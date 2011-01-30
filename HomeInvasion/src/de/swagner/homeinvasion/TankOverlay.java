package de.swagner.homeinvasion;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TankOverlay extends Overlay {

	private Matrix matrix;
	private Point myPoint;
	private Projection projection;

	public TankOverlay() {
		super();
		myPoint = new Point();
		matrix = new Matrix();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		projection = mapView.getProjection();
		if (shadow == false) {
			for (Tank tank : GameLogic.getInstance().getTanks()) {
				projection.toPixels(tank.getAnimPosition(), myPoint);

				matrix.setRotate(tank.getAnimDirection(),(tank.getBitmap().getWidth()/2),(tank.getBitmap().getHeight()/2));
				matrix.postTranslate(myPoint.x - (tank.getBitmap().getWidth()/2), myPoint.y - (tank.getBitmap().getHeight()/2));
				canvas.drawBitmap(tank.getBitmap(),matrix , null);
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