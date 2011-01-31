package de.swagner.homeinvasion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import android.os.Environment;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public final class Debug {

	private static Debug instance;
	
	private ArrayList<GeoPoint> recordedPositions;
	private ArrayList<Float> recordedDirections;
	
	private FileOutputStream fOut;
	private Writer writer;
	
	private boolean debugMode;
	private boolean parsedMode;
	
	public Debug()
	{
		recordedPositions = new ArrayList<GeoPoint>();
		recordedDirections = new ArrayList<Float>();
		
		debugMode = false;
		parsedMode = false;
	}
	
	public synchronized static Debug getInstance() {
		if (instance == null) {
			instance = new Debug();
		}
		return instance;
	}
	
	public void parse()
	{
		String state = Environment.getExternalStorageState();

		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/de.swagner.homeinvasion/files/";
		String fileName = "position.txt";

		if (Environment.MEDIA_MOUNTED.equals(state)) {

			ArrayList<GeoPoint> positions = new ArrayList<GeoPoint>();
			ArrayList<Float> directions = new ArrayList<Float>();
			
			try {
				 
				File session = new File(path + fileName);
	
				if (session.exists()) {
					
				    Scanner scanner = new Scanner(new FileReader(session));
				    try {
				      //first use a Scanner to get each line
				      while ( scanner.hasNextLine() ){
				        //use a second Scanner to parse the content of each line 
				        Scanner scannerDelimiter = new Scanner(scanner.nextLine());
				        scannerDelimiter.useDelimiter(" ");
				        if ( scannerDelimiter.hasNext() ){
				          GeoPoint position = new GeoPoint(scannerDelimiter.nextInt(), scannerDelimiter.nextInt());
				          positions.add(position);
				          directions.add(Float.valueOf(scannerDelimiter.next()));
				        }			        
				        
				      }
				    }
				    catch (Exception e) {
						Log.e("filescanner", e.toString());
					}
				    finally {
				      //ensure the underlying stream is always closed
				      scanner.close();
				    }
					
				} else
					Log.e("parse debug", "no position.txt found");
								 
				setRecordedSession(positions, directions);		
				for(GeoPoint gp:positions) {
					Log.v("filescanner",gp.toString());
				}

			} catch (Exception ioe) {
				Log.e("filescanner", ioe.toString());
			}

		}
		else {
			Log.e("filescanner", "could not mount sd card");
		}
	}

	public void record() {
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

	public void setRecordedPositions(ArrayList<GeoPoint> recordedPositions) {
		this.recordedPositions = recordedPositions;
	}


	public ArrayList<GeoPoint> getRecordedPositions() {
		return recordedPositions;
	}


	public void setRecordedDirections(ArrayList<Float> recordedDirections) {
		this.recordedDirections = recordedDirections;
	}


	public ArrayList<Float> getRecordedDirections() {
		return recordedDirections;
	}
	
	public GeoPoint getCurrentRecordedPosition() {
		if (GameLogic.getInstance().getTimeLimit() - GameLogic.getInstance().getTimeLeft() < recordedPositions.size()) {
			return recordedPositions.get(GameLogic.getInstance().getTimeLimit() - GameLogic.getInstance().getTimeLeft());
		}
		return null;
	}

	public Float getCurrentRecordedDirection() {
		if (GameLogic.getInstance().getTimeLimit() - GameLogic.getInstance().getTimeLeft()  < recordedDirections.size()) {
			return recordedDirections.get(GameLogic.getInstance().getTimeLimit() - GameLogic.getInstance().getTimeLeft());
		}
		return null;
	}
	
	public void setRecordedSession(ArrayList<GeoPoint> recordedPositions, ArrayList<Float> recordedDirections) {
		this.recordedPositions = recordedPositions;
		this.recordedDirections = recordedDirections;
	}
	
	public void recordSession() throws IOException {
		if (GameLogic.getInstance().isGameReady()) {
			writer.write("" + GameLogic.getInstance().getPlayer().getLocation().getLatitudeE6());
			writer.write(" ");
			writer.write("" + GameLogic.getInstance().getPlayer().getLocation().getLongitudeE6());
			writer.write(" ");
			writer.write("" + GameLogic.getInstance().getPlayer().getDirection() + "\n");
			writer.flush();
		}
	}
	
	public void stopRecording() {
		try {
			writer.close();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean getDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean onoff) {
		parsedMode = !onoff;
		debugMode = onoff;
		if(debugMode) record();
	}

	public boolean getParsedMode() {
		return parsedMode;
	}

	public void setParsedMode(boolean onoff) {
		debugMode = !onoff;
		parsedMode = onoff;
		if(parsedMode) parse();
	}

	
}
