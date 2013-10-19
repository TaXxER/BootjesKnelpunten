package nonDbApproach;

public class AisData {
	private Long unixTime;
	private float latitude;
	private float longtitude;
	
	public AisData(Long unixTime, float latitude, float longtitude){
		this.unixTime 	= unixTime;
		this.latitude 	= latitude;
		this.longtitude = longtitude;
	}

	public Long getUnixTime() {
		return unixTime;
	}

	public void setUnixTime(Long unixTime) {
		this.unixTime = unixTime;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(float longtitude) {
		this.longtitude = longtitude;
	}
	
	@Override
	public String toString(){
		return ""+this.unixTime;
	}
}
