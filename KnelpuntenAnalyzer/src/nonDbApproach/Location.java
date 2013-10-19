package nonDbApproach;

public class Location {
	private double latitude;
	private double longitude;
	
	public Location(double latitude, double longtitude){
		this.latitude 	= latitude;
		this.longitude = longtitude;
	}

	public double getLatitude(){
		return latitude;
	}
	
	public double getLongtitude(){
		return longitude;
	}
	
	public double distanceTo(Location newLocation){
		double dLat = Math.abs(latitude - newLocation.getLatitude());
		double dLon = Math.abs(longitude - newLocation.getLongtitude());
		return Math.sqrt((dLat*dLat)+(dLon*dLon));
	}
	
	public static Location averageLocation(Location location1, Location location2, int count){
		double aveLatitude = (count*location1.getLatitude()+location2.getLatitude()) / (count+1);
		double aveLongtitude = (count*location1.getLongtitude()+location2.getLongtitude()) / (count+1);
		return new Location(aveLatitude, aveLongtitude);
	}
	
	@Override
	public String toString(){
		return "lat: "+latitude+", lon: "+longitude;
	}
}