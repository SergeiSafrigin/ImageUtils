package kcg.core.light;

import java.io.Serializable;

public class Point3d implements Serializable{
	private static final long serialVersionUID = 3593707453658966134L;
	public double x, y, z;

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
	
	public void copy(Point3d p){
		x = p.x;
		y = p.y;
		z = p.z;
	}
	
	public double distance3d(Point3d p){
		return Math.sqrt(Math.pow(p.x-x, 2) + Math.pow(p.y-y, 2) + Math.pow(p.z - z, 2) );
	}
	
	public double distance2d(Point3d p){
		return Math.sqrt(Math.pow(p.x-x, 2) + Math.pow(p.y-y, 2));
	}
	
	public static Point3d limitDistance(Point3d from, Point3d to, double distance){
		double x = to.x - from.x;
		double y = to.y - from.y;
		double z = to.z - from.z;
		
		double r = 9;
		double teta = Math.acos(z / Math.sqrt((x * x) + (y * y) + (z * z)));
		double pi = Math.atan2(y, x);
		
		x = r * Math.sin(teta) * Math.cos(pi);
		y = r * Math.sin(teta) * Math.sin(pi);
		z = r * Math.cos(teta);
		
		return new Point3d(from.x + x, from.y + y, from.z + z);
	}
	
	public String toString(){
		return "["+x+", "+y+", "+z+"]";
	}

	public void divide(int num) {
		x /= num;
		y /= num;
		z /= num;
	}
}
