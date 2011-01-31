package de.swagner.homeinvasion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;


public class ItemOverlay extends Overlay {

	private Point point;
	private Projection projection;
	
	public ItemOverlay(Context context) {
		super();
		point = new Point();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		projection = mapView.getProjection();
		if (shadow == false) {
			if(GameLogic.getInstance().isGameReady()) {
				for (Item item : GameLogic.getInstance().getItems()) {
					if(item.isShootAnim()) {
						projection.toPixels(item.getShootAnimPosition(), point);
						canvas.drawBitmap(Images.getInstance().bmp_target_f1, point.x-item.getBitmap().getWidth()/2, point.y-item.getBitmap().getHeight()/2, null);
					}					
					
					projection.toPixels(item.getPosition(), point);
					canvas.drawBitmap(item.getBitmap(), point.x-item.getBitmap().getWidth()/2, point.y-item.getBitmap().getHeight()/2, null);
				}
			}
		}
	}
	
	/**
	 * calls animation updates of all items 
	 */
	public void update() {
		for (Item item : GameLogic.getInstance().getItems()) {
			item.update();
		}
	}

}