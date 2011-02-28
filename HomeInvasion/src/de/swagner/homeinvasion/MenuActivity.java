package de.swagner.homeinvasion;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.admob.android.ads.AdManager;

public class MenuActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.menu);
		GameLogic.getInstance().setSound(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getBoolean("SoundOnOff", false));
		refreshSoundButton();	
		setDifficulty(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getString("GameMode", "Medium"));
		setGameLength(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getString("GameTime", "20 Minutes"));
		GameLogic.getInstance().setAnimation(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getBoolean("Animation", true));
		GameLogic.getInstance().setSatellite(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getBoolean("Satellite", true));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		GameLogic.getInstance().shutdownGame();
	}

	/** Handle "gameStart" action. */
	public void onGameStartClick(View v) {
		GameLogic.getInstance().restartGame();
		Intent PacMapIntent = new Intent(getBaseContext(), de.swagner.homeinvasion.GameActivity.class);
		startActivity(PacMapIntent);
	}

	/** Handle "instructions" action. */
	public void onInstructionsClick(View v) {
		Intent InstructionsIntent = new Intent(getBaseContext(), de.swagner.homeinvasion.InstructionsActivity.class);
		startActivity(InstructionsIntent);
	}
	
	/** Handle "settings" action. */
	public void onSettingsClick(View v)	{
		Intent SettingsIntent = new Intent(getBaseContext(), de.swagner.homeinvasion.SettingsActivity.class);
		startActivity(SettingsIntent); 
	}
	
	/** Handle "sound" action. */
	public void onSoundClick(View v) {
		Editor editor = getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).edit();
		GameLogic.getInstance().switchSound();
		refreshSoundButton();	
		editor.putBoolean("SoundOnOff", GameLogic.getInstance().isSound());
		editor.commit();
	}

	private void refreshSoundButton() {
		Button btn_sound = (Button) findViewById(R.id.menu_btn_sound);
		if(GameLogic.getInstance().isSound()) {
			btn_sound.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.menu_btn_sound_on), null, null);
			btn_sound.setText(getResources().getString(R.string.btn_sound_on));
		} else {
			btn_sound.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.menu_btn_sound_off), null, null);
			btn_sound.setText(getResources().getString(R.string.btn_sound));
		}
	}
	
	//TODO SAME CODE AS IN SETTINGSACTIVITY -> REFACTOR
	public void setDifficulty(String choice) {
		final Editor editor = getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).edit();

		if (choice.equals("Easy")) {
			GameLogic.getInstance().setTankSpeed(GameLogic.TANK_SLOW);
		} else if (choice.equals("Medium")) {
			GameLogic.getInstance().setTankSpeed(GameLogic.TANK_MEDIUM);
		} else if (choice.equals("Hard")) {
			GameLogic.getInstance().setTankSpeed(GameLogic.TANK_FAST);
		}

		editor.putString("GameMode", choice);
		editor.commit();

	}
	
	public void setGameLength(String choice) {
		final Editor editor = getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).edit();

		if (choice.equals("15 Minutes")) {
			GameLogic.getInstance().setTimeLimit(GameLogic.SHORT_LENGTH);
			GameLogic.getInstance().setMaxTargets(GameLogic.EASY_MODE);
		} else if (choice.equals("20 Minutes")) {
			GameLogic.getInstance().setTimeLimit(GameLogic.MEDIUM_LENGTH);
			GameLogic.getInstance().setMaxTargets(GameLogic.MEDIUM_MODE);

		} else if (choice.equals("30 Minutes")) {
			GameLogic.getInstance().setTimeLimit(GameLogic.LONG_LENGTH);
			GameLogic.getInstance().setMaxTargets(GameLogic.HARD_MODE);
		}

		editor.putString("GameTime", choice);
		editor.commit();

	}
}