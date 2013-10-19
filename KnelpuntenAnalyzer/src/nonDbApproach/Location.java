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
		return Math.sqrt((dLat*dLat)+(dLon*dLon))*100;
	}
	
	public static Location averageLocation(Location location1, Location location2, int count){
		return averageLocation(location1,location2,count,1);
	}
	
	public static Location averageLocation(Location location1, Location location2, int count1, int count2){
		double aveLatitude = (count1*location1.getLatitude()+count2*location2.getLatitude()) / (count1+count2);
		double aveLongtitude = (count1*location1.getLongtitude()+count2*location2.getLongtitude()) / (count1+count2);
		return new Location(aveLatitude, aveLongtitude);
	}
	
	public boolean isPort(){
		// Antwerpen
		if( latitude  > 51.165567 && 
			latitude  < 51.361492 &&
			longitude > 4.1194440 &&
			longitude < 4.5424180)
				return true;
		// Amsterdam
		if( latitude  > 52.322750 && 
			latitude  < 52.438432 &&
			longitude > 4.7184130 &&
			longitude < 5.0548700)
				return true;
		// Ijmuiden
		if( latitude  > 52.438432 && 
			latitude  < 52.498668 &&
			longitude > 4.5192860 &&
			longitude < 4.7184130)
				return true;
		// When no port applies
		return false;
	}
	
	@Override
	public String toString(){
		return latitude+", "+longitude;
	}
}