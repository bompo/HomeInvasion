package de.swagner.homeinvasion;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Images {

	private static Images instance;

	/** Explosion Animation **/
	public Bitmap bmp_item_current;
	public Bitmap bmp_item_f1;
	public Bitmap bmp_item_f2;
	public Bitmap bmp_item_f3;
	public Bitmap bmp_item_f4;
	public Bitmap bmp_item_f5;
	public Bitmap bmp_item_f6;
	public Bitmap bmp_item_f7;
	public Bitmap bmp_item_f8;
	public Bitmap bmp_item_f9;
	public Bitmap bmp_item_f10;
	public Bitmap bmp_item_f11;
	public Bitmap bmp_item_f12;
	public Bitmap bmp_item_f13;
	public Bitmap bmp_item_f14_1;
	public Bitmap bmp_item_f14_2;
	public Bitmap bmp_item_f14_3;
	public Bitmap bmp_item_f14_4;
	
	/** Target Animation **/
	public Bitmap bmp_target_f1;
	public Bitmap bmp_target_f2;
	public Bitmap bmp_target_f3;
	public Bitmap bmp_target_f4;
	public Bitmap bmp_target_f5;

	private Images() {
		bmp_item_current = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_result_1);
		bmp_item_f1 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f1);
		bmp_item_f2 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f2);
		bmp_item_f3 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f3);
		bmp_item_f4 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f4);
		bmp_item_f5 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f5);
		bmp_item_f6 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f6);
		bmp_item_f7 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f7);
		bmp_item_f8 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f8);
		bmp_item_f9 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f9);
		bmp_item_f10 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f10);
		bmp_item_f11 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f11);
		bmp_item_f12 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f12);
		bmp_item_f13 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_f13);
		bmp_item_f14_1 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_result_1);
		bmp_item_f14_2 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_result_2);
		bmp_item_f14_3 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_result_3);
		bmp_item_f14_4 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_result_4);
		bmp_target_f1 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_target_f1);
		bmp_target_f2 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_target_f2);
		bmp_target_f3 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_target_f3);
		bmp_target_f4 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_target_f4);
		bmp_target_f5 = BitmapFactory.decodeResource(HomeInvasionApp.getContext().getResources(), R.drawable.explosion_target_f5);
	}

	public Bitmap getExplosionResult() {
		Random generator = new Random(191232131);
		switch (generator.nextInt(4) + 1) {
		case 1:
			return bmp_item_f14_1;
		case 2:
			return bmp_item_f14_2;
		case 3:
			return bmp_item_f14_3;
		default:
			return bmp_item_f14_4;
		}
	}

	public synchronized static Images getInstance() {
		if (instance == null) {
			instance = new Images();
		}
		return instance;
	}
}
