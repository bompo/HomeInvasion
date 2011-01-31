package de.swagner.homeinvasion;

import com.google.android.maps.GeoPoint;

public class Ufo {

	private GeoPoint lastPosition;
	private GeoPoint animPosition;
	private GeoPoint position;

	private float direction;
	private float animDirection;
	private float lastDirection;
	private boolean animateLocation;
	private boolean animateDirection;
	
	private boolean alive=true;
	
	public Ufo() {
		direction = 0;
		lastDirection = 0;
		animDirection = 0;		
		animateLocation=false;
		alive=true;
	}
	
	public void setPosition(GeoPoint position) {
		this.lastPosition = this.position;
		this.position = position;
		if(animPosition==null) animPosition=position;
		if(lastPosition==null) lastPosition=position;
		setAnimateLocation(true);
	}
	
	public void setAnimPosition(GeoPoint animPosition) {
		this.animPosition = animPosition;
	}
	
	public GeoPoint getAnimPosition() {	
		if(!GameLogic.getInstance().isAnimation()) return this.position;
		return animPosition;
	} 

	public GeoPoint getPosition() {
		return position;
	}
	
	public GeoPoint getLastPosition() {
		return lastPosition;
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
	
	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
