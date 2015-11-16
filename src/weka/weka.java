package weka;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import getAttribute.GetAttr;
import transferToSDB.T2SDB;

public class weka {
    public static void main(String[] args) {
    	try { 
    	    String path = "petro_subset1_2010.csv";
    	    ArrayList<ArrayList<String>> records = readCSV(path);
    	    /**Feature Extraction**/    	     	   
    	    GetAttr.featureExtraction("weka.csv", records);    	        	        	  
    	    
    	    /**Translate to SDB**/
    	    T2SDB t2sdb = new T2SDB();   
    	    HashMap<Integer, String> feature_target = GetAttr.featureExtraction_target(records);
    	    t2sdb.translate_training(3, "weka.csv", feature_target, "weka.txt");
    	        	 
    	} catch (FileNotFoundException e) {
    		System.out.println("[ERROR] File Not Found Exception.");
            e.printStackTrace();    		
    	}
    }
    
    static ArrayList<ArrayList<String>> readCSV(String fullpath) throws FileNotFoundException{
        ArrayList<ArrayList<String>> records = new ArrayList<>();
	    File inputFile = new File(fullpath);
	    Scanner scl = new Scanner(inputFile);
	    while(scl.hasNextLine()){
		    ArrayList<String> newRecord = new ArrayList<>();
		    String[] tokens = scl.nextLine().split(",");
		    for(String token : tokens){
			    newRecord.add(token);
		    }
		    records.add(newRecord);
	    }
	    scl.close();		
	    return records; 
    }
    
    public static void readweka(String filename, ArrayList<ArrayList<String>> records) throws FileNotFoundException {
    	ArrayList<ArrayList<String>> result = new ArrayList<>();
    	
    	Scanner sc = new Scanner(new File(filename));
    	while(sc.hasNextLine()){
			String[] tokens = sc.nextLine().split(" -1 -2");
			//System.out.println(tokens[0]);      
			String[] itemset = tokens[0].split(" -1 ");    
			for (String s : itemset) {
			
			}
		}
    }
    
	
}
