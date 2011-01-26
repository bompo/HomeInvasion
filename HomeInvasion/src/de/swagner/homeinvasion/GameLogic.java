package de.swagner.homeinvasion;

import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public final class GameLogic {

	private static GameLogic instance;
	
	final static int EASY_MODE = 25;
	final static int MEDIUM_MODE = 35;
	final static int HARD_MODE = 55;
	
	final static int TANK_SLOW = 10;
	final static int TANK_MEDIUM = 20;
	final static int TANK_FAST = 30;
	
	final static int SHORT_LENGTH = 600;
	final static int MEDIUM_LENGTH = 900;
	final static int LONG_LENGTH = 1200;
	
	final static String prefs = "Settings";

	private CopyOnWriteArraySet<Item> items;
	private CopyOnWriteArraySet<Tank> tanks;
	private int points;
	private boolean gameReady;
	private int timeLeft;
	private int timeLimit;
	private int tankSpeed;
	private GeoPoint lastPlayerLocation;
	private GeoPoint animPlayerLocation;
	private GeoPoint playerLocation;

	private float playerDirection;
	private float animPlayerDirection;
	private float lastPlayerDirection;
	private boolean animatePlayerLocation;
	private boolean animatePlayerDirection;

	private int gameRadius;
	private int dotRadius;
	private int itemDistance;
	private int maxTargets;

	private boolean gameOver;
	private boolean victory;

	private boolean debugMode;
	private boolean parsedMode;
	
	//game settings vars
	private boolean sound;
	private boolean animation;
	private boolean satellite;

	private GameLogic() {
		items = new CopyOnWriteArraySet<Item>();
		tanks = new CopyOnWriteArraySet<Tank>();
		points = 0;
		gameReady = false;
		timeLimit = 1200;
		timeLeft = timeLimit;
		gameRadius = 100;
		dotRadius = 50;
		itemDistance = 400;
		tankSpeed = 15;
		victory = false;
		gameOver = false;
		Tank.id = 0;
		Item.id = 0;
		playerDirection = 0;
		lastPlayerDirection = 0;
		animPlayerDirection = 0;
		maxTargets = 50;

		sound = false;
		animation =true;
		satellite=true;
		
		debugMode = false;
		parsedMode = false;
		
		animatePlayerLocation=false;
	}

	public synchronized static GameLogic getInstance() {
		if (instance == null) {
			instance = new GameLogic();
		}
		return instance;
	}

	public void setItems(CopyOnWriteArraySet<Item> items) {
		this.items = items;
	}

	public void setTanks(CopyOnWriteArraySet<Tank> tanks) {
		this.tanks = tanks;
	}

	public CopyOnWriteArraySet<Item> getItems() {
		return items;
	}

	public HashSet<GeoPoint> getItemGeoPoints() {
		HashSet<GeoPoint> geoPoints = new HashSet<GeoPoint>();
		for (Item item : items) {
			geoPoints.add(item.getGeoPoint());
		}
		return geoPoints;
	}

	public HashSet<GeoPoint> getTankGeoPoints() {
		HashSet<GeoPoint> geoPoints = new HashSet<GeoPoint>();
		for (Tank tank : tanks) {
			geoPoints.add(tank.getPosition());
		}
		return geoPoints;
	}

	public boolean addItem(Context context, LocationManager locationManager, GeoPoint geoPoint) {
		if(getItemGeoPoints().size()>maxTargets) return false;
				
		// FIXME quick hack for finding doubles
		if (!getItemGeoPoints().add(geoPoint))
			return false;
		
		for(GeoPoint dotGeoPoint:getItemGeoPoints()) {
			if(CalculationByDistance(geoPoint, dotGeoPoint)<dotRadius) return false;
		}

		Item newItem = new Item(context, locationManager, geoPoint);
		return items.add(newItem);
	}

	public boolean addTank(Context context, LocationManager locationManager, GeoPoint geoPoint) {
		if (tanks.size() > 3)
			return false;
		Tank newTank = new Tank(context, locationManager, geoPoint);
		return tanks.add(newTank);
	}

	public void incPoints() {
		++points;
	}

	public int getPoints() {
		return points;
	}

	public void startGame() {
		gameReady = true;
	}

	public boolean isGameReady() {
		return gameReady;
	}

	public int getTimeLeft() {
		return timeLeft;
	}

	public CopyOnWriteArraySet<Tank> getTanks() {
		return tanks;
	}

	public void decreaseTime() {
		// TODO use TimeStamps here
		if (gameReady && timeLeft > 0) {
			--timeLeft;
		}
		if (timeLeft <= 0) {
			gameOver(false);
		}
	}
	
	public void setPlayerLocation(GeoPoint playerLocation) {
		Log.v("new location", playerLocation.toString());
		this.lastPlayerLocation = this.playerLocation;
		this.playerLocation = playerLocation;
		if(animPlayerLocation==null) animPlayerLocation=playerLocation;
		if(lastPlayerLocation==null) lastPlayerLocation=playerLocation;
		setAnimatePlayerLocation(true);
	}
	
	public void setAnimPlayerLocation(GeoPoint animPlayerLocation) {
		this.animPlayerLocation = animPlayerLocation;
	}
	
	public GeoPoint getAnimPlayerLocation() {	
		return animPlayerLocation;
	} 

	public GeoPoint getPlayerLocation() {
		return playerLocation;
	}
	
	public GeoPoint getLastPlayerLocation() {
		return lastPlayerLocation;
	}
	
	public void setPlayerDirection(float dir) {
		this.lastPlayerDirection = this.playerDirection;
		this.playerDirection = dir;
		setAnimatePlayerDirection(true);
	}

	public float getPlayerDirection() {
		return playerDirection;
	}
	
	public float getLastPlayerDirection() {
		return lastPlayerDirection;
	}
	
	public float getAnimPlayerDirection() {		
		return animPlayerDirection;
	} 
	
	public void setAnimPlayerDirection(float dir) {	
//		Log.v("animDir", dir + "");
		animPlayerDirection = dir;
	} 

	public int getItemRadius() {
		return dotRadius;
	}

	public int getGameRadius() {
		return gameRadius;
	}

	public int getTankSpeed() {
		return tankSpeed;
	}
	
	public void setTankSpeed(int speed) 
	{
		tankSpeed = speed;
	}

	public int getItemDistance() {
		return itemDistance;
	}

	public void gameOver(boolean victory) {
		this.victory = victory;
		this.gameOver = true;
	}

	public void shutdownGame() {
		for(Item item:getItems()) {
			item.removeItemProximityAlert();
		}
		for(Tank tank:getTanks()) {
			tank.removeTankProximityAlert();
		}
		gameReady = false;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public boolean isVictory() {
		return victory;
	}

	public void restartGame() {
		for (Tank tank : GameLogic.getInstance().getTanks()) {
			tank.removeTankProximityAlert();
		}
		for (Item item : GameLogic.getInstance().getItems()) {
			item.removeItemProximityAlert();
		}
		items = new CopyOnWriteArraySet<Item>();
		tanks = new CopyOnWriteArraySet<Tank>();
		points = 0;
		gameReady = false;
		timeLeft = timeLimit;
		victory = false;
		gameOver = false;
		Tank.id = 0;
		Item.id = 0;
	}

	/**
	 * DEBUG STUFF records and pased player position and location
	 */

	public boolean getDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean onoff) {
		parsedMode = !onoff;
		debugMode = onoff;
	}

	public boolean getParsedMode() {
		return parsedMode;
	}

	public void setParsedMode(boolean onoff) {
		debugMode = !onoff;
		parsedMode = onoff;
	}

	public static double CalculationByDistance(GeoPoint startGP, GeoPoint endGP) {
		Location location1 = new Location("geo1");
		Location location2 = new Location("geo2");
		
		location1.setLatitude(startGP.getLatitudeE6()/1E6);
		location1.setLongitude(startGP.getLongitudeE6()/1E6);
		
		location2.setLatitude(endGP.getLatitudeE6()/1E6);
		location2.setLongitude(endGP.getLongitudeE6()/1E6);
		
		return location1.distanceTo(location2);
	}
	
	public int getMaxTargets() {
		return maxTargets;
	}
	
	public void setMaxTargets(int max) 
	{
		maxTargets = max;
	}
	
	public int getTimeLimit() {
		return timeLimit;
	}
	
	public boolean isSound() {
		return sound;
	}
	
	public void setSound(boolean soundOnOff) {
		sound = soundOnOff;
	}
	
	public void switchSound() {
		sound = !sound;
	}
	
	public boolean isAnimation() {
		return animation;
	}
	
	public void setAnimation(boolean animationOnOff) {
		animation = animationOnOff;
	}
	
	public void switchAnimation() {
		animation = !animation;
	}
	
	public void setTimeLimit(int time) {
		timeLimit = time;
	}

	public boolean isSatellite() {
		return satellite;
	}	
	
	public void setSatellite(boolean satelliteOnOff) {
		satellite = satelliteOnOff;
	}
	
	public void switchSatellite() {
		satellite = !satellite;
	}

	public boolean isAnimatePlayerLocation() {
		return animatePlayerLocation;
	}

	public void setAnimatePlayerLocation(boolean animatePlayer) {
		this.animatePlayerLocation = animatePlayer;
	}
	
	public boolean isAnimatePlayerDirection() {
		return animatePlayerDirection;
	}

	public void setAnimatePlayerDirection(boolean animatePlayerDirection) {
		this.animatePlayerDirection = animatePlayerDirection;
	}
	
	public static GeoPoint interpolatePos(GeoPoint p1, GeoPoint p2, double f) {
		int newLat = (int) ((p2.getLatitudeE6() - p1.getLatitudeE6()) * f);
		int newLog = (int) ((p2.getLongitudeE6() - p1.getLongitudeE6()) * f);
		return new GeoPoint(p1.getLatitudeE6() + newLat, p1.getLongitudeE6() + newLog);
	}

	public static float interpolateDir(float oldDir, float newDir, double f) {
		int diffDir = (int) ((newDir - oldDir) * f);
		return (oldDir + diffDir);
	}
}
