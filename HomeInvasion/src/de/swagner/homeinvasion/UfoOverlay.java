package de.swagner.homeinvasion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class UfoOverlay extends Overlay {

	private Context m_context;
	private Bitmap bmp_ufo_f1;
	private Bitmap bmp_ufo_f2;
	private Bitmap bmp_ufo_f3;
	private Bitmap bmp_ufo_f4;
	private Bitmap bmp_ufo_f5;
	private Bitmap bmp_ufo_f6;
	private Bitmap bmp_ufo_current;
	
	private Bitmap bmp_ufo_shadow_f1;
	private Bitmap bmp_ufo_shadow_f2;
	private Bitmap bmp_ufo_shadow_f3;
	private Bitmap bmp_ufo_shadow_f4;
	private Bitmap bmp_ufo_shadow_f5;
	private Bitmap bmp_ufo_shadow_f6;
	private Bitmap bmp_ufo_shadow_current;
	
	private Bitmap resizedBitmap;
	
	private int currentFrame;
	private double animPlayerLocationCounter;
	private double animPlayerDirectionCounter;
	
	private Matrix matrix;
	private Point point;
	private Projection projection;

	public UfoOverlay(Context context) {
		m_context = context;
		bmp_ufo_f1 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_f1);
		bmp_ufo_f2 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_f2);
		bmp_ufo_f3 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_f3);
		bmp_ufo_f4 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_f4);
		bmp_ufo_f5 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_f5);
		bmp_ufo_f6 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_f6);		
		bmp_ufo_current = bmp_ufo_f1;

		bmp_ufo_shadow_f1 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_shadow_f1);
		bmp_ufo_shadow_f2 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_shadow_f2);
		bmp_ufo_shadow_f3 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_shadow_f3);
		bmp_ufo_shadow_f4 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_shadow_f4);
		bmp_ufo_shadow_f5 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_shadow_f5);
		bmp_ufo_shadow_f6 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.ufo_shadow_f6);
		bmp_ufo_shadow_current = bmp_ufo_shadow_f1;

		currentFrame = 0;
		animPlayerLocationCounter = 1;
		animPlayerDirectionCounter = 1;

		point = new Point();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		projection = mapView.getProjection();
		if (shadow == false) {
			if (GameLogic.getInstance().getAnimPlayerLocation() != null) {
				// Convert the location to screen pixels
				projection.toPixels(GameLogic.getInstance().getAnimPlayerLocation(), point);

				matrix = new Matrix();
				
				//draw shadow
				matrix.postRotate(GameLogic.getInstance().getAnimPlayerDirection(),bmp_ufo_shadow_current.getWidth() / 2,bmp_ufo_shadow_current.getHeight() / 2);
				resizedBitmap = Bitmap.createBitmap(bmp_ufo_shadow_current, 0, 0, bmp_ufo_shadow_current.getWidth(), bmp_ufo_shadow_current.getHeight(), matrix, true);
				canvas.drawBitmap(resizedBitmap, point.x +40 - bmp_ufo_shadow_current.getWidth() / 2, point.y +40 - bmp_ufo_shadow_current.getHeight() / 2, null);
				
				//draw ufo
				matrix.postRotate(GameLogic.getInstance().getAnimPlayerDirection(),bmp_ufo_current.getWidth() / 2,bmp_ufo_current.getHeight() / 2);
				resizedBitmap = Bitmap.createBitmap(bmp_ufo_current, 0, 0, bmp_ufo_current.getWidth(), bmp_ufo_current.getHeight(), matrix, true);
				canvas.drawBitmap(resizedBitmap, point.x - bmp_ufo_current.getWidth() / 2, point.y - bmp_ufo_current.getHeight() / 2, null);
			}
		}
	}

	public void update() {
		if (currentFrame == 1) {
			bmp_ufo_current = bmp_ufo_f1;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f1;
		} else if (currentFrame == 3) {
			bmp_ufo_current = bmp_ufo_f2;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f2;
		} else if (currentFrame == 5) {
			bmp_ufo_current = bmp_ufo_f3;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f3;
		} else if (currentFrame == 7) {
			bmp_ufo_current = bmp_ufo_f4;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f4;
		} else if (currentFrame == 9) {
			bmp_ufo_current = bmp_ufo_f5;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f5;
		} else if (currentFrame == 11) {
			bmp_ufo_current = bmp_ufo_f6;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f6;
		} else if (currentFrame == 13) {
			bmp_ufo_current = bmp_ufo_f6;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f6;
		} else if (currentFrame == 15) {
			bmp_ufo_current = bmp_ufo_f5;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f5;
		} else if (currentFrame == 17) {
			bmp_ufo_current = bmp_ufo_f4;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f4;
		} else if (currentFrame == 19) {
			bmp_ufo_current = bmp_ufo_f3;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f3;
		} else if (currentFrame == 21) {
			bmp_ufo_current = bmp_ufo_f2;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f2;
		} else if (currentFrame == 23) {
			bmp_ufo_current = bmp_ufo_f1;
			bmp_ufo_shadow_current = bmp_ufo_shadow_f1;
			currentFrame = 0;
		}
		++currentFrame;

		if (GameLogic.getInstance().isAnimatePlayerLocation()) {
			GameLogic.getInstance().setAnimPlayerLocation(GameLogic.interpolatePos(GameLogic.getInstance().getLastPlayerLocation(), GameLogic.getInstance().getPlayerLocation(), animPlayerLocationCounter / 30.));
			animPlayerLocationCounter = animPlayerLocationCounter + 1;
			if (animPlayerLocationCounter == 30) {
				animPlayerLocationCounter = 1;
				GameLogic.getInstance().setAnimatePlayerLocation(false);
			}
		}

		if (GameLogic.getInstance().isAnimatePlayerDirection()) {
			GameLogic.getInstance().setAnimPlayerDirection(GameLogic.interpolateDir(GameLogic.getInstance().getLastPlayerDirection(), GameLogic.getInstance().getPlayerDirection(), animPlayerDirectionCounter / 30.));
			animPlayerDirectionCounter = animPlayerDirectionCounter + 1;
			if (animPlayerDirectionCounter == 30) {
				animPlayerDirectionCounter = 1;
				GameLogic.getInstance().setAnimatePlayerDirection(false);
			}
		}

	}

}
