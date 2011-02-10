package de.swagner.homeinvasion;

import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.maps.GeoPoint;

public final class GameLogic {

	private static GameLogic instance;
	
	final static int EASY_MODE = 25;
	final static int MEDIUM_MODE = 35;
	final static int HARD_MODE = 55;
	
	final static int TANK_SLOW = 12;
	final static int TANK_MEDIUM = 22;
	final static int TANK_FAST = 32;
	
	final static int SHORT_LENGTH = 900;
	final static int MEDIUM_LENGTH = 1200;
	final static int LONG_LENGTH = 1500;
	
	final static String prefs = "Settings";

	private CopyOnWriteArraySet<Item> items;
	private CopyOnWriteArraySet<Tank> tanks;
	private Ufo player;
	private int points;
	private boolean gameReady;
	private int timeLeft;
	private int timeLimit;
	private int tankSpeed;

	private int gameRadius;
	private int itemRadius;
	private int tankRadius;
	private int itemPlacementDistance;
	private int maxTargets;

	private boolean gameOver;
	private boolean victory;

	private int wave;
	
	//need this for proximity accuracy check
	private Location currentLocation;
	
	//game settings vars
	private boolean sound;
	private boolean animation;
	private boolean satellite;

	private GameLogic() {
		items = new CopyOnWriteArraySet<Item>();
		tanks = new CopyOnWriteArraySet<Tank>();
		player = new Ufo();
		points = 0;
		gameReady = false;
		timeLimit = 1200;
		timeLeft = timeLimit;
		gameRadius = 100;
		itemRadius = 55;
		tankRadius = 65;
		itemPlacementDistance = 400;
		tankSpeed = 15;
		victory = false;
		gameOver = false;
		Tank.id = 0;
		Item.id = 0;
		wave=0;
		
		maxTargets = 50;

		sound = false;
		animation =true;
		satellite=true;
	
	}

	public synchronized static GameLogic getInstance() {
		if (instance == null) {
			instance = new GameLogic();
		}
		return instance;
	}
	
	public Ufo getPlayer() {
		return player;
	}

	public CopyOnWriteArraySet<Item> getItems() {
		return items;
	}

	public HashSet<GeoPoint> getItemGeoPoints() {
		HashSet<GeoPoint> geoPoints = new HashSet<GeoPoint>();
		for (Item item : items) {
			geoPoints.add(item.getPosition());
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
			if(CalculationByDistance(geoPoint, dotGeoPoint)<itemRadius) return false;
		}

		Item newItem = new Item(context, locationManager, geoPoint);
		return items.add(newItem);
	}

	public boolean addTank(Context context, LocationManager locationManager, GeoPoint geoPoint) {
		incWave();
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

	public int getItemRadius() {
		return itemRadius;
	}
	
	public int getTankRadius() {
		return tankRadius;
	}

	public int getGameRadius() {
		return gameRadius;
	}

	public int getTankSpeed() {
		return tankSpeed;
	}
	
	public void setTankSpeed(int speed) {
		tankSpeed = speed;
	}

	public int getItemDistance() {
		return itemPlacementDistance;
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
		wave = 0;
		player.setAlive(true);
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
	
	public void setMaxTargets(int max) {
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
	
	public void incWave() {
		++wave;
	}
	
	public int getWave() {
		return wave;
	}
	
	public static GeoPoint interpolatePos(GeoPoint p1, GeoPoint p2, double f) {
		int newLat = (int) ((p2.getLatitudeE6() - p1.getLatitudeE6()) * f);
		int newLog = (int) ((p2.getLongitudeE6() - p1.getLongitudeE6()) * f);
		return new GeoPoint(p1.getLatitudeE6() + newLat, p1.getLongitudeE6() + newLog);
	}

	public static float interpolateDir(float start, float end, float f) {
		 float difference = Math.abs(end - start);
	        if (difference > 180)
	        {
	            // We need to add on to one of the values.
	            if (end > start)
	            {
	                // We'll add it on to start...
	                start += 360;
	            }
	            else
	            {
	                // Add it on to end.
	                end += 360;
	            }
	        }

	        // Interpolate it.
	        float value = (start + ((end - start) * f));

	        // Wrap it..
	        float rangeZero = 360;

	        if (value >= 0 && value <= 360)
	            return value;

	        return (value % rangeZero);
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;		
	}
	
	public Location getCurrentLocation() {
		return currentLocation;		
	}
	

}
