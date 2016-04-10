package Main;
import java.io.*;
import java.util.*;

import getAttribute.GetAttr;
import ruleGeneration.RuleEvaluation;
import ruleMapping.RuleMapping;
import transferToSDB.T2SDB;
import ca.pfv.spmf.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.AlgoPrefixSpan_with_Strings;
import ca.pfv.spmf.input.sequence_database_list_strings.SequenceDatabase;
//import dataPreprocessing.SAXTransformation;
//import dataPreprocessing.SAXTransformation_Testing;
import dataPreprocessing.SAXTransformation;
import dataPreprocessing.SAXTransformation_Testing;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        try {        	
        	//File fout = new File("data\\" + "data" + "_s"+ args[0] + "_w" + args[1]+"_" + args[2] +"_" + args[3] + "method1.txt");        	
        	
        	File fout = new File("data\\" + "data" + "_s"+ args[0] + "_w" + args[1]+ "_p" + args[2] +"_t" + args[3]+"cbs.txt");
        	//File fout = new File("data\\" + "data" + "_s"+ args[0] + "_w" + args[1]+ "_p" + args[2] +"_t" + args[3]+".txt");
     	    FileOutputStream fos = new FileOutputStream(fout);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
	        for (double j =  0.5;j <= 1; j = j + 0.01) {
   	        System.out.println(j);
    		/**0.Set Argument**/    		
    	
    		int minsup = Integer.parseInt(args[0]);    
//    		System.out.println(minsup);
    		//double min_conf = Double.parseDouble(args[1]);
    		double min_conf = j;
    		
    		int window_size =  Integer.parseInt(args[1]);
    		int next_week = window_size;
    		
    		int periods = Integer.parseInt(args[2]);
    		double threshold = Double.parseDouble(args[3]);
    		
    		//Input
    		String path = "petro_subset1_2010_rate.csv";
            ArrayList<ArrayList<String>> records = readCSV(path);
            int traing_data_size = (int)((records.size()-1)*0.8);
            
    		HashMap<Integer, String> feature_target = GetAttr.featureExtraction_target(records);
    		
    		
    		HashMap<String, Integer> rise_down_number = new HashMap<>();    

    		HashMap<Integer, ArrayList<ArrayList<String>>> sdb_training = Read_Training_Data("SDB(Training).txt");
    		for (Integer i : sdb_training.keySet()) {
    			//System.out.println(sdb_training.get(i));
    			String target = sdb_training.get(i).get(sdb_training.get(i).size()-1).get(0);
    			if (rise_down_number.get(target) == null) {
    			    rise_down_number.put(target, 1);	
    			} else {
    				int number = rise_down_number.get(target);
    				number++;
    				rise_down_number.put(target, number);
    			}
    		}
    		 
    		 
    		 
    		 
    		 
    		GetAttr.featureExtraction("transformed_petro_subset1_feature.csv", records, periods, threshold);	
    		//GetAttr.featureExtraction_episode("transformed_petro_subset1_feature.csv", records, feature_target);
    		//GetAttr.featureExtraction_weka("weka.csv", records);	
	        /**2.SAX**/
//    	    System.out.println("##Step 2.1: SAX(Training)");
//            SAXTransformation.start("SAXTransformation_config_petro_subset1_2010.txt");
                       
            //System.out.println("##Step 2.2: SAX(Testing)");          
//           SAXTransformation_Testing.start("petro_subset1_breakpoints_2010.txt");
                                              
            /**3.Temporal Data Base to SDB(Training)**/
//            System.out.println("##Step 3.1: Temporal Data Base to SDB(Training)");
            /*For training*/            
            String path_after_discrete = "transformed_petro_subset1_feature.csv";
   		    T2SDB t = new T2SDB();
    		int SDB_Training_Size = t.translate_training_sliding_window(window_size, path_after_discrete,  feature_target, "SDB(Training).txt");
            System.out.println("SDB_Training_Size:" + SDB_Training_Size);
    		
            //System.out.println("##Step 3.2: Temporal Data Base to SDB(Testing)");    		
            /*For testing*/
            String path_of_testing_file = "transformed_petro_subset1_feature.csv";
            int SDB_Testing_Size = t.translate_testing_sliding_window(window_size, path_of_testing_file, "SDB(Testing).txt");
            System.out.println("SDB_Testing_Size: " + SDB_Testing_Size);             
            /**4.Sequential Pattern Mining**/
            //System.out.println("##Step 4: Sequential Pattern Mining");
            /*Load a sequence database*/
            SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
            sequenceDatabase.loadFile("SDB(Training).txt");
            //print the database to console
            //sequenceDatabase.print();
    		
    		AlgoPrefixSpan_with_Strings algo = new AlgoPrefixSpan_with_Strings(); 
    		/*execute the algorithm*/
    		algo.runAlgorithm(sequenceDatabase, "sequential_patterns.txt", minsup);    
    		algo.runAlgorithm(sequenceDatabase, "sequential_patterns_all.txt", 2);    
    		//algo.printStatistics(sequenceDatabase.size());
    		
    		/**5.Generating Rule**/
    		//System.out.println("##Step 5: Rule Generating");
    		int rule_size = RuleEvaluation.start("RuleEvaluation_config.txt", min_conf, minsup, window_size, SDB_Training_Size);
    		RuleEvaluation.start("RuleEvaluation_config_all.txt", 0.01, 2, window_size, SDB_Training_Size);
    		//System.out.println(rule_size);
    		
    		/**6.Rule Mapping**/    		
    		//System.out.println("##Step 6: Rule Mapping");
    	    RuleMapping mapping = new RuleMapping();
  	        HashMap<Integer, ArrayList<String>> result_of_predict_for_testing_data 
	        = mapping.RuleMapping(readRules("rules.txt"), ReadSDB_for_testing("SDB(Testing).txt"), Read_Training_Data("SDB(Training).txt"),feature_target, readRules("rules_all.txt"), minsup, window_size, min_conf, rise_down_number,  SDB_Training_Size);
  	       
    		/**7.Evaluate Precision**/     		
    	    HashMap<String, Double> e = mapping.evaluate(feature_target, result_of_predict_for_testing_data, traing_data_size, next_week, records.size(),  min_conf, minsup);    		           
    		//if (e.get("macro_f_measure") > 0.7) {
    	    
    		osw.write("window_size:"        + window_size + "\r\n");
    		osw.write("minsup:"             + minsup + "\r\n");
    		osw.write("min_conf:"           + min_conf + "\r\n");  
    		osw.write("rule_size:"           + rule_size + "\r\n");  
    		osw.write("\r\n"); 
    		osw.write("\r\n");  
    		osw.write("=== Confusion Matrix ===\r\n");
    		osw.write("          a      b\r\n");
    		osw.write("a=Rise   " + e.get("True_Positive") + "\t" + e.get("False_Negative") + "\r\n");
   		    osw.write("b=Down   " + e.get("False_Positive") + "\t" + e.get("True_Negative") + "\r\n");		
    		osw.write("precision_rise: " + e.get("precision_rise")+ "\r\n");
    		osw.write("recall_rise: " + e.get("recall_rise")+ "\r\n");
    		osw.write("precision_down: " + e.get("precision_down")+ "\r\n");
   		    osw.write("recall_down: " + e.get("recall_down")+ "\r\n");
            osw.write("macro_precision: " + e.get("macro_precision")+ "\r\n");
            osw.write("macro_recall: " + e.get("macro_recall")+ "\r\n");
            osw.write("macro_f_measure: " + e.get("macro_f_measure")+ "\r\n");
    		osw.write("acc: "               + e.get("acc") + "\r\n");
    		osw.write("\r\n");
    		osw.write("\r\n");
	        //}

    		} 
    		
	        
    	    osw.close();
    	    
    	    
    	    
    	    
   	        
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] File Not Found Exception.");
            e.printStackTrace();
        } catch (IOException e) {
        	System.out.println("[ERROR] I/O Exception.");
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
    
    static HashMap<Integer, ArrayList<ArrayList<String>>> Read_Training_Data(String filename) throws FileNotFoundException{
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
    static HashMap<String, Integer> get_rise_down_number(HashMap<Integer, String> feature_target, int window_size){
    	int training_data_size = (int) (feature_target.size()*0.8);
    	HashMap<String, Integer> result = new HashMap<>();
    	for (Integer i : feature_target.keySet()) {
    	    if (i <= (training_data_size-window_size)) {
    	    	if (result.get(feature_target.get(i)) == null) {
    	    		result.put(feature_target.get(i), 1);
    	    	} else {
    	    		int count = result.get(feature_target.get(i));
    	    		count++;
    	    		result.put(feature_target.get(i), count);
    	    	}
    	    }
    	}
    	
//    	System.out.println(result.get("Rise"));
//    	System.out.println(result.get("Down"));
    	return result;
    }
    
}


