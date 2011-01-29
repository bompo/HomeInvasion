package de.swagner.homeinvasion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.google.android.maps.GeoPoint;

public class GameService extends Service {
	public static final int NOTIF_ID = 1337;
	public static final String UPDATE_UI_ACTION = "de.swagner.homeinvasion.updateui";
	public static final String ADD_TANK_ACTION = "de.swagner.homeinvasion.addtank";
	private Intent updateUIBroadcast = new Intent(UPDATE_UI_ACTION);
	private Intent addTankBroadcast = new Intent(ADD_TANK_ACTION);
	private final Binder binder = new LocalBinder();
	private Timer timer = new Timer();
	private FileOutputStream fOut;
	private Writer writer;
		
	private Handler mHandler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();

		if (GameLogic.getInstance().getDebugMode()) {
			String state = Environment.getExternalStorageState();

			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/de.swagner.homeinvasion/files/";
			String fileName = "position.txt";

			if (Environment.MEDIA_MOUNTED.equals(state)) {

				try {
					// Make sure the path exists
					boolean exists = (new File(path)).exists();
					if (!exists) {
						new File(path).mkdirs();
					}

					// Open output stream
					fOut = new FileOutputStream(path + fileName);
					writer = new OutputStreamWriter(fOut, "UTF-8");

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

			}
		}

		startService();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return (binder);
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		if (GameLogic.getInstance().getDebugMode()) {
			try {
				writer.close();
				fOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 11) {
					sendBroadcast(addTankBroadcast);
				}
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 12) {
					sendBroadcast(addTankBroadcast);
				}
				if (GameLogic.getInstance().getTimeLeft() == GameLogic.getInstance().getTimeLimit() - 13) {
					sendBroadcast(addTankBroadcast);
				}
				try {
					if (GameLogic.getInstance().getDebugMode())
						recordSession();
					tankAI();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (GameLogic.getInstance().getParsedMode() && GameLogic.getInstance().isGameReady()) {
					GameLogic.getInstance().setPlayerDirection(Debug.getInstance().getCurrentRecordedDirection());
				}
			}
		}, 0, 1000);
	}
	
	protected void tankAI() throws IOException {
		for (final Tank tank : GameLogic.getInstance().getTanks()) {
			tank.interpolRoute();
		}
	}

	public class LocalBinder extends Binder {
		GameService getService() {
			return (GameService.this);
		}
	}

	public void recordSession() throws IOException {
		if (GameLogic.getInstance().isGameReady()) {
			writer.write("" + GameLogic.getInstance().getPlayerLocation().getLatitudeE6());
			writer.write(" ");
			writer.write("" + GameLogic.getInstance().getPlayerLocation().getLongitudeE6());
			writer.write(" ");
			writer.write("" + GameLogic.getInstance().getPlayerDirection() + "\n");
			writer.flush();
		}
	}
}