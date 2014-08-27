package kcg.core.light;

import java.io.Serializable;

public class VisualLight implements Serializable {
	private static final long serialVersionUID = -3679458911731673571L;
	public int id;
	public int realId;
	public double x, y;
	public int numOfPixels, numOfEdgePixels;
	public boolean edgeOfScreen;
	
	public double diameter;
	public double diameterHAngle;
	public double diameterVAngle;
	public double fatness;
	
	public double nearestPixelX, nearestPixelY;
	public double farthestPixel1X, farthestPixel1Y, farthestPixel2X, farthestPixel2Y;
	
	//For Farthest and nearest pixel calculation
	private double far1MaxDistance = Double.MIN_VALUE;
	private double far2MaxDistance = Double.MIN_VALUE;
	private double nearMinDistance = Double.MAX_VALUE;
	
	private ImageConfig config;
	
	public VisualLight(ImageConfig config, int id){
		this.config = config;
		this.id = id;
		realId = id;
		x = 0;
		y = 0;
		numOfPixels = 0;
		numOfEdgePixels = 0;
		edgeOfScreen = false;
	}
	
	public VisualLight(ImageConfig config, VisualLight light){
		this.config = config;
		id = light.id;
		realId = light.realId;
		x = light.x;
		y = light.y;
		numOfPixels = light.numOfPixels;
		numOfEdgePixels = light.numOfEdgePixels;
		edgeOfScreen = light.edgeOfScreen;
		
		diameter = light.diameter;
		diameterHAngle = light.diameterHAngle;
		diameterVAngle = light.diameterVAngle;
		fatness = light.fatness;
		
		nearestPixelX = light.nearestPixelX;
		nearestPixelY = light.nearestPixelY;
		farthestPixel1X= light.farthestPixel1X;
		farthestPixel1Y = light.farthestPixel1Y;
		farthestPixel2X = light.farthestPixel2X;
		farthestPixel2Y = light.farthestPixel2Y;
	}
	
	public void computeMassCenter(){
		x /= numOfPixels;
		y /= numOfPixels;
	}
	
	public void computeDiameter(){
		diameter = Math.sqrt(Math.pow(farthestPixel1X - farthestPixel2X, 2) + Math.pow(farthestPixel1Y - farthestPixel2Y, 2));
		diameterHAngle = (config.gethAngle()/config.getWidth())*Math.abs(farthestPixel1X - farthestPixel2X);
		diameterVAngle = (config.getvAngle()/config.getHeight())*Math.abs(farthestPixel1Y - farthestPixel2Y);
		fatness = Math.sqrt(Math.pow(x - nearestPixelX, 2) + Math.pow(y - nearestPixelY, 2))/Math.sqrt(Math.pow(x - farthestPixel1X, 2) + Math.pow(y - farthestPixel1Y, 2));
	}
	
	public void addFarNearEdgePixel(int x, int y){
		double distance = Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2);
		if (distance < nearMinDistance){
			nearMinDistance = distance;
			nearestPixelX = x;
			nearestPixelY = y;
		} else if (distance > far1MaxDistance){
			far1MaxDistance = distance;
			farthestPixel1X = x;
			farthestPixel1Y = y;
		}
	}
	
	public void addFarEdgePixel(int x, int y){
		double distance = Math.pow(x - farthestPixel1X, 2) + Math.pow(y - farthestPixel1Y, 2);
		if (distance > far2MaxDistance){
			far2MaxDistance = distance;
			farthestPixel2X = x;
			farthestPixel2Y = y;
		}
	}
	
	public void add(int x, int y, boolean edgePixel, boolean edgeOfScreen){
		this.x += x;
		this.y += y;
		numOfPixels++;
		if (edgePixel)
			numOfEdgePixels++;
		if (edgeOfScreen)
			this.edgeOfScreen = true;
	}
	
	public void add(double x, double y, int numOfPixels, int numOfEdgePixels, boolean edgeOfScreen){
		this.x += x;
		this.y += y;
		this.numOfPixels += numOfPixels;
		this.numOfEdgePixels += numOfEdgePixels;
		if (edgeOfScreen)
			this.edgeOfScreen = true;
	}
}
