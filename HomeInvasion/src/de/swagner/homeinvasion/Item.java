package de.swagner.homeinvasion;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;

import com.google.android.maps.GeoPoint;

public class Item {

	public PendingIntent proximityIntent;
	public Intent dotsProximityIntent;
	private GeoPoint position;
	private LocationManager locationManager;
	private int dotID;
	private Bitmap bmp_item_current;
	private int currentFrame = 0;
	private int shotFrame = 0;
	private boolean destroyedAnim = false;
	private boolean shootAnim = false;
	private boolean destroyed = false;

	public static int id = 0;

	public Item(Context context, LocationManager locationManager, GeoPoint geoPoint) {
		this.position = geoPoint;
		this.locationManager = locationManager;
		dotsProximityIntent = new Intent(GameActivity.itemsProximityIntentAction);
		dotsProximityIntent.putExtra("dotID", id);
		proximityIntent = PendingIntent.getBroadcast(context, id, dotsProximityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		addDotProximityAlert();
		dotID = id;
		++id;

		bmp_item_current = Images.getInstance().bmp_target_f1;
	}

	public GeoPoint getPosition() {
		return position;
	}

	private void addDotProximityAlert() {
		float radius = GameLogic.getInstance().getItemRadius(); // meters
		locationManager.addProximityAlert(getPosition().getLatitudeE6() / 1E6, getPosition().getLongitudeE6() / 1E6, radius, (GameLogic.getInstance().getTimeLeft()*1000)+5000, proximityIntent);
	}

	public void removeItemProximityAlert() {
		shootAnim = true;
		currentFrame = 0;
		locationManager.removeProximityAlert(proximityIntent);
	}

	public int getID() {
		return dotID;
	}
	
	public boolean isShootAnim() {
		return shootAnim;
	}
	
	public GeoPoint getShootAnimPosition() {
		return GameLogic.interpolatePos(GameLogic.getInstance().getPlayer().getPosition(),getPosition(), shotFrame/20.);
	}	

	public Bitmap getBitmap() {
		return bmp_item_current;
	}

	/**
	 * updates item animation for explosion
	 */
	public void update() {
		if(shootAnim) {
			++shotFrame;
			if(shotFrame==20) {
				destroyedAnim=true;
				shootAnim=false;
			}
		}
		
		if (destroyedAnim) {
			if (currentFrame == 1) {
				bmp_item_current = Images.getInstance().bmp_item_f1;
			} else if (currentFrame == 3) {
				bmp_item_current = Images.getInstance().bmp_item_f2;
			} else if (currentFrame == 5) {
				bmp_item_current = Images.getInstance().bmp_item_f3;
			} else if (currentFrame == 7) {
				bmp_item_current = Images.getInstance().bmp_item_f4;
			} else if (currentFrame == 9) {
				bmp_item_current = Images.getInstance().bmp_item_f5;
			} else if (currentFrame == 11) {
				bmp_item_current = Images.getInstance().bmp_item_f6;
			} else if (currentFrame == 13) {
				bmp_item_current = Images.getInstance().bmp_item_f7;
			} else if (currentFrame == 15) {
				bmp_item_current = Images.getInstance().bmp_item_f8;
			} else if (currentFrame == 17) {
				bmp_item_current = Images.getInstance().bmp_item_f9;
			} else if (currentFrame == 19) {
				bmp_item_current = Images.getInstance().bmp_item_f10;
			} else if (currentFrame == 21) {
				bmp_item_current = Images.getInstance().bmp_item_f11;
			} else if (currentFrame == 23) {
				bmp_item_current = Images.getInstance().bmp_item_f12;
			} else if (currentFrame == 25) {
				bmp_item_current = Images.getInstance().bmp_item_f13;
			} else if (currentFrame == 27) {
				bmp_item_current = Images.getInstance().getExplosionResult();
				destroyedAnim=false; 
				destroyed = true;
			}
			++currentFrame;
		}
		if(!destroyed && !destroyedAnim) {
			if (currentFrame == 1) {
				bmp_item_current = Images.getInstance().bmp_target_f1; 
			} else if (currentFrame == 3) {
				bmp_item_current = Images.getInstance().bmp_target_f2;
			} else if (currentFrame == 5) {
				bmp_item_current = Images.getInstance().bmp_target_f3;
			} else if (currentFrame == 7) {
				bmp_item_current = Images.getInstance().bmp_target_f4;
			} else if (currentFrame == 9) {
				bmp_item_current = Images.getInstance().bmp_target_f5;
			} else if (currentFrame == 11) {
				bmp_item_current = Images.getInstance().bmp_target_f4;
			} else if (currentFrame == 13) {
				bmp_item_current = Images.getInstance().bmp_target_f3;
			} else if (currentFrame == 15) {
				bmp_item_current = Images.getInstance().bmp_target_f2;
				currentFrame = 0;
			} 
			++currentFrame;
		}
	}
	
}
