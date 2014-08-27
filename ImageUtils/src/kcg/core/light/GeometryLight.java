package kcg.core.light;


public class GeometryLight extends VisualLight {
	private static final long serialVersionUID = 8220238100284829762L;
	private static final String TAG = "GeometryLightFilter";
	public int registrationId;
	public Point3d location;
	public int registeredFrames;
	public int unregisteredFrames;
	public double distance;
	public double yaw;
	public double pitch;
	public boolean mainLight;
	
	private ImageConfig config;
		
	public GeometryLight(ImageConfig config, VisualLight visualLight, Point3d userLocation, float yaw, float pitch){
		super(config, visualLight);
		this.config = config;
		location = new Point3d();
		calcDistance();
		calcLocation(userLocation, yaw, pitch);
	}
	
	public GeometryLight(ImageConfig config, GeometryLight geometryLight){
		super(config, geometryLight);
		this.config = config;
		this.registrationId = geometryLight.registrationId;
		this.location = geometryLight.location;
		this.registeredFrames = geometryLight.registeredFrames;
		this.unregisteredFrames = geometryLight.unregisteredFrames;
		this.distance = geometryLight.distance;
		this.yaw = geometryLight.yaw;
		this.pitch = geometryLight.pitch;
//		mainLight = geometryLight.mainLight;
	}
	
	public void register(GeometryLight light){
		registrationId = light.registrationId;
		registeredFrames = light.registeredFrames+1;
		unregisteredFrames = 0;
		location = light.location;
//		if (goodForLocation())
//			mainLight = light.mainLight;
	}

	public boolean unregister(){
		if (unregisteredFrames > 5)
			return true;
		unregisteredFrames++;
		return false;
	}

	public void calcLocation(Point3d userLocation,float yaw, float pitch){
		this.yaw = getYawFromLight(yaw, pitch);
		this.pitch = getPitchFromLight(pitch);
		
		double rYaw = Math.toRadians(this.yaw);
		double rPitch = Math.toRadians(this.pitch);
		
		double z = distance * Math.sin(rPitch);
		double r = distance * Math.cos(rPitch);
		
		double x = r * Math.sin(rYaw);
		double y = r * Math.cos(rYaw);

		location.set(userLocation.x + x, userLocation.y + y, userLocation.z + z);
	}

	public void updateUserLocationFromLight(Point3d newLocation, float yaw, float pitch){
		if (goodForLocation()){	
			double rYaw = Math.toRadians(getYawFromLight(yaw, pitch));
			double rPitch = Math.toRadians(getPitchFromLight(pitch));
			double z = distance * Math.sin(rPitch);
			double r = distance * Math.cos(rPitch);
			
			double x = r * Math.sin(rYaw);
			double y = r * Math.cos(rYaw);

			newLocation.updateForUser(location.x - x, location.y - y, location.z - z);
		}
	}

	public double getYawFromLight(float yaw, float pitch){
		double angle;
		
		if (config.getCamera() == ImageConfig.Camera.FRONT){
			double y;
			
			if (pitch > 90)
				y = this.y - (pitch - 90)*(config.getHeight()/config.getvAngle());
			else 
				y = this.y + (pitch - 90)*(config.getHeight()/config.getvAngle());
			
			angle = (450 + yaw - Math.toDegrees(Math.atan2(config.getHeight()/2 - y, x - config.getWidth()/2)))%360;
		} else 
			angle = (yaw + (config.gethAngle()/config.getWidth())*(x - (config.getWidth()/2)))%360;
		if (angle < 0)
			angle = 360 + angle;
		return angle;
	}

	public double getPitchFromLight(float pitch){
		double angle;
		
		if (config.getCamera() == ImageConfig.Camera.FRONT){
			double distance = Math.sqrt(Math.pow(x - (config.getWidth()/2), 2) + Math.pow(y - (config.getHeight()/2), 2));
			angle = (config.getvAngle()/config.getHeight())*distance;
			
			double fixedPitch;
			if (pitch > 90)
				fixedPitch = pitch - angle;
			else
				fixedPitch = pitch + angle;
			
			angle = 90 - Math.abs(90 - fixedPitch);
		}		
		else
			angle = (pitch + (config.getvAngle()/config.getHeight())*((config.getHeight()/2) - y))%360;
		if (angle < 0)
			angle = 360 + angle;
		return angle;
	}

	public void calcDistance(){
		if (config.getDistanceType() == ImageConfig.DistanceType.EDGE_PIXELS)
			distance = ImageConfig.CM_PER_PIXEL_DISTANCE_EDGE/numOfEdgePixels;
		else
			distance = ImageConfig.CM_PER_PIXEL_DISTANCE_DIAMETER/diameter;
//		
	}
	
	public boolean goodForLocation(){
		if (registeredFrames >= 10 && (!edgeOfScreen || config.getCamera() == ImageConfig.Camera.FRONT 
				|| config.getDevice() == ImageConfig.Device.ZGPAX))
			return true;
		return false;
	}
}
