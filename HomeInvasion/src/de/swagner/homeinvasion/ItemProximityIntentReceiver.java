package de.swagner.homeinvasion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

public class ItemProximityIntentReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// String key = LocationManager.KEY_PROXIMITY_ENTERING;
		// Boolean entering = intent.getBooleanExtra(key, false);
		int dotID = intent.getIntExtra("dotID", -1);

		if (GameLogic.getInstance().isGameReady()) {
			
			if(!GameLogic.getInstance().getCurrentLocation().hasAccuracy() || GameLogic.getInstance().getCurrentLocation().getAccuracy()>20) return;
			
			GameLogic.getInstance().incPoints();
			
			if(GameLogic.getInstance().isSound()) {
				try {
				MediaPlayer mp = MediaPlayer.create(context, R.raw.explosion);
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
				} catch (Exception e) {
					Log.e("play chomadot", "failed");
				}
			}
						
			for (Item dot : GameLogic.getInstance().getItems()) {
				if (dot.getID() == dotID) {
					dot.removeItemProximityAlert();
				}
			}
			
			//victory?
			if(GameLogic.getInstance().getItems().size()==0) {
				GameLogic.getInstance().gameOver(true);
			}
		}
	}
}