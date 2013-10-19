package nonDbApproach;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import config.LocalSettings;

public class DataLoader {
	// Local settings
	public static final String cvsSplitBy = ",";
	public static final int MAX_NUM_OF_LINES = 20000000;
	public static final int STATIONARY_DISTANCE = 5;
	
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
        HashMap<Location, Integer> stationaryCount = new HashMap<Location, Integer>();
        
        // iterator to check progress
        int i = 0;
        
        // Fill boatIdToDataPoints multimap
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;

            // repeat until all lines is read
            while ((line = reader.readLine()) != null && i<MAX_NUM_OF_LINES) {
            	i++;
            	if((i % 1000000)==0)
            		System.out.println("lines read: "+(i/1000000)+"M");
            	// split line by comma
            	String[] country = line.split(cvsSplitBy);
            	// only process data points with first attribute = 1
            	if(!country[structureCodeIndex].equals(structureCodeWanted))
            		continue;
            	try{
            		Integer boatId = Integer.parseInt(country[boatIdIndex]);
            		Long timestamp = Long.parseLong(country[timestampIndex]);
            		double latitude = Double.parseDouble(country[latitudeIndex]);
            		double longtitude = Double.parseDouble(country[longitudeIndex]);
            		if(boatIdToDataPoint.containsKey(boatId)){
            			Location oldLocation = boatIdToDataPoint.get(boatId).getLocation();
            			Location newLocation = new Location(latitude, longtitude);            			
            			if(oldLocation.distanceTo(newLocation) < STATIONARY_DISTANCE){
            				int count = stationaryCount.containsKey(oldLocation) ? stationaryCount.get(oldLocation)+1 : 1;
            				Location aveLocation = Location.averageLocation(oldLocation, newLocation, count);
            				stationaryCount.put(aveLocation, count);
            				stationaryCount.remove(oldLocation);
            				boatIdToDataPoint.put(boatId, new AisData(timestamp, aveLocation));
            			}else
            				boatIdToDataPoint.put(boatId, new AisData(timestamp, newLocation));
            		}else
            			boatIdToDataPoint.put(boatId, new AisData(timestamp, new Location(latitude, longtitude)));
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
        
        // Test boatIdToDataPoints multimap
        for(Location loc: stationaryCount.keySet()){
        	System.out.println("key: "+loc+", value: "+stationaryCount.get(loc));
        }
	}
}
