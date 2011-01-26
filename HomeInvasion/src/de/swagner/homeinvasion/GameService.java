package de.swagner.homeinvasion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.google.android.maps.GeoPoint;

public class GameService extends Service {
	public static final int NOTIF_ID = 1337;
	public static final String UPDATE_UI_ACTION = "de.swagner.homeinvasion.updateui";
	public static final String ADD_TANK_ACTION = "de.swagner.homeinvasion.addtank";
	private Intent updateUIBroadcast = new Intent(UPDATE_UI_ACTION);
	private Intent addTankBroadcast = new Intent(ADD_TANK_ACTION);
	private final Binder binder = new LocalBinder();
	private Timer timer = new Timer();
	private FileOutputStream fOut;
	private Writer writer;

	private Handler mHandler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();

		if (GameLogic.getInstance().getDebugMode()) {
			String state = Environment.getExternalStorageState();

			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/de.swagner.homeinvasion/files/";
			String fileName = "position.txt";

			if (Environment.MEDIA_MOUNTED.equals(state)) {

				try {
					// Make sure the path exists
					boolean exists = (new File(path)).exists();
					if (!exists) {
						new File(path).mkdirs();
					}

					// Open output stream
					fOut = new FileOutputStream(path + fileName);
					writer = new OutputStreamWriter(fOut, "UTF-8");

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

			}
		}

		startService();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return (binder);
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		if (GameLogic.getInstance().getDebugMode()) {
			try {
				writer.close();
				fOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	public void startService() {

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				sendBroadcast(updateUIBroadcast);
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 10) {
					sendBroadcast(addTankBroadcast);
				}
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 100) {
					sendBroadcast(addTankBroadcast);
				}
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 200) {
					sendBroadcast(addTankBroadcast);
				}
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 300) {
					sendBroadcast(addTankBroadcast);
				}
				try {
					if (GameLogic.getInstance().getDebugMode())
						recordSession();
					tankAI();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (GameLogic.getInstance().getParsedMode() && GameLogic.getInstance().isGameReady()) {
					GameLogic.getInstance().setPlayerDirection(Debug.getInstance().getCurrentRecordedDirection());
				}
			}
		}, 0, 1000);
	}

	protected void tankAI() throws IOException {
		for (final Tank tank : GameLogic.getInstance().getTanks()) {

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					try {
						if (tank.getCalcNewRoute()) {
							tank.setOldRoutePosition(tank.getPosition());
							tank.setRouteCounter(1);

							// TODO cache Route and alter only if player moved
							String pairs[] = GameActivity.getDirectionData(tank.getPosition().getLatitudeE6() / 1E6 + "," + tank.getPosition().getLongitudeE6() / 1E6, GameLogic.getInstance()
									.getPlayerLocation().getLatitudeE6()
									/ 1E6 + "," + GameLogic.getInstance().getPlayerLocation().getLongitudeE6() / 1E6);
							String[] lngLat = pairs[1].split(",");
							tank.setNextRoutePosition(new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6)));
							tank.setCalcNewRoute(false);
							Log.v("newRoute", " true ");
						}

						GeoPoint newGP = tank.getNextRoutePosition();
						int newLat = (int) (tank.getOldRoutePosition().getLatitudeE6() + (tank.getSpeed()
								* tank.getRouteCounter()
								* (1 / Math.sqrt(Math.pow(tank.getNextRoutePosition().getLatitudeE6() - tank.getOldRoutePosition().getLatitudeE6(), 2)
										+ Math.pow(tank.getNextRoutePosition().getLongitudeE6() - tank.getOldRoutePosition().getLongitudeE6(), 2))) * (tank.getNextRoutePosition().getLatitudeE6() - tank
								.getOldRoutePosition().getLatitudeE6())));
						int newLog = (int) (tank.getOldRoutePosition().getLongitudeE6() + (tank.getSpeed()
								* tank.getRouteCounter()
								* (1 / Math.sqrt(Math.pow(tank.getNextRoutePosition().getLatitudeE6() - tank.getOldRoutePosition().getLatitudeE6(), 2)
										+ Math.pow(tank.getNextRoutePosition().getLongitudeE6() - tank.getOldRoutePosition().getLongitudeE6(), 2))) * (tank.getNextRoutePosition().getLongitudeE6() - tank
								.getOldRoutePosition().getLongitudeE6())));

						if (Math.sqrt(Math.pow(
								tank.getSpeed()
										* tank.getRouteCounter()
										* (1 / Math.sqrt(Math.pow(tank.getNextRoutePosition().getLatitudeE6() - tank.getOldRoutePosition().getLatitudeE6(), 2)
												+ Math.pow(tank.getNextRoutePosition().getLongitudeE6() - tank.getOldRoutePosition().getLongitudeE6(), 2)))
										* (tank.getNextRoutePosition().getLatitudeE6() - tank.getOldRoutePosition().getLatitudeE6()), 2)
								+ Math.pow(
										tank.getSpeed()
												* tank.getRouteCounter()
												* (1 / Math.sqrt(Math.pow(tank.getNextRoutePosition().getLatitudeE6() - tank.getOldRoutePosition().getLatitudeE6(), 2)
														+ Math.pow(tank.getNextRoutePosition().getLongitudeE6() - tank.getOldRoutePosition().getLongitudeE6(), 2)))
												* (tank.getNextRoutePosition().getLongitudeE6() - tank.getOldRoutePosition().getLongitudeE6()), 2)) < Math.sqrt(Math.pow(tank.getOldRoutePosition()
								.getLatitudeE6() - tank.getNextRoutePosition().getLatitudeE6(), 2)
								+ Math.pow(tank.getOldRoutePosition().getLongitudeE6() - tank.getNextRoutePosition().getLongitudeE6(), 2))) {
							tank.setRouteCounter(tank.getRouteCounter() + 1);
							newGP = new GeoPoint(newLat, newLog);
						} else {
							tank.setCalcNewRoute(true);
						}

						double diffLat = tank.getNextRoutePosition().getLatitudeE6() - newLat;
						double diffLog = tank.getNextRoutePosition().getLongitudeE6() - newLog;
						double normdiffLog = (1/(Math.sqrt(Math.pow(diffLog, 2)+Math.pow(diffLat, 2))))* diffLog;
						double normdiffLat = (1/(Math.sqrt(Math.pow(diffLog, 2)+Math.pow(diffLat, 2))))* diffLat;
						
						tank.setDirection((float) (360-Math.acos(((1 * normdiffLat)+(0*normdiffLog))/(Math.sqrt(Math.pow(1, 2)+Math.pow(0, 2))*Math.sqrt(Math.pow(normdiffLat, 2)+Math.pow(normdiffLog, 2))))*180/Math.PI));
						Log.v("tankdir", tank.getDirection() + "");
						tank.setPosition(newGP);
					} catch (Exception e) {
						Log.e("GhostAI", e.toString());
					}
				}
			});
		}
	}

	public class LocalBinder extends Binder {
		GameService getService() {
			return (GameService.this);
		}
	}

	public void recordSession() throws IOException {
		if (GameLogic.getInstance().isGameReady()) {
			writer.write("" + GameLogic.getInstance().getPlayerLocation().getLatitudeE6());
			writer.write(" ");
			writer.write("" + GameLogic.getInstance().getPlayerLocation().getLongitudeE6());
			writer.write(" ");
			writer.write("" + GameLogic.getInstance().getPlayerDirection() + "\n");
			writer.flush();
		}
	}
}