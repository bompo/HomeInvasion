package de.swagner.homeinvasion;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class Tank {

	public PendingIntent proximityIntent;
	public Intent tankProximityIntent;
	private GeoPoint position;
	private LocationManager locationManager;
	private int tankID;
	private int speed;
	private Bitmap bmp_tank_f1;
	private Bitmap bmp_tank_f2;
	private Bitmap bmp_tank_f3;
	private Bitmap bmp_tank_f4;
	private Bitmap bmp_tank_f5;
	private Bitmap bmp_tank_f6;
	private Bitmap bmp_tank_f7;
	private Bitmap bmp_tank_f8;
	private Bitmap bmp_tank_f9;
	private Bitmap bmp_tank_f10;
	private Bitmap bmp_tank_current;
	private boolean calcNewRoute;
	private GeoPoint oldRoutePosition;
	private GeoPoint nextRoutePosition;
	private int routeCounter;

	// AI
	private String pairs[];
	private String[] lngLat;
	private GeoPoint newGP;
	private GeoPoint oldGP;
	private float newDir;
	private int newLat;
	private int newLog;

	private CopyOnWriteArrayList<GeoPoint> routeToPlayer;
	private int currentRouteCounter;

	private int currentFrame = 0;
	private int animPositionCounter;
	private int animDirectionCounter;

	private boolean animatePosition;
	private GeoPoint lastPosition;
	private GeoPoint animPosition;

	private float direction;
	private float animDirection;
	private float lastDirection;

	private boolean animateDirection;

	public static int id = 0;

	public Tank(Context context, LocationManager locationManager, GeoPoint geoPoint) {
		this.position = geoPoint;
		this.locationManager = locationManager;
		tankProximityIntent = new Intent(GameActivity.tankProximityIntentAction);
		tankProximityIntent.putExtra("ghostID", id);
		proximityIntent = PendingIntent.getBroadcast(context, (int) (id + 1E6), tankProximityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		addTankProximityAlert();
		tankID = id;
		speed = GameLogic.getInstance().getTankSpeed();

		bmp_tank_f1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank_f1);
		bmp_tank_f2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank_f2);
		bmp_tank_f3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank_f3);
		bmp_tank_f4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank_f4);
		bmp_tank_f5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank_f5);
		bmp_tank_f6 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank_f6);
		bmp_tank_f7 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank_f7);
		bmp_tank_f8 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank_f8);
		bmp_tank_f9 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank_f9);
		bmp_tank_f10 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank_f10);
		bmp_tank_current = bmp_tank_f1;

		++id;
		calcNewRoute = true;

		animPositionCounter = 1;
		animatePosition = false;
		animPosition = position;
		currentRouteCounter = 0;
		routeToPlayer = new CopyOnWriteArrayList<GeoPoint>();

		calcNewRoute();

	}

	public GeoPoint getPosition() {
		return position;
	}

	public void setPosition(GeoPoint newPosition) {
		lastPosition = this.position;
		position = newPosition;
		// Log.v("animpos", newPosition.toString() + " " +
		// lastPosition.toString());
		removeTankProximityAlert();
		addTankProximityAlert();
		setAnimatePosition(true);
	}

	private void addTankProximityAlert() {
		locationManager.addProximityAlert(getPosition().getLatitudeE6() / 1E6, getPosition().getLongitudeE6() / 1E6, GameLogic.getInstance().getItemRadius(), 2000, proximityIntent);
	}

	public void removeTankProximityAlert() {
		locationManager.removeProximityAlert(proximityIntent);
	}

	public int getID() {
		return tankID;
	}

	public int getSpeed() {
		return speed;
	}

	public Bitmap getBitmap() {
		return bmp_tank_current;
	}

	public boolean getCalcNewRoute() {
		return calcNewRoute;
	}

	public void setCalcNewRoute(boolean b) {
		calcNewRoute = b;
	}

	public void setOldRoutePosition(GeoPoint position) {
		oldRoutePosition = position;
	}

	public GeoPoint getOldRoutePosition() {
		return oldRoutePosition;
	}

	public void setNextRoutePosition(GeoPoint geoPoint2) {
		nextRoutePosition = geoPoint2;
	}

	public GeoPoint getNextRoutePosition() {
		return nextRoutePosition;
	}

	public void setRouteCounter(int i) {
		routeCounter = i;
	}

	public int getRouteCounter() {
		return routeCounter;
	}

	/**
	 * updates tank animation
	 */
	public void update() {
		if (currentFrame == 1) {
			bmp_tank_current = bmp_tank_f1;
		} else if (currentFrame == 3) {
			bmp_tank_current = bmp_tank_f2;
		} else if (currentFrame == 5) {
			bmp_tank_current = bmp_tank_f3;
		} else if (currentFrame == 7) {
			bmp_tank_current = bmp_tank_f4;
		} else if (currentFrame == 9) {
			bmp_tank_current = bmp_tank_f5;
		} else if (currentFrame == 49) {
			bmp_tank_current = bmp_tank_f4;
		} else if (currentFrame == 51) {
			bmp_tank_current = bmp_tank_f3;
		} else if (currentFrame == 53) {
			bmp_tank_current = bmp_tank_f2;
		} else if (currentFrame == 55) {
			bmp_tank_current = bmp_tank_f1;
		} else if (currentFrame == 95) {
			bmp_tank_current = bmp_tank_f6;
		} else if (currentFrame == 97) {
			bmp_tank_current = bmp_tank_f7;
		} else if (currentFrame == 99) {
			bmp_tank_current = bmp_tank_f8;
		} else if (currentFrame == 101) {
			bmp_tank_current = bmp_tank_f9;
		} else if (currentFrame == 103) {
			bmp_tank_current = bmp_tank_f10;
		} else if (currentFrame == 143) {
			bmp_tank_current = bmp_tank_f9;
		} else if (currentFrame == 145) {
			bmp_tank_current = bmp_tank_f8;
		} else if (currentFrame == 147) {
			bmp_tank_current = bmp_tank_f7;
		} else if (currentFrame == 149) {
			bmp_tank_current = bmp_tank_f6;
		} else if (currentFrame == 151) {
			bmp_tank_current = bmp_tank_f1;
		} else if (currentFrame == 191) {
			currentFrame = 0;
		}

		++currentFrame;

		if (isAnimatePosition()) {
			setAnimPosition(GameLogic.interpolatePos(getLastPosition(), getPosition(), animPositionCounter / 30.));
			animPositionCounter = animPositionCounter + 1;
			if (animPositionCounter == 30) {
				animPositionCounter = 1;
				setAnimatePosition(false);
			}
		}

		if (isAnimateDirection()) {
			setAnimDirection(GameLogic.interpolateDir(getLastDirection(), getDirection(), animDirectionCounter / 30.));
			animDirectionCounter = animDirectionCounter + 1;
			if (animDirectionCounter == 30) {
				animDirectionCounter = 1;
				setAnimateDirection(false);
			}
		}

	}

	public boolean isAnimatePosition() {
		return animatePosition;
	}

	public void setAnimatePosition(boolean animatePosition) {
		this.animatePosition = animatePosition;
	}

	public GeoPoint getAnimPosition() {
		return animPosition;
	}

	public void setAnimPosition(GeoPoint animPosition) {
		this.animPosition = animPosition;
	}

	public GeoPoint getLastPosition() {
		return lastPosition;
	}

	public boolean isAnimateDirection() {
		return animateDirection;
	}

	public void setAnimateDirection(boolean animateDirection) {
		this.animateDirection = animateDirection;
	}

	public void setDirection(float dir) {
		this.lastDirection = this.direction;
		this.direction = dir;
		setAnimateDirection(true);
	}

	public float getDirection() {
		return direction;
	}

	public float getLastDirection() {
		return lastDirection;
	}

	public float getAnimDirection() {
		return animDirection;
	}

	public void setAnimDirection(float dir) {
		// Log.v("animDir", dir + "");
		animDirection = dir;
	}

	public CopyOnWriteArrayList<GeoPoint> getRouteToPlayer() {
		return routeToPlayer;
	}

	public void setRouteToPlayer(CopyOnWriteArrayList<GeoPoint> routeToPlayer) {
		this.routeToPlayer = routeToPlayer;
	}

	public int getCurrentRouteCounter() {
		return currentRouteCounter;
	}

	public void setCurrentRouteCounter(int currentRouteCounter) {
		this.currentRouteCounter = currentRouteCounter;
	}

	public void calcNewRoute() {

		try {
			setRouteCounter(1);
			setCurrentRouteCounter(0);

			pairs = GameActivity.getDirectionData(getPosition().getLatitudeE6() / 1E6 + "," + getPosition().getLongitudeE6() / 1E6, GameLogic.getInstance().getPlayerLocation().getLatitudeE6() / 1E6
					+ "," + GameLogic.getInstance().getPlayerLocation().getLongitudeE6() / 1E6);

			getRouteToPlayer().clear();
			for (int i = 1; i < pairs.length; ++i) {
				lngLat = pairs[i].split(",");
				getRouteToPlayer().add(new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6)));
			}
			Log.v("newRoute", "for tank " + getID());
			for (GeoPoint p : getRouteToPlayer()) {
				Log.v("tank " + getID(), p.toString());
			}

			oldGP = getPosition();
			newGP = getRouteToPlayer().get(getCurrentRouteCounter());
		} catch (Exception e) {
			Log.e("tankAICalcNewRoute", e.toString());
			e.printStackTrace();
		}

	}

	public void interpolRoute() {
		try {
			//refresh route?
			if(GameLogic.getInstance().getTimeLimit()%100==0) {
				calcNewRoute();
				return;
			}			

			newLat = (int) (oldGP.getLatitudeE6() + (getSpeed() * getRouteCounter()
					* (1 / Math.sqrt(Math.pow(newGP.getLatitudeE6() - oldGP.getLatitudeE6(), 2) + Math.pow(newGP.getLongitudeE6() - oldGP.getLongitudeE6(), 2))) * (newGP.getLatitudeE6() - oldGP
					.getLatitudeE6())));
			newLog = (int) (oldGP.getLongitudeE6() + (getSpeed() * getRouteCounter()
					* (1 / Math.sqrt(Math.pow(newGP.getLatitudeE6() - oldGP.getLatitudeE6(), 2) + Math.pow(newGP.getLongitudeE6() - oldGP.getLongitudeE6(), 2))) * (newGP.getLongitudeE6() - oldGP
					.getLongitudeE6())));

			// check if end of current interval is reached
			if (Math.sqrt(
					Math.pow(getSpeed() * getRouteCounter() * (1 / Math.sqrt(
							Math.pow(newGP.getLatitudeE6() - oldGP.getLatitudeE6(), 2) + 
							Math.pow(newGP.getLongitudeE6() - oldGP.getLongitudeE6(), 2)))
						* (newGP.getLatitudeE6() - oldGP.getLatitudeE6()), 2) +
					Math.pow(getSpeed() * getRouteCounter() * (1 / Math.sqrt(
							Math.pow(newGP.getLatitudeE6() - oldGP.getLatitudeE6(), 2) + 
							Math.pow(newGP.getLongitudeE6() - oldGP.getLongitudeE6(), 2)))
						* (newGP.getLongitudeE6() - oldGP.getLongitudeE6()), 2)) 
					< 
					Math.sqrt(
							Math.pow(oldGP.getLatitudeE6() - newGP.getLatitudeE6(), 2) +
							Math.pow(oldGP.getLongitudeE6() - newGP.getLongitudeE6(), 2))) {
				setRouteCounter(getRouteCounter() + 1);
			} else {
				setRouteCounter(1);
				setCurrentRouteCounter(getCurrentRouteCounter() + 1);
				
				// check if route contains enough new points, 
				// if not force recalc
				if (getCurrentRouteCounter() > getRouteToPlayer().size()) {
					calcNewRoute();
					return;
				}				

				oldGP = getPosition();
				newGP = getRouteToPlayer().get(getCurrentRouteCounter());
				Log.v("Tank", "tank currentRoute " +getCurrentRouteCounter());
				return;
			}

			newDir = (float) Math.atan2((oldGP.getLatitudeE6() - newGP.getLatitudeE6()), (oldGP.getLongitudeE6() - newGP.getLongitudeE6()));
			newDir = (float) (270 - newDir * (180 / Math.PI));
			setDirection(newDir);

			setPosition(new GeoPoint(newLat, newLog));
		} catch (Exception e) {
			Log.e("TankAI", e.toString());
			e.printStackTrace();
		}
	}
}
