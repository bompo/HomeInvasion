package de.swagner.homeinvasion;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TankOverlay extends Overlay {

	private Matrix matrix;
	private Point point;
	private Projection projection;

	public TankOverlay() {
		super();
		point = new Point();
		matrix = new Matrix();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		projection = mapView.getProjection();
		if (shadow == false) {
			for (Tank tank : GameLogic.getInstance().getTanks()) {
				projection.toPixels(tank.getAnimPosition(), point);

				//draw radius
				canvas.drawBitmap(Images.getInstance().bmp_tank_radius_big, point.x - (Images.getInstance().bmp_tank_radius_big.getWidth() / 2), point.y - (Images.getInstance().bmp_tank_radius_big.getHeight() / 2), null);
				
				matrix.setRotate(tank.getAnimDirection(),(tank.getBitmap().getWidth()/2),(tank.getBitmap().getHeight()/2));
				matrix.postTranslate(point.x - (tank.getBitmap().getWidth()/2), point.y - (tank.getBitmap().getHeight()/2));
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