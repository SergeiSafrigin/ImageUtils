package kcg.core.filters;

import android.graphics.Color;

public class BinaryFilter {
	private static final int BLACK = Color.BLACK;
	private static final int WHITE = Color.WHITE;
	
	public static int[] GRAY_TO_BINARY(int[] src, int[] dst, int threshold, int width, int height){
		int r,g,b;
		int size = width*height;
		for(int i = 0; i < size; i++){
			r = (src[i] >> 16) & 0xff;
			g = (src[i] >> 8) & 0xff;
			b = src[i] & 0xff;
			
			int level = (int) Math.sqrt((r*r) + (g*g) + (b*b));
			if (level >= threshold)
				dst[i] = WHITE;
			else
				dst[i] = BLACK;
		}
		return dst;
	}
	
	public static int[] GRAY_TO_BINARY(byte[] src, int[] dst, int threshold, int width, int height){
		int level;
		int size = width*height;
		for(int i = 0; i < size; i++){
			level = src[i] & 0xff;
			if (level >= threshold)
				dst[i] = WHITE;
			else
				dst[i] = BLACK;
		}
		return dst;
	}
	
	public static int[] NV21_TO_BINARY(byte[] src, int[] dst, int threshold, int width, int height) {
		int p;
		int level;
	    int size = width*height;
	    for(int i = 0; i < size; i++) {
	        p = src[i] & 0xFF;	        
	        dst[i] = 0xff000000 | p<<16 | p<<8 | p;
			
			level = dst[i] & 0xff;
			if (level >= threshold)
				dst[i] = WHITE;
			else
				dst[i] = BLACK;
	    }
	    return dst;
	}
	
	public static int[] RGB_TO_BINARY(int[] src, int[] dst, int threshold, int width, int height) {
		int p;
		int level;
	    int size = width*height;
	    for(int i = 0; i < size; i++) {
	        p = src[i] & 0xFF;	        
	        dst[i] = 0xff000000 | p<<16 | p<<8 | p;
			
			level = dst[i] & 0xff;
			if (level >= threshold)
				dst[i] = WHITE;
			else
				dst[i] = BLACK;
	    }
	    return dst;
	}
}
