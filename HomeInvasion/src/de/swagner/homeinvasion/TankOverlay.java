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
				if(mapView.getZoomLevel()<=17) {
					matrix.setScale(0.01f, 0.01f,Images.getInstance().bmp_tank_radius_big.getWidth() / 2, Images.getInstance().bmp_tank_radius_big.getHeight() / 2);
				} else if(mapView.getZoomLevel()==18) {
					matrix.setScale(0.5f, 0.5f,Images.getInstance().bmp_tank_radius_big.getWidth() / 2, Images.getInstance().bmp_tank_radius_big.getHeight() / 2);
				}else if(mapView.getZoomLevel()==19) {
					matrix.setScale(1f, 1f,Images.getInstance().bmp_tank_radius_big.getWidth() / 2, Images.getInstance().bmp_tank_radius_big.getHeight() / 2);
				}else if(mapView.getZoomLevel()==20) {
					matrix.setScale(2f, 2f,Images.getInstance().bmp_tank_radius_big.getWidth() / 2, Images.getInstance().bmp_tank_radius_big.getHeight() / 2);
				}else if(mapView.getZoomLevel()==21) {
					matrix.setScale(4f, 4f,Images.getInstance().bmp_tank_radius_big.getWidth() / 2, Images.getInstance().bmp_tank_radius_big.getHeight() / 2);
				}else if(mapView.getZoomLevel()>=22) {
					matrix.setScale(8f, 8f,Images.getInstance().bmp_tank_radius_big.getWidth() / 2, Images.getInstance().bmp_tank_radius_big.getHeight() / 2);
				}
				matrix.postTranslate(point.x - (Images.getInstance().bmp_tank_radius_big.getWidth() / 2), point.y - (Images.getInstance().bmp_tank_radius_big.getHeight() / 2));
				canvas.drawBitmap(Images.getInstance().bmp_tank_radius_big, matrix, null);
				
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