package nonDbApproach;

public class AisData {
	private Long unixTime;
	private Location location;
	
	public AisData(Long unixTime, Location location){
		this.unixTime 	= unixTime;
		this.location   = location;
	}
	
	public Long getUnixTime() {
		return unixTime;
	}

	public void setUnixTime(Long unixTime) {
		this.unixTime = unixTime;
	}

	public Location getLocation() {
		return location;
	}
	
	@Override
	public String toString(){
		return ""+this.unixTime;
	}
}
