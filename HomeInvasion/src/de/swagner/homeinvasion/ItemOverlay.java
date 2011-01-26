package de.swagner.homeinvasion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;


public class ItemOverlay extends Overlay {

	public ItemOverlay(Context context) {
		super();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Projection projection = mapView.getProjection();
		if (shadow == false) {
			if(GameLogic.getInstance().isGameReady()) {
				for (Item item : GameLogic.getInstance().getItems()) {
					Point myPoint = new Point();
					projection.toPixels(item.getGeoPoint(), myPoint);
					canvas.drawBitmap(item.getBitmap(), myPoint.x-item.getBitmap().getWidth()/2, myPoint.y-item.getBitmap().getHeight()/2, null);
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