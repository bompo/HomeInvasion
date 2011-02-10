package de.swagner.homeinvasion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

public class TankProximityIntentReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// String key = LocationManager.KEY_PROXIMITY_ENTERING;
		// Boolean entering = intent.getBooleanExtra(key, false);
		int ghostID = intent.getIntExtra("tankID", -1);

		if (GameLogic.getInstance().isGameReady()) {
			
			if(!GameLogic.getInstance().getCurrentLocation().hasAccuracy() || GameLogic.getInstance().getCurrentLocation().getAccuracy()>20) return;
			
			//don't really need this here...
			for (Tank tank : GameLogic.getInstance().getTanks()) {
				if (tank.getID() == ghostID) {
					tank.removeTankProximityAlert();
					tank.setShootAnim(true);
					if(!GameLogic.getInstance().isAnimation()) {
						GameLogic.getInstance().getPlayer().setAlive(false);
						GameLogic.getInstance().gameOver(false);
					}
					if(GameLogic.getInstance().isSound()) {
						MediaPlayer mp = MediaPlayer.create(context, R.raw.gameover);
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
				}
			}
			

		}
	}
}