package kcg.core.light.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kcg.core.light.GeometryLight;
import kcg.core.light.ImageConfig;
import kcg.core.light.Point3d;
import kcg.core.light.VisualLight;
import android.util.Log;

public class VisualOdometryFilter {
	private static final String TAG = "LightFilter";
	private static final int maxDistancePerFrame = 10;
	private static final int maxPixelDistanceForRegistration = 30;
	private static final int maxAngleChangeForRegistration = 5;
	
	private ArrayList<GeometryLight> lastFrameLights;
	private ArrayList<GeometryLight> geometryLightsList;

	private Point3d location;
	private Point3d prevLocation;
	private Point3d opticFlow;

	private int generatedNames;

	private ImageConfig config;
	private int frames;

	public VisualOdometryFilter(ImageConfig config){
		this.config = config;
		location = new Point3d();
		init();
	}

	public VisualOdometryFilter(ImageConfig config, Point3d location){
		this.config = config;
		this.location = new Point3d(location);
		init();
	}

	private void init(){
		generatedNames = 0;
		lastFrameLights = new ArrayList<GeometryLight>();
		geometryLightsList = new ArrayList<GeometryLight>();
		opticFlow = new Point3d();
		prevLocation = new Point3d();
	}

	public ArrayList<GeometryLight> process(ArrayList<VisualLight> visualListsList, float yaw, float pitch, float roll){
		geometryLightsList = new ArrayList<GeometryLight>();

		registerLights(visualListsList, yaw, pitch, roll);

		if (location.distance3d(prevLocation) > maxDistancePerFrame)
			location = Point3d.limitDistance(prevLocation, location, maxDistancePerFrame);

		opticFlow.set(location.x - prevLocation.x, location.y - prevLocation.y, location.z - prevLocation.z);

		prevLocation.copy(location);

		ArrayList<GeometryLight> cloneList = (ArrayList<GeometryLight>)geometryLightsList.clone();
		frames++;
		return cloneList;
	}

	private void registerLights(ArrayList<VisualLight> visualLightsList, float yaw, float pitch, float roll){
		GeometryLight mainLight = null;
		GeometryLight registeredToMainLight = null;;

		Collections.sort(visualLightsList, lightComparator);

		//convert visual lights to geometry lights
		for(VisualLight visualLight: visualLightsList){
			GeometryLight geometryLight = new GeometryLight(config, visualLight, location, yaw, pitch, roll);
			if (geometryLight.pitch > 0)
				geometryLightsList.add(geometryLight);
		}

		//register lights with prevFrameLights
		for(GeometryLight newLight: geometryLightsList) {
			GeometryLight bestMatch = null;
			double minBadScore = Double.MAX_VALUE;

			for(GeometryLight prevFrameLight: lastFrameLights) {
				if (prevFrameLight.usedForRegistration)
					continue;

				double distance;

				if (config.getCamera() == ImageConfig.Camera.FRONT){
					distance = pixelDistance(newLight, prevFrameLight);
					if (distance > maxPixelDistanceForRegistration)
						continue;
				} else {
					distance = Math.abs(prevFrameLight.yaw - newLight.yaw) + Math.abs(prevFrameLight.pitch - newLight.pitch);
					if (distance > maxAngleChangeForRegistration)
						continue;
				}

				if (distance < minBadScore) {
					minBadScore = distance;
					bestMatch = prevFrameLight;
				}
			}

			if (bestMatch != null) {
				newLight.register(bestMatch);
			}
		}

		mainLight = calcMainLight(geometryLightsList);
		
		if (mainLight != null)
			mainLight.updateUserLocationFromLight(location);

		//calculate location by main light

		boolean newLightsFound = false;

		//generate registration id for the lights that found no match
		for(GeometryLight newLight: geometryLightsList) {
			if (newLight.registrationId == -1) {
				newLight.calcLocation(location);
				
				newLight.registrationId = generateName();
				newLightsFound = true;
			}
		}

		if (!newLightsFound && geometryLightsList.size() != lastFrameLights.size())
			newLightsFound = true;

		//find mainLight's last frame registration
		if (mainLight != null) {
			for(GeometryLight prevFrameLight: lastFrameLights) {
				if (prevFrameLight.registrationId == mainLight.registrationId) {
					registeredToMainLight = prevFrameLight;
					break;
				}
			}
		}

		//set last frames as the new frames
		if (newLightsFound || (frames%5) == 4 || registeredToMainLight == null)
			lastFrameLights = geometryLightsList;
		else {
			for(GeometryLight prevFrameLight: lastFrameLights) {
				prevFrameLight.usedForRegistration = false;
			}
		}
	}
	
	private GeometryLight calcMainLight(ArrayList<GeometryLight> lights){
		double maxFatness = Integer.MIN_VALUE;
		GeometryLight bestLight = null;

		for (int i = 75; i > 0; i-= 15) {
			for(GeometryLight light: lights) {
				if (light.pitch >= i && light.fatness > maxFatness && light.goodForLocation() && light.registrationId != -1){
					maxFatness = light.fatness;
					bestLight = light;
				}
			}

			if (bestLight != null) {
				bestLight.mainLight = true;
				return bestLight;
			}
		}

		for (int i = 75; i > 0; i-= 15) {
			for(GeometryLight light: lights) {
				if (light.pitch >= i && light.fatness > maxFatness && light.registrationId != -1){
					maxFatness = light.fatness;
					bestLight = light;
				}
			}

			if (bestLight != null) {
				bestLight.mainLight = true;
				return bestLight;
			}
		}

		return null;
	}
	
	public Point3d getOpticFlow(){
		return opticFlow;
	}

	private double pixelDistance(GeometryLight l1, GeometryLight l2){
		return Math.sqrt(Math.pow(l1.fixedY - l2.fixedY, 2) + 
				Math.pow(l1.fixedX - l2.fixedX, 2));
	}
	
	/**
	 * @return visual odometry location 3d location in centimeters
	 */

	public Point3d getLocationInCm(){
		return location;
	}
	
	/**
	 * @return visual odometry location 3d location in meters
	 */
	public Point3d getLocationInMt(){
		Point3d loc = new Point3d(location);
		loc.divide(100);
		return loc;
	}

	private int generateName(){
		if (generatedNames >= 1000)
			generatedNames = 0;
		return generatedNames++;
	}

	public void reset(){
		location.reset();
		geometryLightsList.clear();
		location.reset();
		prevLocation.reset();
		opticFlow.reset();
		generatedNames = 0;
	}

	Comparator<VisualLight> lightComparator = new Comparator<VisualLight>() {

		@Override
		public int compare(VisualLight l1, VisualLight l2) {
			if (l1 == null && l2 == null)
				return 0;
			if (l1 == null)
				return 1;
			if (l2 == null)
				return -1;

			if (l1.numOfPixels > l2.numOfPixels)
				return -1;
			if (l1.numOfPixels < l2.numOfPixels)
				return 1;
			return 0;
		}
	};
}
