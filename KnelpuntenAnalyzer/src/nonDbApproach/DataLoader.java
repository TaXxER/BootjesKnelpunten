package nonDbApproach;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.common.collect.ArrayListMultimap;

public class DataLoader {
	// Local settings
	public static final String filename = "D:\\hackaton\\Ship\\aggregate.txt";
	public static final String cvsSplitBy = ",";
	public static final int maxNumOfLines = 20000000;
	
	// Define structure of data
	public static final int structureCodeIndex 	= 0;
	public static final int timestampIndex 		= 1;
	public static final int boatIdIndex 		= 2;
	public static final int latitudeIndex		= 3;
	public static final int longtitudeIndex		= 4;
	
	// Define filters on data value
	public static final String structureCodeWanted = "1";
		
	public static void main(String[] args){
        File file = new File(filename);
        BufferedReader reader = null;
        
        ArrayListMultimap<Integer, AisData> boatIdToDataPoints = ArrayListMultimap.create();
        
        // iterator to check progress
        int i = 0;
        
        // Fill boatIdToDataPoints multimap
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;

            // repeat until all lines is read
            while ((line = reader.readLine()) != null && i<maxNumOfLines) {
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
            		Float latitude = Float.parseFloat(country[latitudeIndex]);
            		Float longtitude = Float.parseFloat(country[longtitudeIndex]);
            		boatIdToDataPoints.put(boatId, new AisData(timestamp, latitude, longtitude));
            	}catch(NumberFormatException e){
            		// data set contains dirty data points catching prevents
            		//  data loader from breaking on these rows
            		continue;
            	}
            }
        }catch(FileNotFoundException e){
        	e.printStackTrace();
        }catch(IOException e){
        	e.printStackTrace();
        }
        
        // Test boatIdToDataPoints multimap
        for(int key: boatIdToDataPoints.keySet()){
        	System.out.println("key: "+key+", value: "+boatIdToDataPoints.get(key));
        }
	}
}
