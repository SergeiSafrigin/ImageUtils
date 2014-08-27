package kcg.core;
import android.graphics.Bitmap;


public class ImageConvert {
	
	/**
	 * 
	 * @param src
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	
	public static Bitmap ArrayToBitmap(int[] src, Bitmap bitmap, int width, int height){
		bitmap.setPixels(src, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	/**
	 * 
	 * @param src
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap MatrixToBitmap(int[][] src, Bitmap bitmap, int width, int height){
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				bitmap.setPixel(j, i, src[i][j]);
			}
		}
		return bitmap;
	}
	
	/**
	 * 
	 * @param src
	 * @param dst
	 * @param width
	 * @param height
	 * @return
	 */
	public static int[][] NV21TORGB(byte[] src, int[][] dst, int width, int height){
		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;  
			for (int i = 0; i < width; i++, yp++) {  
				int y = (0xff & ((int) src[yp])) - 16;  
				if (y < 0)  
					y = 0;  
				if ((i & 1) == 0) {  
					v = (0xff & src[uvp++]) - 128;  
					u = (0xff & src[uvp++]) - 128;  
				}  

				int y1192 = 1192 * y;  
				int r = (y1192 + 1634 * v);  
				int g = (y1192 - 833 * v - 400 * u);  
				int b = (y1192 + 2066 * u);  

				if (r < 0)                  r = 0;               else if (r > 262143)  
					r = 262143;  
				if (g < 0)                  g = 0;               else if (g > 262143)  
					g = 262143;  
				if (b < 0)                  b = 0;               else if (b > 262143)  
					b = 262143;  

				dst[yp/width][yp%width] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);  
			}  
		}
		return dst;
	}
	
	/**
	 * 
	 * @param src
	 * @param dst
	 * @param width
	 * @param height
	 * @return
	 */
	public static int[] NV21TORGB(byte[] src, int[] dst, int width, int height){
		final int frameSize = width * height;
		
		if (dst == null)
			dst = new int[width*height];

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;  
			for (int i = 0; i < width; i++, yp++) {  
				int y = (0xff & ((int) src[yp])) - 16;  
				if (y < 0)  
					y = 0;  
				if ((i & 1) == 0) {  
					v = (0xff & src[uvp++]) - 128;  
					u = (0xff & src[uvp++]) - 128;  
				}  

				int y1192 = 1192 * y;  
				int r = (y1192 + 1634 * v);  
				int g = (y1192 - 833 * v - 400 * u);  
				int b = (y1192 + 2066 * u);  

				if (r < 0)                  r = 0;               else if (r > 262143)  
					r = 262143;  
				if (g < 0)                  g = 0;               else if (g > 262143)  
					g = 262143;  
				if (b < 0)                  b = 0;               else if (b > 262143)  
					b = 262143;  

				dst[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);  
			}  
		}
		return dst;
	}
}
