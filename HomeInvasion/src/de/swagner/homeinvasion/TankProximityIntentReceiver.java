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
		int ghostID = intent.getIntExtra("ghostID", -1);

		if (GameLogic.getInstance().isGameReady()) {
			
			//don't really need this here...
			for (Tank tank : GameLogic.getInstance().getTanks()) {
				if (tank.getID() == ghostID) {
					tank.removeTankProximityAlert();
					GameLogic.getInstance().gameOver(false);
					GameLogic.getInstance().getTanks().remove(tank);	
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