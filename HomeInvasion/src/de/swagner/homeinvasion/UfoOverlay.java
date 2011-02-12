package de.swagner.homeinvasion;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class UfoOverlay extends Overlay {

	private Bitmap bmp_ufo_current;
	private Bitmap bmp_ufo_shadow_current;
	private Bitmap bmp_ufo_radius_current;
	
	private int currentFrame;
	private double animPlayerLocationCounter;
	private double animPlayerDirectionCounter;
	
	private Matrix matrix;
	private Point point;
	private Projection projection;

	public UfoOverlay() {
		bmp_ufo_current = Images.getInstance().bmp_ufo_f1;
		bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f1;
		bmp_ufo_radius_current = Images.getInstance().bmp_ufo_radius_small;

		currentFrame = 0;
		animPlayerLocationCounter = 1;
		animPlayerDirectionCounter = 1;

		point = new Point();
		matrix = new Matrix();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		projection = mapView.getProjection();
		if (shadow == false) {
			if (GameLogic.getInstance().getPlayer().getAnimPosition() != null) {
				// Convert the location to screen pixels
				projection.toPixels(GameLogic.getInstance().getPlayer().getAnimPosition(), point);
				
				// draw radius
				if(mapView.getZoomLevel()<=17) {
					matrix.setScale(0.01f, 0.01f,bmp_ufo_radius_current.getWidth() / 2, bmp_ufo_radius_current.getHeight() / 2);
				} else if(mapView.getZoomLevel()==18) {
					matrix.setScale(0.5f, 0.5f,bmp_ufo_radius_current.getWidth() / 2, bmp_ufo_radius_current.getHeight() / 2);
				}else if(mapView.getZoomLevel()==19) {
					matrix.setScale(1f, 1f,bmp_ufo_radius_current.getWidth() / 2, bmp_ufo_radius_current.getHeight() / 2);
				}else if(mapView.getZoomLevel()==20) {
					matrix.setScale(2f, 2f,bmp_ufo_radius_current.getWidth() / 2, bmp_ufo_radius_current.getHeight() / 2);
				}else if(mapView.getZoomLevel()==21) {
					matrix.setScale(4f, 4f,bmp_ufo_radius_current.getWidth() / 2, bmp_ufo_radius_current.getHeight() / 2);
				}else if(mapView.getZoomLevel()>=22) {
					matrix.setScale(8f, 8f,bmp_ufo_radius_current.getWidth() / 2, bmp_ufo_radius_current.getHeight() / 2);
				}
				matrix.postTranslate(point.x - (bmp_ufo_radius_current.getWidth() / 2), point.y - (bmp_ufo_radius_current.getHeight() / 2));
				canvas.drawBitmap(bmp_ufo_radius_current, matrix, null);	
				
				// draw shadow
				//matrix.setRotate(GameLogic.getInstance().getPlayer().getAnimDirection(), bmp_ufo_shadow_current.getWidth() / 2, bmp_ufo_shadow_current.getHeight() / 2);
				matrix.setTranslate(point.x +30 - (bmp_ufo_shadow_current.getWidth() / 2), point.y +30- (bmp_ufo_shadow_current.getHeight() / 2));
				canvas.drawBitmap(bmp_ufo_shadow_current, matrix, null);

				// draw ufo
				matrix.setRotate(GameLogic.getInstance().getPlayer().getAnimDirection(), bmp_ufo_current.getWidth() / 2, bmp_ufo_current.getHeight() / 2);
				matrix.postTranslate(point.x - (bmp_ufo_current.getWidth() / 2), point.y - (bmp_ufo_current.getHeight() / 2));
				canvas.drawBitmap(bmp_ufo_current, matrix, null);
			}
		}
	}

	public void update() {
		if (!GameLogic.getInstance().getPlayer().isAlive()) {
			if (currentFrame == 21) {
				bmp_ufo_current = Images.getInstance().bmp_item_f1;
			} else if (currentFrame == 23) {
				bmp_ufo_current = Images.getInstance().bmp_item_f2;
			} else if (currentFrame == 25) {
				bmp_ufo_current = Images.getInstance().bmp_item_f3;
			} else if (currentFrame == 27) {
				bmp_ufo_current = Images.getInstance().bmp_item_f4;
			} else if (currentFrame == 29) {
				bmp_ufo_current = Images.getInstance().bmp_item_f5;
			} else if (currentFrame == 31) {
				bmp_ufo_current = Images.getInstance().bmp_item_f6;
			} else if (currentFrame == 33) {
				bmp_ufo_current = Images.getInstance().bmp_item_f7;
			} else if (currentFrame == 35) {
				bmp_ufo_current = Images.getInstance().bmp_item_f8;
			} else if (currentFrame == 37) {
				bmp_ufo_current = Images.getInstance().bmp_item_f9;
			} else if (currentFrame == 39) {
				bmp_ufo_current = Images.getInstance().bmp_item_f10;
			} else if (currentFrame == 41) {
				bmp_ufo_current = Images.getInstance().bmp_item_f11;
			} else if (currentFrame == 43) {
				bmp_ufo_current = Images.getInstance().bmp_item_f12;
			} else if (currentFrame == 45) {
				bmp_ufo_current = Images.getInstance().bmp_item_f13;
			} else if (currentFrame == 47) {
				bmp_ufo_current = Images.getInstance().getExplosionResult();
			} else if (currentFrame == 200) {
				GameLogic.getInstance().gameOver(false);
			}
			++currentFrame;

		} else {
			if (currentFrame == 1) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f1;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f1;
			} else if (currentFrame == 3) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f2;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f2;
			} else if (currentFrame == 5) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f3;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f3;
			} else if (currentFrame == 7) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f4;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f4;
			} else if (currentFrame == 9) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f5;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f5;
			} else if (currentFrame == 11) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f6;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f6;
			} else if (currentFrame == 13) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f6;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f6;
			} else if (currentFrame == 15) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f5;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f5;
			} else if (currentFrame == 17) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f4;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f4;
			} else if (currentFrame == 19) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f3;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f3;
			} else if (currentFrame == 21) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f2;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f2;
			} else if (currentFrame == 23) {
				bmp_ufo_current = Images.getInstance().bmp_ufo_f1;
				bmp_ufo_shadow_current = Images.getInstance().bmp_ufo_shadow_f1;
				currentFrame = 0;
			}
			++currentFrame;
			
			if (GameLogic.getInstance().getPlayer().isAnimateLocation()) {
				GameLogic.getInstance().getPlayer().setAnimPosition(GameLogic.interpolatePos(GameLogic.getInstance().getPlayer().getLastPosition(), GameLogic.getInstance().getPlayer().getPosition(), animPlayerLocationCounter / 30.));
				animPlayerLocationCounter = animPlayerLocationCounter + 1;
				if (animPlayerLocationCounter == 30) {
					animPlayerLocationCounter = 1;
					GameLogic.getInstance().getPlayer().setAnimateLocation(false);
				}
			}

			if (GameLogic.getInstance().getPlayer().isAnimateDirection()) {
				GameLogic.getInstance().getPlayer().setAnimDirection(GameLogic.interpolateDir(GameLogic.getInstance().getPlayer().getLastDirection(), GameLogic.getInstance().getPlayer().getDirection(), (float) (animPlayerDirectionCounter / 30.)));
				animPlayerDirectionCounter = animPlayerDirectionCounter + 1;
				if (animPlayerDirectionCounter == 30) {
					animPlayerDirectionCounter = 1;
					GameLogic.getInstance().getPlayer().setAnimateDirection(false);
				}
			}
		}



	}

}
