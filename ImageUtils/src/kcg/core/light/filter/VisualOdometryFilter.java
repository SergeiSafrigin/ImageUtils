package kcg.core.light.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kcg.core.light.ImageConfig;
import kcg.core.light.GeometryLight;
import kcg.core.light.Point3d;
import kcg.core.light.VisualLight;

public class VisualOdometryFilter {
	private static final String TAG = "LightFilter";
	private static final int maxDistanceForRegistration = 10;
	private static final int maxPixelDistanceForRegistration = 30;
	private static final int maxMassCenterDistanceForRegistration = 10;

	private ArrayList<GeometryLight> lastFrameLights;
	private ArrayList<GeometryLight> geometryLightsList;
	private ArrayList<GeometryLight> emptyList;

	private Point3d location;

	private int generatedNames;

	private ImageConfig config;

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
		emptyList = new ArrayList<GeometryLight>();
		lastFrameLights = new ArrayList<GeometryLight>();
		geometryLightsList = new ArrayList<GeometryLight>();
	}

	public ArrayList<GeometryLight> process(ArrayList<VisualLight> visualListsList, float yaw, float pitch){
		emptyList = lastFrameLights;
		lastFrameLights = geometryLightsList;
		emptyList.clear();
		geometryLightsList = emptyList;

		registerLights(visualListsList, yaw, pitch);

		ArrayList<GeometryLight> cloneList = (ArrayList<GeometryLight>)geometryLightsList.clone();
		return cloneList;
	}

	private void registerLights(ArrayList<VisualLight> visualLightsList, float yaw, float pitch){
		GeometryLight mainLight = null;
		Point3d newLocation = new Point3d();

		Collections.sort(visualLightsList, lightComparator);

		for(VisualLight visualLight: visualLightsList){
			double minBadScore = Double.MAX_VALUE;
			int id = -1;

			GeometryLight geometryLight = new GeometryLight(config, visualLight, location, yaw, pitch);

			for(int j = 0; j < lastFrameLights.size(); j++){
				GeometryLight lastFrameLight = lastFrameLights.get(j);
				if (lastFrameLight == null)
					continue;
				
				double distance;
				
				if (config.getCamera() == ImageConfig.Camera.FRONT){
					distance = pixelDistance(visualLight, lastFrameLight);
					if (distance > maxPixelDistanceForRegistration)
						continue;
				} else {
					distance = geometryLight.location.distance(lastFrameLight.location);
					if (distance > maxDistanceForRegistration && 
							pixelDistance(visualLight, lastFrameLight) > maxMassCenterDistanceForRegistration)
						continue;
				}
				
				if (distance < minBadScore){
					minBadScore = distance;
					id = j;
				}
			}

			if (id != -1){
				geometryLight.register(lastFrameLights.get(id));
				lastFrameLights.set(id, null);
			} else 
				geometryLight.registrationId = generateName();

			geometryLightsList.add(geometryLight);
		}

		for(int i = 0; i < geometryLightsList.size(); i++){
			GeometryLight geometryLight = geometryLightsList.get(i);
			if (geometryLight != null && geometryLight.goodForLocation()){
				mainLight = geometryLight;
				geometryLight.mainLight = true;
				break;
			}
		}


		if (mainLight != null){
			mainLight.updateUserLocationFromLight(newLocation, yaw, pitch);
			newLocation.calculateLocation();
		}

		if (!newLocation.isBase())
			location = newLocation;
		newLocation = null;

		for(int i = 0; i < geometryLightsList.size(); i++){
			GeometryLight geometryLight = geometryLightsList.get(i);
			if (geometryLight != null && mainLight != geometryLight)
				geometryLight.calcLocation(location, yaw, pitch);
		}

		for(int i = 0; i < lastFrameLights.size(); i++){
			GeometryLight geometryLight = lastFrameLights.get(i);
			if (geometryLight != null){
				if (!geometryLight.unregister()){
					GeometryLight newLight = new GeometryLight(config, geometryLight);

					geometryLightsList.add(newLight);
				}
			}
		}
	}

	private void registerLights2(ArrayList<VisualLight> visualLightsList, float yaw, float pitch){
		Point3d newLocation = new Point3d();

		Collections.sort(visualLightsList, lightComparator);

		for(VisualLight visualLight: visualLightsList){
			double minBadScore = Double.MAX_VALUE;
			int id = -1;

			GeometryLight geometryLight = new GeometryLight(config, visualLight, location, yaw, pitch);

			for(int j = 0; j < lastFrameLights.size(); j++){
				GeometryLight lastFrameLight = lastFrameLights.get(j);
				if (lastFrameLight == null)
					continue;
				double distance = geometryLight.location.distance(lastFrameLight.location);
				if (distance > maxDistanceForRegistration && 
						pixelDistance(visualLight, lastFrameLight) > maxMassCenterDistanceForRegistration)
					continue;
				double badScore = distance*100;
				if (!visualLight.edgeOfScreen){
					badScore += Math.abs(visualLight.diameter - lastFrameLight.diameter)*50;
					badScore += Math.abs(visualLight.numOfEdgePixels - lastFrameLight.numOfEdgePixels)*10;
					badScore += Math.abs(visualLight.numOfPixels - lastFrameLight.numOfPixels);
				}

				if (badScore < minBadScore){
					minBadScore = badScore;
					id = j;
				}

			}

			if (id != -1){
				geometryLight.register(lastFrameLights.get(id));
				geometryLight.updateUserLocationFromLight(newLocation, yaw, pitch);
				lastFrameLights.set(id, null);
			} else 
				geometryLight.registrationId = generateName();

			geometryLightsList.add(geometryLight);
		}

		newLocation.calculateLocation();

		if (!newLocation.isBase())
			location = newLocation;
		newLocation = null;

		for(int i = 0; i < geometryLightsList.size(); i++){
			GeometryLight geometryLight = geometryLightsList.get(i);
			if (geometryLight != null)
				geometryLight.calcLocation(location, yaw, pitch);
		}

		for(int i = 0; i < lastFrameLights.size(); i++){
			GeometryLight geometryLight = lastFrameLights.get(i);
			if (geometryLight != null){
				if (!geometryLight.unregister()){
					GeometryLight newLight = new GeometryLight(config, geometryLight);

					geometryLightsList.add(newLight);
				}
			}
		}
	}

	private double pixelDistance(VisualLight l1, VisualLight l2){
		return Math.sqrt(Math.pow(l1.y - l2.y, 2) + 
				Math.pow(l1.x - l2.x, 2));
	}

	public Point3d getLocation(){
		return location;
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
