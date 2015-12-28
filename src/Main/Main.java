package Main;
import java.io.*;
import java.util.*;

import getAttribute.GetAttr;
import ruleGeneration.RuleEvaluation;
import ruleMapping.RuleMapping;
import transferToSDB.T2SDB;
import ca.pfv.spmf.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.AlgoPrefixSpan_with_Strings;
import ca.pfv.spmf.input.sequence_database_list_strings.SequenceDatabase;
import dataPreprocessing.SAXTransformation;
import dataPreprocessing.SAXTransformation_Testing;

public class Main {
    public static void main(String[] args) throws 
    FileNotFoundException {
        try {
    	    File fout = new File("data.txt");
    	    FileOutputStream fos = new FileOutputStream(fout);
	        OutputStreamWriter osw = new OutputStreamWriter(fos);
	        
   	        //for (double j = 0.01;j <= 0.85; j = j + 0.01) {
   	        //System.out.println(j);
    		/**0.Set Argument**/
    		int window_size = 2;
    		int minsup = 28;
    		double min_conf = 0.68;
    		//Input
    		String path = "petro_subset1_2010.csv";
            ArrayList<ArrayList<String>> records = readCSV(path);
            int traing_data_size = (int)((records.size()-1)*0.8);
            
    		HashMap<Integer, String> feature_target = GetAttr.featureExtraction_target(records);
            //GetAttr.featureExtraction("transformed_petro_subset1_feature.csv", records);
            //GetAttr.featureExtraction("transformed_petro_subset1_feature.csv", records);	
    		GetAttr.featureExtraction("transformed_petro_subset1_feature.csv", records);	
    		//GetAttr.featureExtraction2("transformed_petro_subset1_feature.csv", records, feature_target, window_size);	
	        /**2.SAX**/
    	    //System.out.println("##Step 2.1: SAX(Traing)");
            //SAXTransformation.start("SAXTransformation_config_petro_subset1_2010.txt");
                       
            //System.out.println("##Step 2.2: SAX(Testing)");          
            //SAXTransformation_Testing.start("petro_subset1_breakpoints_2010.txt");
                                              
            /**3.Temporal Data Base to SDB(Training)**/
            //System.out.println("##Step 3.1: Temporal Data Base to SDB(Training)");
            //For training            
            String path_after_discrete = "transformed_petro_subset1_feature.csv";
    		T2SDB t = new T2SDB();
            t.translate_training(window_size, path_after_discrete,  feature_target, "SDB(Training).txt");
            
            //System.out.println("##Step 3.2: Temporal Data Base to SDB(Testing)");
            //For testing
            String path_of_testing_file_after_SAX = "transformed_petro_subset1_feature.csv";
            t.translate_testing(window_size, path_of_testing_file_after_SAX, "SDB(Testing).txt");
                         
            /**4.Sequential Pattern Mining**/
            //System.out.println("##Step 4: Sequential Pattern Mining");
            //Load a sequence database
            SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
            sequenceDatabase.loadFile("SDB(Training).txt");
            //print the database to console
            //sequenceDatabase.print();
    		
    		AlgoPrefixSpan_with_Strings algo = new AlgoPrefixSpan_with_Strings(); 
    		//execute the algorithm
    		algo.runAlgorithm(sequenceDatabase, "sequential_patterns.txt", minsup);    
    		//algo.printStatistics(sequenceDatabase.size());
    		
    		/**5.Generating Rule**/
    		//System.out.println("##Step 5: Rule Generating");
    		RuleEvaluation.start("RuleEvaluation_config.txt", min_conf, traing_data_size);
                		
    		/**6.Rule Mapping**/    		
    		System.out.println("##Step 6: Rule Mapping");
    	    RuleMapping mapping = new RuleMapping();
    		HashMap<Integer, ArrayList<String>> result_of_predict_for_testing_data 
    		= mapping.RuleMapping(readRules("rules.txt"), ReadSDB_for_testing("SDB(Testing).txt"), feature_target);
    	    
    		/**7.Evaluate Precision**/ 
    		HashMap<String, Double> e = mapping.evaluate(feature_target, result_of_predict_for_testing_data, traing_data_size, window_size);    		           
    		
    		osw.write("window_size:"        + window_size + "\r\n");
    		osw.write("minsup:"             + minsup + "\r\n");
    		osw.write("min_conf:"           + min_conf + "\r\n");    
    		osw.write("=== Confusion Matrix ===\r\n");
    		osw.write("          a      b\r\n");
    		osw.write("a=Rise   " + e.get("True_Positive") + "\t" + e.get("False_Negative") + "\r\n");
    		osw.write("b=Down   " + e.get("False_Positive") + "\t" + e.get("True_Negative") + "\r\n");		
            osw.write("macro_precision: " + e.get("macro_precision")+ "\r\n");
            osw.write("macro_recall: " + e.get("macro_recall")+ "\r\n");
    		osw.write("acc: "               + e.get("acc") + "\r\n");
    		osw.write("\r\n");
    		osw.write("\r\n");	  	
   	        //}
    	    osw.close();
   	        
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] File Not Found Exception.");
            e.printStackTrace();
        } catch (IOException e) {
        	//System.out.println("[ERROR] I/O Exception.");
            //e.printStackTrace();  	
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
    
    static HashMap<Integer, ArrayList<ArrayList<String>>> ReadSDB_for_testing(String filename) throws FileNotFoundException{
        HashMap<Integer, ArrayList<ArrayList<String>>> result = new HashMap<>();
        int index = 1;        
        Scanner sc = new Scanner(new File(filename));        
        while(sc.hasNextLine()) {        
            ArrayList<ArrayList<String>> itemsets = new ArrayList<>();
         
            String[] tokens = sc.nextLine().split(" -1 -2");
            String[] tokens_next = tokens[0].split(" -1 ");
            for (String s : tokens_next) {
                ArrayList<String> itemset = new ArrayList<>();
                String[] tokens_next_next = s.split(" ");
                for (String ss : tokens_next_next) {
                    itemset.add(ss);
                }
                itemsets.add(itemset);
            }
            result.put(index, itemsets);
            index = index + 1;
        }     
        
        /*
        //debug
        for (Integer i : result.keySet()) {
	        System.out.println(i + " " + result.get(i));
	    
	    }*/
        //System.out.println(result.size());
        sc.close();
        return result;
        
    }
     
    //Read rule file
    static HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> readRules(String filename) throws FileNotFoundException{
	        
		HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> result = new HashMap<>();
				
		Scanner sc = new Scanner(new File(filename));
		while(sc.hasNextLine()){
		
		    ArrayList<ArrayList<String>> itemsets = new ArrayList<>();
		    ArrayList<Double> list = new ArrayList<>();
			String[] tokens = sc.nextLine().split("\t:\t");
			//For sup, confidence
			String[] number = tokens[1].split(",\t");
			for (String s : number) {
			    double n = Double.parseDouble(s);
			    list.add(n);
			}
			
			//For items
			String[] tokens_next = tokens[0].split(" -> ");
			String[] tokens_next_next = tokens_next[0].split(" -1 ");
			
			//tokens_next[1] : Rise/Down
			ArrayList<String> itemset_next = new ArrayList<>();
			itemset_next.add(tokens_next[1]);
			
			for(String s : tokens_next_next) {
			    String[] tokens_next_next_next =  s.split(" ");
			    ArrayList<String> itemset = new ArrayList<>();   
			    for(String ss : tokens_next_next_next) {
			        itemset.add(ss);    			    
			    }
			    itemsets.add(itemset);
            }
			itemsets.add(itemset_next);		
			result.put(itemsets, list);
			
		}
		/*
		//debug
		for (ArrayList<ArrayList<String>> key : result.keySet()) {
		    System.out.println(key + " " + result.get(key));
		
		}*/
		
		sc.close();
		return result;	
    }
  
}


