package kcg.core.light.filter;

import java.util.ArrayList;
import java.util.HashSet;

import android.graphics.Color;

import kcg.core.light.ImageConfig;
import kcg.core.light.VisualLight;

public class LightFilter {
	private static final String TAG = "LightFilter";
	private static final int BLACK = Color.BLACK;
	private static final int WHITE = Color.WHITE;

	private boolean edgePixel;
	private boolean edgeOfScreen;

	private final int width;
	private final int height;

	private int[][] mat;
	private int[] edgePixelsX;
	private int[] edgePixelsY;

	private int lightsFound;
	private int edgePixelsFound;

	private VisualLight[] lights;
	private ArrayList<HashSet<Integer>> doubleIndexesLists;
	
	private ImageConfig config;
	
	public LightFilter(ImageConfig config){
		this.config = config;
		width = config.getWidth();
		height = config.getHeight();
		mat = new int[height][width];

		edgePixelsX = new int[10000];
		edgePixelsY = new int[10000];

		lights = new VisualLight[200];
		doubleIndexesLists = new ArrayList<HashSet<Integer>>();
	}

	public ArrayList<VisualLight> process(int[][] frame, int minPixelsForLight){
		edgePixelsFound = 0;
		lightsFound = 0;
		doubleIndexesLists.clear();

		findLights(frame);
		fixAvoidedLight();
		computeMassCenterForLights(minPixelsForLight);
		computeDiameter();
		return generateLightsArrayList(minPixelsForLight);
	}

	private void findLights(int[][] frame){
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				if (frame[i][j] == WHITE){
					int id = getPixelId(frame, j, i);
					if (id >= lightsFound){
						lights[id] = new VisualLight(config, id);
						lightsFound++;
					}

					lights[id].add(j, i, edgePixel, edgeOfScreen);
					mat[i][j] = id;

					if (edgePixel){
						edgePixelsX[edgePixelsFound] = j;
						edgePixelsY[edgePixelsFound] = i;
						edgePixelsFound++;
					}
				} else
					mat[i][j] = -1;
			}
		}
	}

	private void fixAvoidedLight(){
		for(HashSet<Integer> doubleIndexesSet: doubleIndexesLists){
			boolean isFirstId = true;
			int firstId = -1;

			for(Integer id: doubleIndexesSet){
				if (isFirstId){
					if (lights[id] != null){
						firstId = id;
						isFirstId = false;
					}
				} else {
					VisualLight light = lights[id];
					if (light != null){
						lights[id].realId = firstId;
						lights[firstId].add(light.x, light.y, light.numOfPixels, light.numOfEdgePixels, light.edgeOfScreen);
					}
				}
			}
		}
	}
	
	private void computeMassCenterForLights(int minPixelsForLight){
		for(int i = 0; i < lightsFound; i++){
			VisualLight light = lights[i];
			if (light != null && light.id == light.realId){
				if (light.numOfPixels >= minPixelsForLight)
					light.computeMassCenter();
			}
		}
	}
	
	private void computeDiameter(){
		for(int i = 0; i < edgePixelsFound; i++){
			VisualLight light = lights[mat[edgePixelsY[i]][edgePixelsX[i]]];
			lights[light.realId].addFarNearEdgePixel(edgePixelsX[i], edgePixelsY[i]);
		}
				
		for(int i = 0; i < edgePixelsFound; i++){
			VisualLight light = lights[mat[edgePixelsY[i]][edgePixelsX[i]]];
			lights[light.realId].addFarEdgePixel(edgePixelsX[i], edgePixelsY[i]);
		}
		
		for(int i = 0; i < lightsFound; i++){
			VisualLight light = lights[i];
			if (light != null)
				light.computeDiameter();
		}
	}

	private ArrayList<VisualLight> generateLightsArrayList(int minPixelsForLight){
		ArrayList<VisualLight> lightsList = new ArrayList<VisualLight>();
		for(int i = 0; i < lightsFound; i++){
			VisualLight light = lights[i];
			if (light != null && light.id == light.realId && light.numOfPixels >= minPixelsForLight)
				lightsList.add(light);
		}
		return lightsList;
	}

	private int getPixelId(int[][] frame, int x, int y){
		edgePixel = false;
		edgeOfScreen = false;
		int[] ids = {-1, -1, -1, -1};
		int idsFound = 0;

		if (validIndex(x-1, y)){
			if (mat[y][x-1] != -1){
				ids[0] = mat[y][x-1];
				idsFound++;
			} else
				edgePixel = true;
		} else {
			edgePixel = true;
			edgeOfScreen = true;
		}

		if (validIndex(x, y-1)){
			if (mat[y-1][x] != -1){
				ids[1] = mat[y-1][x];
				idsFound++;
			} else
				edgePixel = true;
		} else {
			edgePixel = true;
			edgeOfScreen = true;
		}

		if (validIndex(x-1, y-1)){
			if (mat[y-1][x-1] != -1){
				ids[2] = mat[y-1][x-1];
				idsFound++;
			} else
				edgePixel = true;
		} else {
			edgePixel = true;
			edgeOfScreen = true;
		}

		if (validIndex(x+1, y-1)){
			if (mat[y-1][x+1] != -1){
				ids[3] = mat[y-1][x+1];
				idsFound++;
			} else
				edgePixel = true;
		} else {
			edgePixel = true;
			edgeOfScreen = true;
		}

		if (!edgePixel)
			checkForEdgePixel(frame, x, y);

		if (idsFound == 0)
			return lightsFound;

		if (idsFound == 1){
			for(int i = 0; i < ids.length; i++){
				if (ids[i] != -1)
					return ids[i];
			}
		}

		return addDoubleIndexes(ids);
	}

	private int addDoubleIndexes(int[] ids){
		for(HashSet<Integer> doubleIndexesSet: doubleIndexesLists){
			for(int i = 0; i < ids.length; i++){
				if (doubleIndexesSet.contains(ids[i])){
					for(int j = 0; j < ids.length; j++){
						if (j != i && ids[j] != -1 && ids[i] != ids[j])
							doubleIndexesSet.add(ids[j]);
					}
					return ids[i];
				}
			}
		}

		HashSet<Integer> newSet = new HashSet<Integer>();
		for(int i = 0; i < ids.length; i++){
			if (ids[i] != -1)
				newSet.add(ids[i]);
		}
		doubleIndexesLists.add(newSet);
		return newSet.iterator().next();
	}

	private void checkForEdgePixel(int[][] frame, int x, int y){
		if (validIndex(x, y+1)){
			if (frame[y+1][x] == BLACK){
				edgePixel = true;
				return;
			}
		} else {
			edgePixel = true;
			edgeOfScreen = true;
		}

		if (validIndex(x+1, y)){
			if (frame[y][x+1] == BLACK){
				edgePixel = true;
				return;
			}
		} else {
			edgePixel = true;
			edgeOfScreen = true;
		}

		if (validIndex(x-1, y+1)){
			if (frame[y+1][x-1] == BLACK){
				edgePixel = true;
				return;
			}
		} else {
			edgePixel = true;
			edgeOfScreen = true;
		}

		if (validIndex(x+1, y+1)){
			if (frame[y+1][x+1] == BLACK){
				edgePixel = true;
				return;
			}
		} else {
			edgePixel = true;
			edgeOfScreen = true;
		}
	}

	private boolean validIndex(int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height)
			return false;
		return true;
	}

	public int[] getEdgePixelsX() {
		return edgePixelsX;
	}

	public int[] getEdgePixelsY() {
		return edgePixelsY;
	}

	public int getEdgePixelsFound() {
		return edgePixelsFound;
	}
}
