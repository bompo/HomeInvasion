package de.swagner.homeinvasion;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class SettingsActivity extends Activity {

	private Editor editor;
	
	private CheckBox animationchk;
	private CheckBox satellitechk;
	private Spinner difficultySpinner;
	private Spinner timeSpinner;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		editor = getSharedPreferences(GameLogic.prefs, MODE_PRIVATE).edit();
		
		animationchk = (CheckBox) findViewById(R.id.settingsAnimation_check);
		animationchk.setChecked(getSharedPreferences(GameLogic.prefs, MODE_PRIVATE).getBoolean("Animation", GameLogic.getInstance().isAnimation()));
		
		satellitechk = (CheckBox) findViewById(R.id.settingsSatellite_check);
		satellitechk.setChecked(getSharedPreferences(GameLogic.prefs, MODE_PRIVATE).getBoolean("Satellite", GameLogic.getInstance().isSatellite()));


		difficultySpinner = (Spinner) findViewById(R.id.settingsSpinner);
		ArrayAdapter<String> adapt = (ArrayAdapter<String>) difficultySpinner.getAdapter();
		difficultySpinner.setSelection(adapt.getPosition(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getString("GameMode", "fail")));

		difficultySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String choice = (String) arg0.getItemAtPosition(arg2);
				setDifficulty(choice);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		timeSpinner = (Spinner) findViewById(R.id.settingsTimeSpinner);
		ArrayAdapter<String> adapt2 = (ArrayAdapter<String>) timeSpinner.getAdapter();
		timeSpinner.setSelection(adapt2.getPosition(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getString("GameTime", "fail")));

		timeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String choice = (String) arg0.getItemAtPosition(arg2);
				setGameLength(choice);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	public void onDebugClick(View v) {
		Debug.getInstance().setDebugMode(true);
	}

	public void onParseClick(View v) {
		Debug.getInstance().setParsedMode(true);
	}

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
	
	public void onAnimationClick(View v) {
		GameLogic.getInstance().switchAnimation();
		editor.putBoolean("Animation", GameLogic.getInstance().isAnimation());
		editor.commit();
	}
	
	public void onSatelliteClick(View v) {
		GameLogic.getInstance().switchSatellite();
		editor.putBoolean("Satellite", GameLogic.getInstance().isSatellite());
		editor.commit();
	}

}
