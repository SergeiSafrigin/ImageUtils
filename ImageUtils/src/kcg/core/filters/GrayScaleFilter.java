package kcg.core.filters;


public class GrayScaleFilter {
	
	
	public static int[] RGB_TO_GRAY(int[] src, int[] dst, int width, int height){
		int r,g,b;
		int size = width*height;
		for(int i = 0; i < size; i++){
			r = (src[i] >> 16) & 0xff;
			g = (src[i] >> 8) & 0xff;
			b = src[i] & 0xff;
			
			int grayLevel = (r + g + b) / 3;
			dst[i] = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
		}
		return dst;
	}
	
	public static int[] NV21_TO_GRAY(byte[] src, int[] dst, int width, int height) {
		int p;
	    int size = width*height;
	    for(int i = 0; i < size; i++) {
	        p = src[i] & 0xFF;	        
	        dst[i] = 0xff000000 | p<<16 | p<<8 | p; 
	    }
	    return dst;
	}
}
