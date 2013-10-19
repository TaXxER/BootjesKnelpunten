package hotspots;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import config.LocalSettings;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		listFilesForFolder(folder);
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	
		
		int[][] locations = new int[300][400];
		
		double factor = 1e2; // = 1 * 10^2 = 100.
		
		int lineCount = 1;

		
		try {
			 
			br = new BufferedReader(new FileReader(LocalSettings.AGGREGATE_URL));
			while ((line = br.readLine()) != null) {
	 
			        // use comma as separator
				String[] seperatedLine = line.split(cvsSplitBy);
	 
				if (Integer.parseInt(seperatedLine[0]) == 1 ){
					
					double result = Math.round(Double.parseDouble(seperatedLine[3]) * factor) / factor;
					
					double result2 = Math.round(Double.parseDouble(seperatedLine[4]) * factor) / factor;
					
//					System.out.println("Long: " + result
//							+ " 	Rest: "+result2);
//					
					
					if (result > 50 && result < 53 && result2 > 3 && result2 < 7){
						
						locations[(int) ((result-50)*100)][(int) ((result2-3)*100)] +=1;
					}
					
				}
				
				lineCount++;
				if(lineCount%10000 == 0){
					System.out.println(lineCount);
				}
	 
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println("Done");
		
		System.out.println("spec count: ");
		
		printTopN(100,locations );
		
		
	}
	
	public static void printTopN(int n, int[][] cords){
		
		gps[] topN = new gps[n];
		
		for (int x = 0; x < n; x++){
			topN[x] = new gps(0, x, cords[x][0]);
		}
		
		for (int x = n; x < 300; x++){
			for (int y = 0; y < 350; y++){
				
				if (topN[n-1].count < cords[x][y]){
					boolean found = false;
					gps gpsTmp;
					gps gpsTmp2 = null;
					
					for (int i = 0; i < n; i++){
						if (found){
							gpsTmp = gpsTmp2;
							gpsTmp2 = topN[i];
							topN[i] = gpsTmp;
						} else if (topN[i].count < cords[x][y]){
							found = true;
							gpsTmp2 = topN[i];
							topN[i] = new gps(y, x, cords[x][y]);
						}
					}
					
				}
				
				
			}
		}
		
		for( int x = 0; x < n; x++){
			double lon = (((double)topN[x].lo+5000d)/100d);
			double len = (((double)topN[x].le+300d)/100d);
			System.out.println("Top "+x+": "+topN[x].count+" "+lon+", "+len);
			
		}
	}
		
//	public static void printTopN(int n, int[][] cords){
//		Map lol = new HashMap<Integer, gps>();
//		
//		int[] topN = new int[n];
//		
//		for (int x; x < 300; x++){
//			for (int y; y < 300; y++){
//				
//				if (topN[n-1] < cords[x][y]){
//					boolean found = false;
//					int tmp;
//					gps gpsTmp;
//					
//					for (int i = 0; i < n; i++){
//						if (topN[i] < cords[x][y]){
//							
//						}
//					}
//					
//				}
//				
//				
//			}
//		}
//		
//		
//	}
	
//
//
//	public static int[] setInTop(int n, int num, int[] top){
//		for (int i = n-1; i>= 0; i--){
//			if (topN[i])
//		}
//		
//	}
//	
	
//	public static void listFilesForFolder(final File folder) {
//	    for (final File fileEntry : folder.listFiles()) {
//	        if (fileEntry.isDirectory()) {
//	            listFilesForFolder(fileEntry);
//	        } else {
//	            System.out.println(fileEntry.getName());
//	        }
//	    }
//	}


}
