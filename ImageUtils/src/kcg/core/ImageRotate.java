package kcg.core;


public class ImageRotate {

	/**
	 * 
	 * @param src
	 * @param dst
	 * @param width
	 * @param height
	 */
	public static int[] flip(int[] src, int[] dst, int width, int height){
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				dst[(width*i) + j] = src[(width*i) + width-j-1];
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
	 */
	public static void flip(byte[] src, byte[] dst, int width, int height){

	}

	/**
	 * 
	 * @param src
	 * @param dst
	 * @param width
	 * @param height
	 * @param rotation
	 * @throws Exception 
	 */
	public static int[] rotate(int[] src, int[] dst, int width, int height, int rotation) throws Exception{
		if (rotation % 90 != 0)
			throw new RuntimeException("Rotation has to be multiple of 90");
		
		rotation %= 360;
		
		switch (rotation){
		case 0:
			dst = src.clone();
			break;
			
		case 90:
		case -270:
			for(int i = 0; i < width; i++){
				for(int j = 0; j < height; j++){
					dst[(height*i) + j] = src[width*(height-j-1)+i];
				}
			}
			break;
			
		case 180:
		case -180:
			for(int i = 0, j = src.length-1; i < src.length; i++, j--){
				dst[i] = src[j];
			}
			break;
			
		case 270:
		case -90:
			for(int i = 0; i < width; i++){
				for(int j = 0; j < height; j++){
					dst[(height*i) + j] = src[width*(j+1)-1-i];
				}
			}
			break;		
		}
		
		return dst;
	}

	/**
	 * 
	 * @param src
	 * @param dst
	 * @param width
	 * @param height
	 * @param rotation
	 */
	public static void rotate(byte[] src, byte[] dst, int width, int height, int rotation){

	}

	/**
	 * 
	 * @param src
	 * @param dst
	 * @param width
	 * @param height
	 * @param rotation
	 */
	public static void rotate(int[][] src, int[][] dst, int width, int height, int rotation){

	}
}
