package kcg.core;

public class Arrays {
	
	
	public static int[][] arrayToMatrix(int[] src, int[][] dst, int width, int height){
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				dst[i][j] = src[i*width + j];
			}
		}
		
		return dst;
	}
}
