package de.swagner.homeinvasion;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Gameplay Activity
 * LocationListener, Overlays,   
 * 
 * @author bompo
 *
 */
public class GameActivity extends MapActivity {
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static SensorManager sensorManager;
	
	private List<Sensor> sensors;
	private boolean sersorrunning = false;
	private MapView mapView;
	private MapController mapController;
	private LocationManager locationManager;
	private UfoOverlay ufoOverlay;
	private ItemOverlay itemOverlay;
	private TankOverlay tankOverlay;
	
	private TextView tv_points;
	private TextView tv_time;
	private TextView tv_wave;

	private GameLogic theGame;
	private GameService gameService;
	private Intent gameServiceIntent;

	public static String itemsProximityIntentAction = new String("de.swagner.homeinvasion.item.PROXIMITY_ALERT");
	public static String tankProximityIntentAction = new String("de.swagner.homeinvasion.tank.PROXIMITY_ALERT");

	private ProgressBar mProgress;
	private Handler mHandler = new Handler();

	private Dialog dialog;
	private Location currentLocation;
	private Location debugLocation;

	private String provider;

	private Timer animTimer = new Timer();
	Thread locationFix;	
	
	private ItemProximityIntentReceiver itemProximityIntentReceiver;
	private TankProximityIntentReceiver tankProximityIntentReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.map_layout);
		
		if (!GameLogic.getInstance().isGameReady()) {

			tv_points = (TextView) findViewById(R.id.tv_points);
			tv_wave = (TextView) findViewById(R.id.tv_wave);

			String context = Context.LOCATION_SERVICE;
			locationManager = (LocationManager) getSystemService(context);

			theGame = GameLogic.getInstance();

			// intent for proximity items stuff
			itemProximityIntentReceiver = new ItemProximityIntentReceiver();
			IntentFilter itemIntentFilter = new IntentFilter(itemsProximityIntentAction);
			registerReceiver(itemProximityIntentReceiver, itemIntentFilter);

			// intent for proximity tanks stuff
			tankProximityIntentReceiver = new TankProximityIntentReceiver();
			IntentFilter tankIntentFilter = new IntentFilter(tankProximityIntentAction);
			registerReceiver(tankProximityIntentReceiver, tankIntentFilter);

			mapView = (MapView) findViewById(R.id.map_view);
			if(GameLogic.getInstance().isSatellite()) {
				mapView.setSatellite(true);
			} else {
				mapView.setSatellite(false);
			}
			mapController = mapView.getController();
		
			provider = LocationManager.GPS_PROVIDER;

			//DEBUG (remove for release)
			try {
				locationManager.removeTestProvider(provider);
			} catch (Exception e) {
				
			}

			if (!locationManager.isProviderEnabled(provider)) {
				createGpsDisabledAlert();
				return;
			}
			
			if(!isOnline()) {
				createInternetAlert();
				return;
			}
			
			currentLocation = locationManager.getLastKnownLocation(provider);
			updateWithNewLocation(currentLocation);
			
			locationManager.requestLocationUpdates(provider, 1000, 1f, locationListener);
			
			// find best location provider
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			provider = locationManager.getBestProvider(criteria, true);
					
			locationManager.requestLocationUpdates(provider, 1000, 1f, locationCoarseListener);
			
			if (!Debug.getInstance().getParsedMode()) {
				// sensor setup
				sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
				sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);

				if (sensors.size() > 0) {
					sensorManager.registerListener(mySensorEventListener, sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
					sersorrunning = true;
				} else {
					sersorrunning = false;
				}

			} else {
				// test that mock locations are allowed so a more descriptive
				// error message can be logged
				if (Settings.Secure.getInt(getBaseContext().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) == 0) {
					Log.e("debug", "Mock locations are currently disabled in Settings - this test requires mock locations");
				}

				locationManager.addTestProvider(provider, false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
				locationManager.setTestProviderEnabled(provider, true);

				// in debug mode set recorded positions
				setLocation(Debug.getInstance().getCurrentRecordedPosition().getLatitudeE6() / 1E6, Debug.getInstance().getCurrentRecordedPosition().getLongitudeE6() / 1E6);
			}

			mapView.setBuiltInZoomControls(true);
			mapController.setZoom(19);

			List<Overlay> overlays = mapView.getOverlays();

			// add dot overlay
			itemOverlay = new ItemOverlay(this.getBaseContext());
			overlays.add(itemOverlay);

			// Create a Dialog to let the User know
			// that we're waiting for a GPS Fix
			dialog = ProgressDialog.show(GameActivity.this, "Preparing Invasion...", "Retrieving current Position... Please make sure that GPS is enabled and your internet connection is working. This game can only played outside.", true);
			dialog.setCancelable(true);
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
			
			if (!theGame.isGameReady()) {
				// takes long so we add a progress bar
				mProgress = (ProgressBar) findViewById(R.id.progressbar);
				mProgress.setVisibility(View.VISIBLE);
				mProgress.setIndeterminate(true);

				// Start lengthy operation in a background thread
				locationFix = new Thread(new Runnable() {
					@Override
					public void run() {
						while (theGame.getPlayer().getPosition() == null) {
							// Wait for first GPS Fix
							// (do nothing until loc != null)
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						updateWithNewLocation(currentLocation);
						
						createDots();
						// Update the progress bar
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								// After receiving first GPS Fix dismiss the
								// Progress Dialog
								dialog.dismiss();

								mProgress.setVisibility(View.GONE);
								mapView.invalidate();
								if (GameLogic.getInstance().isSound()) {
									MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.intro);
									try {
										mp.prepare();
									} catch (IllegalStateException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
										
										@Override
										public void onPrepared(MediaPlayer mp) {
											mp.start();
										}
									});
									mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
										
										@Override
										public void onCompletion(MediaPlayer mp) {
											mp.stop();
											mp.release();
										}
									});
								}
								theGame.startGame();
							}
						});

					}
				});
				locationFix.start();
			}

			// add tank overlay
			tankOverlay = new TankOverlay();
			overlays.add(tankOverlay);
			
			// add ufo overlay
			ufoOverlay = new UfoOverlay();
			overlays.add(ufoOverlay);

			updateWithNewLocation(currentLocation);

			tv_time = (TextView) findViewById(R.id.tv_time);

			gameServiceIntent = new Intent(this, GameService.class);
			bindService(gameServiceIntent, onService, BIND_AUTO_CREATE);

		}


	}

	private void updateWithNewLocation(Location location) {
		if (isBetterLocation(location, currentLocation) && GameLogic.getInstance().getPlayer().isAlive()) {
			currentLocation = location;
		
			// Update the map location.
			GeoPoint point = new GeoPoint((int) (currentLocation.getLatitude() * 1E6), (int) (currentLocation.getLongitude() * 1E6));

			mapController.animateTo(point);
			theGame.getPlayer().setPosition(point);
		}
	}

	/**
	 * listener for location changes
	 */
	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			try {
				locationManager.removeUpdates(locationListener);
			} catch (Exception e) {
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	
	/**
	 * listener for location changes
	 */
	private final LocationListener locationCoarseListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			try {
				locationManager.removeUpdates(locationCoarseListener);
			} catch (Exception e) {
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	/**
	 * sensor listener
	 */
	private SensorEventListener mySensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			GameLogic.getInstance().getPlayer().setDirection(event.values[0]);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try{
			locationFix.stop();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		theGame.shutdownGame();
		
		unregisterReceiver(addTankReceiver);
		unregisterReceiver(updateUIReceiver);
		unregisterReceiver(tankProximityIntentReceiver);
		unregisterReceiver(itemProximityIntentReceiver);
		
		if (GameLogic.getInstance().isAnimation()) {
			try {
				animTimer.cancel();
			} catch (Exception e) {
			}
		}

		if (sersorrunning) {
			sensorManager.unregisterListener(mySensorEventListener);
		}

		try {
			locationManager.removeUpdates(locationListener);
			locationManager.removeUpdates(locationCoarseListener);
		} catch (Exception e) {
		}

		try {
			unbindService(onService);
			stopService(gameServiceIntent);
		} catch (Exception e) {
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		registerReceiver(addTankReceiver, new IntentFilter(GameService.ADD_TANK_ACTION));
		registerReceiver(updateUIReceiver, new IntentFilter(GameService.UPDATE_UI_ACTION));
	}
	
	@Override
	public void onStop() {
		super.onStart();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (sersorrunning) {
			sensorManager.registerListener(mySensorEventListener, sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
		}

		// animate with ca30fps
		if (GameLogic.getInstance().isAnimation()) {
			animTimer = new Timer();
			try{
			animTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					ufoOverlay.update();
					tankOverlay.update();
					itemOverlay.update();
					mapView.postInvalidate();
				}
			}, 0, 33);
			} catch (Exception e) {}
		}
		
		if (provider==null) {
			createGpsDisabledAlert();
			return;
		}
		
		if(!isOnline()) {
			createInternetAlert();
			return;
		}
		
		try {
			locationManager.removeUpdates(locationListener);
			locationManager.removeUpdates(locationCoarseListener);
		} catch (Exception e) {
		}
		locationManager.requestLocationUpdates(provider, 1000, 1f, locationListener);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (GameLogic.getInstance().isAnimation()) {
			try {
				animTimer.cancel();
			} catch (Exception e) {
			}			
		}
		
		if (sersorrunning) {
			sensorManager.unregisterListener(mySensorEventListener);
		}
		
		try {
			locationManager.removeUpdates(locationListener);
			locationManager.removeUpdates(locationCoarseListener);
		} catch (Exception e) {
		}		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// IMPORTANT: This method must return true if your Activity
		// is displaying driving directions. Otherwise return false.
		return false;
	}

	/**
	 * uses current player location to place dots
	 * uses a grid placed around the player and calculates from each gridpoint a route to the player
	 */
	private void createDots() {
		GeoPoint routePos;
		GeoPoint nextPos;
		GeoPoint prevPos;
		boolean nextRoute = false;
		int cntRoute = 1;
		int radius = 10;
		while (GameLogic.getInstance().getItems().size() <= GameLogic.getInstance().getMaxTargets()) {
			radius = radius+5; 
			for (int y = -radius; y <= radius; y = y + radius) {
				for (int x = -radius; x <= radius; x = x + radius) {
					try {

						double geoLat = theGame.getPlayer().getPosition().getLatitudeE6() + (x * theGame.getGameRadius());
						double geoLng = theGame.getPlayer().getPosition().getLongitudeE6() + (y * theGame.getGameRadius());

						// cals route from player to grid position
						routePos = new GeoPoint((int) (geoLat), (int) (geoLng));

						String pairs[] = GameActivity.getDirectionData(routePos.getLatitudeE6() / 1E6 + "," + routePos.getLongitudeE6() / 1E6, GameLogic.getInstance().getPlayer().getPosition()
								.getLatitudeE6()
								/ 1E6 + "," + GameLogic.getInstance().getPlayer().getPosition().getLongitudeE6() / 1E6);
						cntRoute = 1;
						if(pairs.length<2) break;
						String[] nextlngLat = pairs[cntRoute].split(",");
						String[] prevlngLat = pairs[cntRoute - 1].split(",");

						nextPos = new GeoPoint((int) (Double.parseDouble(nextlngLat[1]) * 1E6), (int) (Double.parseDouble(nextlngLat[0]) * 1E6));
						prevPos = new GeoPoint((int) (Double.parseDouble(prevlngLat[1]) * 1E6), (int) (Double.parseDouble(prevlngLat[0]) * 1E6));

						int l = 1;

						while (cntRoute < pairs.length - 1) {
							if (nextRoute) {
								l = 1;
								++cntRoute;
								nextlngLat = pairs[cntRoute].split(",");
								prevlngLat = pairs[cntRoute - 1].split(",");
								nextPos = new GeoPoint((int) (Double.parseDouble(nextlngLat[1]) * 1E6), (int) (Double.parseDouble(nextlngLat[0]) * 1E6));
								prevPos = new GeoPoint((int) (Double.parseDouble(prevlngLat[1]) * 1E6), (int) (Double.parseDouble(prevlngLat[0]) * 1E6));
								nextRoute = false;
								//Log.v("newRoute", " true ");
							}

							// interpolPos = prevPos + 100 * counter * ||nextPos
							// -
							// prevPos||
							GeoPoint dotPos = nextPos;
							int newLat = (int) (prevPos.getLatitudeE6() + (theGame.getItemDistance() * l
									* (1 / Math.sqrt(Math.pow(nextPos.getLatitudeE6() - prevPos.getLatitudeE6(), 2) + Math.pow(nextPos.getLongitudeE6() - prevPos.getLongitudeE6(), 2))) * (nextPos
									.getLatitudeE6() - prevPos.getLatitudeE6())));
							int newLog = (int) (prevPos.getLongitudeE6() + (theGame.getItemDistance() * l
									* (1 / Math.sqrt(Math.pow(nextPos.getLatitudeE6() - prevPos.getLatitudeE6(), 2) + Math.pow(nextPos.getLongitudeE6() - prevPos.getLongitudeE6(), 2))) * (nextPos
									.getLongitudeE6() - prevPos.getLongitudeE6())));

							//Log.v("newpos", newLat + " " + newLog);

							// if( ||prevPos + 100 * counter * ||nextPos -
							// prevPos|||| <||nextPos - prevPos|| )
							if (Math.sqrt(Math.pow(
									theGame.getItemDistance() * l
											* (1 / Math.sqrt(Math.pow(nextPos.getLatitudeE6() - prevPos.getLatitudeE6(), 2) + Math.pow(nextPos.getLongitudeE6() - prevPos.getLongitudeE6(), 2)))
											* (nextPos.getLatitudeE6() - prevPos.getLatitudeE6()), 2)
									+ Math.pow(
											theGame.getItemDistance()
													* l
													* (1 / Math.sqrt(Math.pow(nextPos.getLatitudeE6() - prevPos.getLatitudeE6(), 2) + Math.pow(nextPos.getLongitudeE6() - prevPos.getLongitudeE6(), 2)))
													* (nextPos.getLongitudeE6() - prevPos.getLongitudeE6()), 2)) < Math.sqrt(Math.pow(prevPos.getLatitudeE6() - nextPos.getLatitudeE6(), 2)
									+ Math.pow(prevPos.getLongitudeE6() - nextPos.getLongitudeE6(), 2))) {
								++l;
								dotPos = new GeoPoint(newLat, newLog);
							} else {
								nextRoute = true;
							}
							//Log.v("placeDot", dotPos.toString());

							if(!this.isFinishing()) {
								if (GameLogic.CalculationByDistance(theGame.getPlayer().getPosition(), routePos) > GameLogic.CalculationByDistance(theGame.getPlayer().getPosition(), dotPos)) {
									theGame.addItem(getApplicationContext(), locationManager, dotPos);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}

	}

	/**
	 * calls maps.google.com to get a from srcPlace to destPlace (both can be geopoints)
	 * @param srcPlace
	 * @param destPlace
	 * @return
	 */
	public static String[] getDirectionData(String srcPlace, String destPlace) {

		srcPlace = java.net.URLEncoder.encode(srcPlace);
		destPlace = java.net.URLEncoder.encode(destPlace);

		String urlString = "http://maps.google.com/maps?f=d&hl=en&dirflg=w&saddr=" + srcPlace + "&daddr=" + destPlace + "&ie=UTF8&0&om=0&output=kml";

		Log.d("URL", urlString);
		Document doc = null;
		HttpURLConnection urlConnection = null;
		URL url = null;
		String pathConent = "";

		try {
			url = new URL(urlString.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(urlConnection.getInputStream());
		} catch (Exception e) {
		}

		NodeList nl = doc.getElementsByTagName("LineString");
		for (int s = 0; s < nl.getLength(); s++) {
			Node rootNode = nl.item(s);
			NodeList configItems = rootNode.getChildNodes();
			for (int x = 0; x < configItems.getLength(); x++) {
				Node lineStringNode = configItems.item(x);
				NodeList path = lineStringNode.getChildNodes();
				pathConent = path.item(0).getNodeValue();
			}
		}
		String[] tempContent = pathConent.split(" ");
		
		return tempContent;
	}

	/**
	 * decreases the Time and updates the text and points
	 */
	public void updateUI() {
		if (theGame.isGameReady()) {
			theGame.decreaseTime();
			tv_time.setText(Math.round(theGame.getTimeLeft() / 60) + "min " + String.format("%02d", theGame.getTimeLeft() % 60) + "sec");
			tv_points.setText("Points " + theGame.getPoints());
			tv_wave.setText("Wave " + theGame.getWave());
			mapView.postInvalidate();
		}
		if (theGame.isGameReady() && theGame.isGameOver()) {
			theGame.shutdownGame();
			Intent ScoreIntent = new Intent(getBaseContext(), de.swagner.homeinvasion.ScoreActivity.class);
			startActivity(ScoreIntent);
			finish();
		}
		if (Debug.getInstance().getParsedMode()) {
			setLocation(Debug.getInstance().getCurrentRecordedPosition().getLatitudeE6() / 1E6, Debug.getInstance().getCurrentRecordedPosition().getLongitudeE6() / 1E6);
		}
	}

	/**
	 * add tank to game
	 * tank gets a random position around user
	 */
	public void addTank() {
		Double geoLat, geoLng;

		// left/right or up/down
		int leftRightOrUpDown = (int) Math.floor((Math.random() * 2) + 1);
		if (leftRightOrUpDown == 1) {
			// left or right
			int leftRight = (int) Math.floor((Math.random() * 2) + 1);
			if (leftRight == 1) {
				// left
				geoLat = (double) (theGame.getPlayer().getPosition().getLatitudeE6() + ((-6 * theGame.getItemDistance())));
				int rnd = (int) Math.floor((Math.random() * 6) + -6);
				geoLng = (double) (theGame.getPlayer().getPosition().getLongitudeE6() + ((rnd * theGame.getItemDistance())));
			} else {
				// right
				geoLat = (double) (theGame.getPlayer().getPosition().getLatitudeE6() + ((6 * theGame.getItemDistance())));
				int rnd = (int) Math.floor((Math.random() * 6) + -6);
				geoLng = (double) (theGame.getPlayer().getPosition().getLongitudeE6() + ((rnd * theGame.getItemDistance())));
			}
		} else {
			// top or down
			int topDown = (int) Math.floor((Math.random() * 2) + 1);
			if (topDown == 1) {
				// top
				geoLng = (double) (theGame.getPlayer().getPosition().getLongitudeE6() + ((6 * theGame.getItemDistance())));
				int rnd = (int) Math.floor((Math.random() * 6) + -6);
				geoLat = (double) (theGame.getPlayer().getPosition().getLatitudeE6() + ((rnd * theGame.getItemDistance())));
			} else {
				// down
				geoLng = (double) (theGame.getPlayer().getPosition().getLongitudeE6() + ((-6 * theGame.getItemDistance())));
				int rnd = (int) Math.floor((Math.random() * 6) + -6);
				geoLat = (double) (theGame.getPlayer().getPosition().getLatitudeE6() + ((rnd * theGame.getItemDistance())));
			}
		}

		GeoPoint point = new GeoPoint(geoLat.intValue(), geoLng.intValue());
		GeoPoint nextPos =point;

		try {
			// search nearest street for this point
			String pairs[] = GameActivity.getDirectionData(point.getLatitudeE6() / 1E6 + "," + point.getLongitudeE6() / 1E6, GameLogic.getInstance().getPlayer().getPosition().getLatitudeE6() / 1E6 + ","
					+ GameLogic.getInstance().getPlayer().getPosition().getLongitudeE6() / 1E6);

			String[] nextlngLat = pairs[0].split(",");

			nextPos = new GeoPoint((int) (Double.parseDouble(nextlngLat[1]) * 1E6), (int) (Double.parseDouble(nextlngLat[0]) * 1E6));
		} catch (Exception e) {
		}

		theGame.addTank(getApplicationContext(), locationManager, nextPos);
	}

	/**
	 * refreshes UI if GameService says so
	 */
	private BroadcastReceiver updateUIReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI();
		}
	};

	/**
	 * calls add tank if GameService says so
	 */
	private BroadcastReceiver addTankReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			addTank();
		}
	};

	/**
	 * connects to GameService (for timer events)
	 */
	private ServiceConnection onService = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder rawBinder) {
			gameService = ((GameService.LocalBinder) rawBinder).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			gameService = null;
		}
	};

	/**
	 * sets mock location for debug parser
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setLocation(double latitude, double longitude) {
		debugLocation = new Location(provider);
		debugLocation.setTime(System.currentTimeMillis());
		debugLocation.setLatitude(latitude);
		debugLocation.setLongitude(longitude);
		locationManager.setTestProviderLocation(provider, debugLocation);
	}

	/**
	 * shows "really quit?" dialog to user
	 */
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Really quit current game? All your progress will be lost").setCancelable(false).setPositiveButton("Quit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		});
		builder.setNegativeButton("Resume game", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * warning if GPS is disabled
	 */
	private void createGpsDisabledAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS is disabled! Would you like to enable it?").setCancelable(false).setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				showGpsOptions();
				finish();
			}
		});
		builder.setNegativeButton("Do nothing", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * open location settings
	 */
	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}
	
	/**
	 * checks if client is online
	 * @return online status
	 */
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * warning if no internet connection is available
	 */
	private void createInternetAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("No internet connection available").setCancelable(false).setPositiveButton("Enable Internet", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				showInternetOptions();
				finish();
			}
		});
		builder.setNegativeButton("Do nothing", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * open connection settings
	 */
	private void showInternetOptions() {
		Intent internetOptionsIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
		startActivity(internetOptionsIntent);
	}
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (location == null) {
	        // a new null location is always bad
	        return false;
	    }
		
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    } else if (isNewer && !isLessAccurate && !isMoreAccurate) {
	    	return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

}
