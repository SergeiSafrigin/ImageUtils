package kcg.core.light;

import java.io.Serializable;

public class Point3d implements Serializable{
	private static final long serialVersionUID = 3593707453658966134L;
	public double x, y, z;
	private int numOfLocationsFromLight = 0;

	public Point3d(){
		x = 0;
		y = 0;
		z = 0;
	}

	public Point3d(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point3d(Point3d p){
		x = p.x;
		y = p.y;
		z = p.z;
	}

	public void set(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void reset(){
		x = 0;
		y = 0;
		z = 0;
	}

	public void updateForUser(double x, double y, double z){
		this.x += x;
		this.y += y;
		this.z += z;
		numOfLocationsFromLight++;
	}

	public void calculateLocation(){
		if (numOfLocationsFromLight > 0){
			x /= numOfLocationsFromLight;
			y /= numOfLocationsFromLight;
			z /= numOfLocationsFromLight;

			numOfLocationsFromLight = 0;
		}
	}
	
	public void copy(Point3d p){
		x = p.x;
		y = p.y;
		z = p.z;
	}
	
	public boolean isBase(){
		if (x == 0 && y == 0 && z == 0)
			return true;
		return false;
	}
	
	public double distance(Point3d p){
		return Math.sqrt(Math.pow(p.x-x, 2) + Math.pow(p.y-y, 2) + Math.pow(p.z - z, 2) );
	}
	
	public double distance2d(Point3d p){
		return Math.sqrt(Math.pow(p.x-x, 2) + Math.pow(p.y-y, 2));
	}
}
