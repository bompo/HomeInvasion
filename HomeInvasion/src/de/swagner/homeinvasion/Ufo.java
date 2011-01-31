package de.swagner.homeinvasion;

import com.google.android.maps.GeoPoint;

public class Ufo {

	private GeoPoint lastLocation;
	private GeoPoint animLocation;
	private GeoPoint Location;

	private float direction;
	private float animDirection;
	private float lastDirection;
	private boolean animateLocation;
	private boolean animateDirection;
	
	public Ufo() {
		direction = 0;
		lastDirection = 0;
		animDirection = 0;		
		animateLocation=false;
	}
	
	public void setLocation(GeoPoint Location) {
		this.lastLocation = this.Location;
		this.Location = Location;
		if(animLocation==null) animLocation=Location;
		if(lastLocation==null) lastLocation=Location;
		setAnimateLocation(true);
	}
	
	public void setAnimLocation(GeoPoint animLocation) {
		this.animLocation = animLocation;
	}
	
	public GeoPoint getAnimLocation() {	
		if(!GameLogic.getInstance().isAnimation()) return this.Location;
		return animLocation;
	} 

	public GeoPoint getLocation() {
		return Location;
	}
	
	public GeoPoint getLastLocation() {
		return lastLocation;
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
		if(!GameLogic.getInstance().isAnimation()) return this.direction;
		return animDirection;
	} 
	
	public void setAnimDirection(float dir) {	
//		Log.v("animDir", dir + "");
		animDirection = dir;
	} 
	
	public boolean isAnimateLocation() {
		return animateLocation;
	}

	public void setAnimateLocation(boolean animate) {
		this.animateLocation = animate;
	}
	
	public boolean isAnimateDirection() {
		return animateDirection;
	}

	public void setAnimateDirection(boolean animateDirection) {
		this.animateDirection = animateDirection;
	}
}
