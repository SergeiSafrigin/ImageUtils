package kcg.core.light;

public class GeometryLight extends VisualLight {
	private static final long serialVersionUID = 8220238100284829762L;
	private static final String TAG = "GeometryLightFilter";
	public int registrationId = -1;
	public boolean usedForRegistration = false;
	public Point3d location;
	public int registeredFrames;
	public int unregisteredFrames;
	public double distance;
	public double yaw, pitch;
	private double mathYaw, mathPitch;
	public boolean mainLight;
	public double fixedX, fixedY;
	
	private ImageConfig config;
		
	public GeometryLight(ImageConfig config, VisualLight visualLight, Point3d userLocation, float yaw, float pitch, float roll){
		super(config, visualLight);
		this.config = config;
		location = new Point3d();
		fixOrientationError(yaw, pitch, roll);
		calcYaw(yaw, pitch, roll);
		calcPitch(pitch, roll);
		calcDistance();
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
	}
	
	public void register(GeometryLight light){
		registrationId = light.registrationId;
		registeredFrames = light.registeredFrames+1;
		unregisteredFrames = 0;
		location = light.location;
		light.usedForRegistration = true;
	}

	public boolean unregister(){
		if (unregisteredFrames > 5)
			return true;
		unregisteredFrames++;
		return false;
	}

	public void updateUserLocationFromLight(Point3d userLocation){
		double teta = mathPitch;
		double pi = mathYaw;
		double x = distance * Math.sin(teta) * Math.cos(pi);
		double y = distance * Math.sin(teta) * Math.sin(pi);
		double z = -(distance * Math.cos(teta));
				
		userLocation.set(location.x - x, location.y - y, location.z - z);
	}

	public void calcLocation(Point3d userLocation){		
		double teta = mathPitch;
		double pi = mathYaw;
		double x = distance * Math.sin(teta) * Math.cos(pi);
		double y = distance * Math.sin(teta) * Math.sin(pi);
		double z = -(distance * Math.cos(teta));
		
		location.set(userLocation.x + x, userLocation.y + y, userLocation.z + z);
	}

	private void fixOrientationError(double yaw, double pitch, double roll){
		double pixelPerAngle = config.getHeight()/config.getvAngle();

		fixedY = y-(((pitch*-1)-90)*pixelPerAngle);		
		fixedX = x + (roll * pixelPerAngle);
	}

	private void calcYaw(float yaw, float pitch, float roll){
		double angle;

		if (config.getCamera() == ImageConfig.Camera.FRONT) {
			angle = (450 + yaw - Math.toDegrees(Math.atan2(config.getHeight()/2 - fixedY, fixedX - config.getWidth()/2)))%360;
		} else 
			angle = (yaw + (config.gethAngle()/config.getWidth())*(x - (config.getWidth()/2)))%360;
		if (angle < 0)
			angle = 360 + angle;
		
		this.yaw = angle;
		
		angle = (450 - angle)%360;
		if (angle > 180)
			angle -= 360;
		this.mathYaw = Math.toRadians(angle);
	}

	private void calcPitch(float pitch, float roll){
		double angle = 0;

		if (config.getCamera() == ImageConfig.Camera.FRONT) {
			double anglePerPixel = config.getvAngle()/config.getHeight();

			distance = Math.sqrt(Math.pow(fixedX - (config.getWidth()/2), 2) + Math.pow(fixedY - (config.getHeight()/2), 2));
			angle = 90 - (anglePerPixel * distance);
		}		
		else
			angle = (pitch + (config.getvAngle()/config.getHeight())*((config.getHeight()/2) - y))%360;
		if (angle < 0)
			angle = 360 + angle;
		
		this.pitch = angle;
		this.mathPitch = Math.toRadians(angle + 90);
	}

	private void calcDistance(){
		//TODO change the 200 parameter to -> light's height - user's height
		distance = 200 / Math.sin(Math.toRadians(pitch));
	}

	public boolean goodForLocation(){
		if (pitch > 0 && !edgeOfScreen)
			return true;
		return false;
	}

	public GeometryLight clone(){
		try {
			return (GeometryLight)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
