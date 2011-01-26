package de.swagner.homeinvasion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ScoreActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score);
		TextView tv_score = (TextView) findViewById(R.id.tv_score_points);
		TextView tv_score_text = (TextView) findViewById(R.id.tv_score_text);
		if(GameLogic.getInstance().isVictory()) {
			tv_score_text.setText("You Win!");
		} else {
			tv_score_text.setText("Game Over");
		}
		tv_score.setText("Your Score: " + GameLogic.getInstance().getPoints());
		
		recalcHighscore(GameLogic.getInstance().getPoints(), GameLogic.getInstance().getTimeLeft());
	}

	/** Handle "newGame" action. */
	public void onNewGameClick(View v) {
		GameLogic.getInstance().restartGame();
		Intent PacMapIntent = new Intent(getBaseContext(), de.swagner.homeinvasion.GameActivity.class);
		startActivity(PacMapIntent);
	}

	/** Handle "menu" action. */
	public void onMenuClick(View v) {
		GameLogic.getInstance().restartGame();
		Intent MenuIntent = new Intent(getBaseContext(), de.swagner.homeinvasion.MenuActivity.class);
		startActivity(MenuIntent);
	}

	public void recalcHighscore(int points,int timeleft) {
		//get score
		ArrayList<Integer> highscore = new ArrayList<Integer>();
		highscore.add(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getInt("Score1", 0));
		highscore.add(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getInt("Score2", 0));
		highscore.add(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getInt("Score3", 0));
		highscore.add(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getInt("Score4", 0));
		highscore.add(getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).getInt("Score5", 0));
		highscore.add(points);
		Collections.sort(highscore);
		Collections.reverse(highscore);
		
		//set new score
		final Editor editor = getSharedPreferences(GameLogic.prefs,MODE_PRIVATE).edit();
		Iterator<Integer> iterHighscore = highscore.iterator();
		editor.putInt("Score1", iterHighscore.next());
		editor.putInt("Score2", iterHighscore.next());
		editor.putInt("Score3", iterHighscore.next());
		editor.putInt("Score4", iterHighscore.next());
		editor.putInt("Score5", iterHighscore.next());	
		editor.commit();
		
		//display score
		iterHighscore = highscore.iterator();
		TextView tv_score1 = (TextView) findViewById(R.id.tv_highscore_1_text);
		TextView tv_score2 = (TextView) findViewById(R.id.tv_highscore_2_text);
		TextView tv_score3 = (TextView) findViewById(R.id.tv_highscore_3_text);
		TextView tv_score4 = (TextView) findViewById(R.id.tv_highscore_4_text);
		TextView tv_score5 = (TextView) findViewById(R.id.tv_highscore_5_text);
		tv_score1.setText(iterHighscore.next().toString());
		tv_score2.setText(iterHighscore.next().toString());
		tv_score3.setText(iterHighscore.next().toString());
		tv_score4.setText(iterHighscore.next().toString());
		tv_score5.setText(iterHighscore.next().toString());
	}
	
}
