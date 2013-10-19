package nonDbApproach;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import config.LocalSettings;

public class DataLoader {
	// Local settings
	public static final String cvsSplitBy = ",";
	public static final int MAX_NUM_OF_LINES = 1000000;
	public static final double STATIONARY_DISTANCE = 5.0;
	public static final double SAME_PLACE_THRESHOLD = 10.0;
	public static final int MAX_OLD_DATAPOINT_MILLIS = 5 * 60 * 1000; // 5 minutes
	
	// Define structure of data
	public static final int structureCodeIndex 	= 0;
	public static final int timestampIndex 		= 1;
	public static final int boatIdIndex 		= 2;
	public static final int latitudeIndex		= 3;
	public static final int longitudeIndex		= 4;
	
	// Define filters on data value
	public static final String structureCodeWanted = "1";
		
	public static void main(String[] args){
        File file = new File(LocalSettings.AGGREGATE_URL);
        BufferedReader reader = null;
        
        HashMap<Integer, AisData> boatIdToDataPoint = new HashMap<Integer, AisData>();
        HashMap<Integer, Integer> boatOccurrences = new HashMap<Integer, Integer>();
        HashMap<Location, Integer> stationaryCount = new HashMap<Location, Integer>();
    	class ValueComparator implements Comparator<Location> {

    	    Map<Location, Integer> base;
    	    public ValueComparator(Map<Location, Integer> base) {
    	        this.base = base;
    	    }

    	    // Note: this comparator imposes orderings that are inconsistent with equals.    
    	    public int compare(Location a, Location b) {
    	        if (base.get(a) >= base.get(b)) {
    	            return -1;
    	        } else {
    	            return 1;
    	        } // returning 0 would merge keys
    	    }
    	}
        ValueComparator sorter =  new ValueComparator(stationaryCount);
        TreeMap<Location, Integer> sorted_map = new TreeMap<Location, Integer>(sorter);
        
        // iterator to check progress
        int i = 0;
        
        // Fill boatIdToDataPoints multimap
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;

            // repeat until all lines is read
            while ((line = reader.readLine()) != null && i<MAX_NUM_OF_LINES) {
            	i++;
            	if((i % 100000)==0)
            		System.out.println("lines read: "+(i/100000)+"M");
            	// split line by comma
            	String[] country = line.split(cvsSplitBy);
            	// only process data points with first attribute = 1
            	if(!country[structureCodeIndex].equals(structureCodeWanted))
            		continue;
            	try{
            		// Parse data point
            		Integer boatId = Integer.parseInt(country[boatIdIndex]);
            		Integer occurrences = boatOccurrences.get(boatId)==null ? 0 : boatOccurrences.get(boatId);
            		boatOccurrences.put(boatId, occurrences+1);
            		
            		Long timestamp = Long.parseLong(country[timestampIndex]);
            		double latitude = Double.parseDouble(country[latitudeIndex]);
            		double longitude = Double.parseDouble(country[longitudeIndex]);
            		Location newLocation = new Location(latitude,longitude);
            		
            		// Discard data when in known port
            		if(newLocation.isPort())
            			continue;
            		
            		if(boatIdToDataPoint.containsKey(boatId)){
            			AisData oldData = boatIdToDataPoint.get(boatId);
            			Location oldLocation = oldData.getLocation();
            			if(oldLocation.distanceTo(newLocation) < STATIONARY_DISTANCE &&
            			   (timestamp - oldData.getUnixTime()) < MAX_OLD_DATAPOINT_MILLIS){
            				boolean existingApproximateLocation = false;
            				
            				// temp file for stationaryCount, because editing stationaryCount while looping over it throws ConcurrentModificationException exception
            				HashMap<Location, Integer> stationaryCountTemp = new HashMap<Location, Integer>();
            				List<Location> stationaryCountRemoveTemp = new ArrayList<Location>();
            				
            				for(Location existingLocation: stationaryCount.keySet()){
            					if(existingLocation.distanceTo(newLocation) < SAME_PLACE_THRESHOLD){
                    				int count = stationaryCount.get(existingLocation);
                    				Location aveLocation = Location.averageLocation(existingLocation, newLocation, count);
                    				
                    				boolean existingApproximateLocation2 = false;
                    				for(Location existingLocation2: stationaryCount.keySet()){
                    					if(existingLocation2.distanceTo(aveLocation) <SAME_PLACE_THRESHOLD){
                    						stationaryCount.put(existingLocation2, stationaryCount.get(existingLocation2)+1);
                    						existingApproximateLocation2 = true;
                    					}
                    				}
                    				
                    				if(existingApproximateLocation2){
                    					stationaryCountTemp.put(aveLocation, count+1);                    				
                    					stationaryCountRemoveTemp.add(existingLocation);
                    					existingApproximateLocation = true;
                    				}
            					}
            				}
            				
            				// Write stationaryCountTemp to stationaryCount
            				stationaryCount.putAll(stationaryCountTemp);
            				for(Location remLoc:stationaryCountRemoveTemp)
            					stationaryCount.remove(remLoc);
            				
            				if(!existingApproximateLocation){
            					stationaryCount.put(newLocation, 1);
            				}
            			}
            			// Overwrite value in map with new timestamp/location
            			boatIdToDataPoint.put(boatId, new AisData(timestamp, newLocation));
            		}else
            			// Add timestamp/location of new boat to map
            			boatIdToDataPoint.put(boatId, new AisData(timestamp, newLocation));
            		
            		// temp file for stationaryCount, because editing stationaryCount while looping over it throws ConcurrentModificationException exception
    				HashMap<Location, Integer> stationaryCountTemp = new HashMap<Location, Integer>();
    				List<Location> stationaryCountRemoveTemp = new ArrayList<Location>();
            		
    				// Write stationaryCountTemp to stationaryCount
    				stationaryCount.putAll(stationaryCountTemp);
    				for(Location remLoc:stationaryCountRemoveTemp)
    					stationaryCount.remove(remLoc);
            		
            		
            	}catch(NumberFormatException e){
            		// data set contains dirty data points catching prevents
            		// data loader from breaking on these rows
            		continue;
            	}
            }
        }catch(FileNotFoundException e){
        	e.printStackTrace();
        }catch(IOException e){
        	e.printStackTrace();
        }
        
        sorted_map.putAll(stationaryCount);
        System.out.println(sorted_map);
	}

}
