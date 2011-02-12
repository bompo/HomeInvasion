package de.swagner.homeinvasion;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class GameService extends Service {
	public static final int NOTIF_ID = 1337;
	public static final String UPDATE_UI_ACTION = "de.swagner.homeinvasion.updateui";
	public static final String ADD_TANK_ACTION = "de.swagner.homeinvasion.addtank";
	private Intent updateUIBroadcast = new Intent(UPDATE_UI_ACTION);
	private Intent addTankBroadcast = new Intent(ADD_TANK_ACTION);
	private final Binder binder = new LocalBinder();
	private Timer timer = new Timer();

	@Override
	public void onCreate() {
		super.onCreate();
		startService();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return (binder);
	}

	@Override
	public void onDestroy() {
		timer.cancel();
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
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 400) {
					sendBroadcast(addTankBroadcast);
				}
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 500) {
					sendBroadcast(addTankBroadcast);
				}
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 600) {
					sendBroadcast(addTankBroadcast);
				}
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 700) {
					sendBroadcast(addTankBroadcast);
				}
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 800) {
					sendBroadcast(addTankBroadcast);
				}
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 900) {
					sendBroadcast(addTankBroadcast);
				}
				
				tankAI();
				
				//DEBUG STUFF
				try {
					if (Debug.getInstance().getDebugMode())
						Debug.getInstance().recordSession();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (Debug.getInstance().getParsedMode() && GameLogic.getInstance().isGameReady()) {
					GameLogic.getInstance().getPlayer().setDirection(Debug.getInstance().getCurrentRecordedDirection());
				}
				
			}
		}, 0, 1000);
	}
	
	protected void tankAI() {
		for (final Tank tank : GameLogic.getInstance().getTanks()) {
			tank.interpolRoute();
		}
	}

	public class LocalBinder extends Binder {
		GameService getService() {
			return (GameService.this);
		}
	}
}