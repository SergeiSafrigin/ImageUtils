package kcg.core.bitmap;

import android.graphics.Bitmap;

public class BitmapScale {

	
	public static Bitmap scaleToFit(Bitmap src, int target_width, int target_height){
		double scaled_width;
		double scaled_height;
		
		if (target_width - src.getWidth() < target_height - src.getHeight()){
			scaled_width = target_width;
			scaled_height = src.getHeight() * (target_width/src.getWidth());
		} else {
			scaled_height = target_height;
			scaled_width = src.getWidth() * (scaled_height/src.getHeight());
		}
		
		
		return Bitmap.createScaledBitmap(src, (int)scaled_width, (int)scaled_height, true);
	}
}
