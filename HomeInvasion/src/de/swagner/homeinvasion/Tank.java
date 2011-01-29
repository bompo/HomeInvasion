package de.swagner.homeinvasion;

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
		animPosition=position;
	}

	public GeoPoint getPosition() {
		return position;
	}

	public void setPosition(GeoPoint newPosition) {
		lastPosition = this.position;
		position = newPosition;
//		Log.v("animpos", newPosition.toString() + " " + lastPosition.toString());
		removeTankProximityAlert();
		addTankProximityAlert();
		setAnimatePosition(true);
	}

	private void addTankProximityAlert() {
		float radius = GameLogic.getInstance().getItemRadius(); // meters
		long expiration = -1; // do not expire
		locationManager.addProximityAlert(getPosition().getLatitudeE6() / 1E6, getPosition().getLongitudeE6() / 1E6, radius, 2000, proximityIntent);
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
			setAnimPosition(GameLogic.interpolatePos(getLastPosition(),getPosition(), animPositionCounter / 30.));
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
//		Log.v("animDir", dir + "");
		animDirection = dir;
	}

}
