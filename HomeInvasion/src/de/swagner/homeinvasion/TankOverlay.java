package de.swagner.homeinvasion;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TankOverlay extends Overlay {

	private Bitmap resizedBitmap;

	public TankOverlay() {
		super();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Projection projection = mapView.getProjection();
		if (shadow == false) {
			for (Tank tank : GameLogic.getInstance().getTanks()) {
				Point myPoint = new Point();
				projection.toPixels(tank.getAnimPosition(), myPoint);

				Matrix matrix = new Matrix();
				matrix.postRotate(tank.getAnimDirection());
				resizedBitmap = Bitmap.createBitmap(tank.getBitmap(), 0, 0, tank.getBitmap().getWidth(), tank.getBitmap().getHeight(), matrix, true);
				canvas.drawBitmap(resizedBitmap, myPoint.x - tank.getBitmap().getWidth() / 2, myPoint.y - tank.getBitmap().getHeight() / 2, null);
			}
		}
	}

	/**
	 * calls animation updates of all tank
	 */
	public void update() {
		for (Tank tank : GameLogic.getInstance().getTanks())
			tank.update();
	}
}