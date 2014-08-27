package kcg.core.light;


public class ImageConfig {
	public static final int CM_PER_PIXEL_DISTANCE_DIAMETER = 6000;
	public static final int CM_PER_PIXEL_DISTANCE_EDGE = 20000;
	public static enum DistanceType {DIAMETER, EDGE_PIXELS};
	public static enum Device {PHONE, SMART_GLASSES, ZGPAX};
	public static enum Camera {FRONT, BACK};
	
	private DistanceType distanceType;
	private Device device;
	private Camera camera;
	private float hAngle, vAngle;
	private int height, width;
	
	public ImageConfig(DistanceType distanceType, Device device, Camera camera,
			float hAngle, float vAngle, int height, int width) {
		this.distanceType = distanceType;
		this.device = device;
		this.camera = camera;
		this.hAngle = hAngle;
		this.vAngle = vAngle;
		this.height = height;
		this.width = width;
	}
	
	public DistanceType getDistanceType() {
		return distanceType;
	}

	public void setDistanceType(DistanceType distanceType) {
		this.distanceType = distanceType;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public float gethAngle() {
		return hAngle;
	}

	public void sethAngle(float hAngle) {
		this.hAngle = hAngle;
	}

	public float getvAngle() {
		return vAngle;
	}

	public void setvAngle(float vAngle) {
		this.vAngle = vAngle;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}