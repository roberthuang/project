package weka;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import getAttribute.GetAttr;
import transferToSDB.T2SDB;

public class weka {
    public static void main(String[] args) {
    	try { 
    	    String path = "petro_subset1_2010_rate.csv";
    	    ArrayList<ArrayList<String>> records = readCSV(path);
    	    /**Feature Extraction**/    	     	   
    	    GetAttr.featureExtraction_weka("weka.csv", records);    	        	        	  
    	    
    	    /**Translate To SDB**/
    	    /**1.Training Data**/
    	    T2SDB t2sdb = new T2SDB();   
    	    HashMap<Integer, String> feature_target = GetAttr.featureExtraction_target(records);
    	    //t2sdb.translate_training_sliding_window_weka(10, "petro_subset1_2010_rate.csv", feature_target, "weka_training.txt");
    	    t2sdb.translate_training_sliding_window_weka(10, "weka.csv", feature_target, "weka_training.txt");
    	    /**2.Testing Data**/   
    	    //t2sdb.translate_testing_sliding_window_weka(10, "petro_subset1_2010_rate.csv", feature_target, "weka_testing.txt");
    	    t2sdb.translate_testing_sliding_window_weka(10, "weka.csv", feature_target, "weka_testing.txt");
    	    /**Text To CSV**/
    	    
   	        try {
                ArrayList<ArrayList<String>> txt_training = read_text_weka("weka_training.txt");  
                ArrayList<ArrayList<String>> txt_testing = read_text_weka("weka_testing.txt");  
                try {
    		        writeCSV("", "weka_training.csv", txt_training);
    		        writeCSV("", "weka_testining.csv", txt_testing);
    		    } catch (IOException e) {
   			        System.out.println("[ERROR] I/O Exception.");
    			    e.printStackTrace();
   		        }  
            } catch (FileNotFoundException e) {
               
            }
    	    
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
    public static ArrayList<ArrayList<String>> read_text_weka(String filename) throws FileNotFoundException {
    	ArrayList<ArrayList<String>> result = new ArrayList<>();    	
    	Scanner sc = new Scanner(new File(filename));
    	int i = 1;
    	while(sc.hasNextLine()){
		    String[] tokens = sc.nextLine().split(", ");  
		    ArrayList<String> temp = new ArrayList<>();  
		    if (i == 1) {		    
		        for (String s : tokens) {
		            temp.add(s);
		        }   
		        i--;
		        result.add(temp); 
		    } else {
		        for (String s : tokens) {
		            temp.add(s);
		        }   
		        result.add(temp); 
		    }
	    }
	    return result;			
    }
    
    static void writeCSV(String path, String filename, ArrayList<ArrayList<String>> records) throws IOException{
		FileWriter outputFW = new FileWriter(path + filename);
		for(int i=0;i<records.size();i++){
			ArrayList<String> record = records.get(i);
			StringBuilder recordSB = new StringBuilder();
			for(String col : record) recordSB.append(col).append(',');
			recordSB.deleteCharAt(recordSB.length()-1);
			outputFW.write(recordSB.toString());
			if(i < records.size()-1) outputFW.write("\r\n");
		}
		outputFW.close();
	}	
	
}
