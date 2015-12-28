package ruleMapping;

import java.io.*;
import java.util.*;

public class RuleMapping {
	
	public static double Cacluate_all_entropy(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules) {
	    double globalEntropy = 0; 	
		int rise_number = 0;
		int down_number = 0;
		for (ArrayList<ArrayList<String>> rule : rules.keySet()) {
		    String str = rule.get(rule.size()-1).get(0);	
			if (str.equals("Rise")) {
				rise_number++;
			} else {
				down_number++;
			}
		}

		int total = 0;
        total = rise_number + down_number;
		double frequencyDouble1 = rise_number / (double) total;
		double frequencyDouble2 = down_number / (double) total;
		
		if (frequencyDouble2 != 0) {
			globalEntropy -= (frequencyDouble1 * Math.log(frequencyDouble1) / Math.log(2))+(frequencyDouble2 * Math.log(frequencyDouble2) / Math.log(2));				
		} else {
			globalEntropy = 0;		
		}	

		return globalEntropy;
		
	}
	
	public static ArrayList<String> getinstance(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules, ArrayList<ArrayList<ArrayList<String>>> match_rules, ArrayList<String> default_class) {
		
		double globalEntropy = Cacluate_all_entropy(rules);
		double score_1 = 0;
		double score_2 = 0;
		
		ArrayList<ArrayList<ArrayList<String>>> class1_set = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<String>>> class2_set = new ArrayList<>();
		
		for (ArrayList<ArrayList<String>> match_rule : match_rules) {
		    String str = match_rule.get(match_rule.size()-1).get(0);
		    if (str.equals("Rise")) {
		    	class1_set.add(match_rule);	
		    } else {
		    	class2_set.add(match_rule);
		    }
			
		}
		
		if (class1_set.isEmpty()) {
			System.out.println("Empty: Rise");
			ArrayList<String> temp = new ArrayList<>();
			temp.add("Down");
			return temp;
		} else if (class2_set.isEmpty()) {
			System.out.println("Empty: Down");
			ArrayList<String> temp = new ArrayList<>();
			temp.add("Rise");
			return temp;
		} else {
			System.out.println("Mapping");
		
		for (ArrayList<ArrayList<String>> class1_member : class1_set) {
			int match_number = 0;
			double Entropy = 0;
			int match_c1_number = 0;
			int match_c2_number = 0;
			int none_match_c1_number = 0;
			int none_match_c2_number = 0;
		    for (ArrayList<ArrayList<String>> rule : rules.keySet()) {
		        int size = 0;
		        int current = 0;
		        for (int i_1 = 0; i_1 <  class1_member.size(); i_1++) {                	
                    for (int j = current; j < rule.size()-1; j++) {                                         
                        if (rule.get(j).containsAll(class1_member.get(i_1))) {    
                            current = j;
                            current = current + 1;
                            size = size + 1;
                            break;
                        }                    	
                    }   
                                       
                }       
		        if (size == rule.size()-1) {
                	match_number = match_number + 1;      
                	if (rule.get(rule.size()-1).get(0).equals("Rise")) {
                	    match_c1_number += 1;	
                	} else {
                		match_c2_number += 1;
                	}
                } else {
                	if (rule.get(rule.size()-1).get(0).equals("Rise")) {
                		none_match_c1_number += 1;
                	} else {
                		none_match_c2_number += 1;
                	}
                }
		    }
		    
		    int total = rules.keySet().size();
		    double left_ratio = match_number / (double) total;
		    //System.out.println("Rise: " + left_ratio + "  match_number: " + match_number + " total: " + total);   
		    double l_l_ratio = match_c1_number / (double) match_number;
		    double l_r_ratio = match_c2_number / (double) match_number;
		    
		    int other = rules.keySet().size() - match_number;
		    
		    double right_ratio = other / (double) rules.keySet().size();
		    double r_l_ratio = none_match_c1_number / (double) other;
		    double r_r_ratio = none_match_c2_number / (double) other;
		    
		    double left_entropy = left_ratio*(-(l_l_ratio*(Math.log(l_l_ratio)/ Math.log(2))) - (l_r_ratio*(Math.log(l_r_ratio)/ Math.log(2))));
		    double right_entropy = right_ratio*(-(r_l_ratio*(Math.log(r_l_ratio)/ Math.log(2))) - (r_r_ratio*(Math.log(r_r_ratio)/ Math.log(2))));
		    Entropy = left_entropy + right_entropy;
		    System.out.println(Entropy);
		    double Gain = globalEntropy - Entropy;
		    score_1 += Gain;			
		}
		
		for (ArrayList<ArrayList<String>> class2_member : class2_set) {
			int match_number = 0;
			double Entropy = 0;
			int match_c1_number = 0;
			int match_c2_number = 0;
			int none_match_c1_number = 0;
			int none_match_c2_number = 0;
		    for (ArrayList<ArrayList<String>> rule : rules.keySet()) {
		        int size = 0;
		        int current = 0;
		        for (int i_1 = 0; i_1 <  class2_member.size(); i_1++) {                	
                    for (int j = current; j < rule.size()-1; j++) {                                         
                        if (rule.get(j).containsAll(class2_member.get(i_1))) {    
                            current = j;
                            current = current + 1;
                            size = size + 1;
                            break;
                        }                    	
                    }   
                                       
                }       
		        if (size == rule.size()-1) {
                	match_number = match_number + 1;      
                	if (rule.get(rule.size()-1).get(0).equals("Rise")) {
                	    match_c1_number += 1;	
                	} else {
                		match_c2_number += 1;
                	}
                } else {
                	if (rule.get(rule.size()-1).get(0).equals("Rise")) {
                		none_match_c1_number += 1;
                	} else {
                		none_match_c2_number += 1;
                	}
                }
		    }
		   
		    int total = rules.keySet().size();
		    double left_ratio = match_number / (double) total;
		    //System.out.println("Down: " + left_ratio + "  match_number: " + match_number + " total: " + total);
		    
		    double l_l_ratio = match_c1_number / (double) match_number;
		    double l_r_ratio = match_c2_number / (double) match_number;
		    
		    int other = rules.keySet().size() - match_number;
		    
		    double right_ratio = other / (double) rules.keySet().size();
		    double r_l_ratio = none_match_c1_number / (double) other;
		    double r_r_ratio = none_match_c2_number / (double) other;
		    
		    double left_entropy = left_ratio*(-(l_l_ratio*(Math.log(l_l_ratio)/ Math.log(2))) - (l_r_ratio*(Math.log(l_r_ratio)/ Math.log(2))));
		    double right_entropy = right_ratio*(-(r_l_ratio*(Math.log(r_l_ratio)/ Math.log(2))) - (r_r_ratio*(Math.log(r_r_ratio)/ Math.log(2))));
		    Entropy = left_entropy + right_entropy;
		    System.out.println(Entropy);
		    double Gain = globalEntropy - Entropy;
		    score_2 += Gain;
			
		}
	    
		if (score_1 > score_2) {
			ArrayList<String> temp = new ArrayList<>();
			temp.add("Rise");
			return temp;
		} else if (score_1 == score_2) {
		    return default_class;
		} else {
			ArrayList<String> temp = new ArrayList<>();
			temp.add("Down");
			return temp;
		}
		
		}
	}
	
    public HashMap<Integer, ArrayList<String>>  RuleMapping(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules,
    HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_testing, HashMap<Integer, String> target_class) {
 
    	HashMap<String, Integer> number_of_rise_down = new HashMap<>();
    	for (int i = 1; i <= target_class.size()*0.8; i++) {
    	    if (number_of_rise_down .get(target_class.get(i)) == null) {
    	    	number_of_rise_down.put(target_class.get(i), 1);        	
    	    } else {
    	    	int number = number_of_rise_down.get(target_class.get(i));
    	    	number ++;
    	    	number_of_rise_down.put(target_class.get(i), number);
    	    }
    		
    	}
        
    	ArrayList<String> answer = new ArrayList<>();
    	if (number_of_rise_down.get("Rise") == null) {
        	answer.add("Down");        	
        } else if (number_of_rise_down.get("Down") == null) {
        	answer.add("Rise"); 
        } else {
        	int Rise_number =  number_of_rise_down.get("Rise");
            int Down_number =  number_of_rise_down.get("Down");
            if (Rise_number > Down_number) {
                answer.add("Rise");        	
            } else {
            	answer.add("Down");   
            }	
        }
                                                 	 
    	//2.Begin Mapping 
        HashMap<Integer, ArrayList<String>> result = new HashMap<>();      
        for (Integer i : SDB_for_testing.keySet()) {
            //Match rule's number
            int match_number = 0;
            ArrayList<ArrayList<ArrayList<String>>> match_rules = new  ArrayList<>();            
            //The sequence in SDB_Testing
            ArrayList<ArrayList<String>> itemsets = SDB_for_testing.get(i);                            
            for (ArrayList<ArrayList<String>> rule : rules.keySet()) {
                //The size of mapping items in rule                
                int size = 0;
                int current = 0;
                for (int i_1 = 0; i_1 <  itemsets.size(); i_1++) {                	
                    for (int j = current; j < rule.size()-1; j++) {                                         
                        if (itemsets.get(i_1).containsAll(rule.get(j))) {    
                            current = j;
                            current = current + 1;
                            size = size + 1;
                            break;
                        }                    	
                    }                                                            
                    
                }                
                if (size == rule.size()-1) {
                	match_number = match_number + 1;      
                	match_rules.add(rule);
                }
               
            } 
            //System.out.println(match_number);            
            if (match_number >=2) {
            	//CBA
            	/*
                int max = 0;
                double max_sup = rules.get(match_rules.get(0)).get(0);
                double max_confidence = rules.get(match_rules.get(0)).get(1);              
                for (int j = 1; j < match_rules.size(); j++) {
                	double confidence = rules.get(match_rules.get(j)).get(1);
                	double sup = rules.get(match_rules.get(j)).get(0);
                    if (confidence > max_confidence) {
                        max = j;
                    } else if (confidence == max_confidence) {
                    
                    	if (sup > max_sup) {
                    		max = j;  
                   
                    	}
                    }
                	
                }            	
                ArrayList<ArrayList<String>> match_rule = match_rules.get(max);
                ArrayList<String> Rise_Down = match_rule.get(match_rule.size()-1);                
            	result.put(i, Rise_Down);*/
            	//CBS
            	
            	result.put(i, getinstance(rules, match_rules, answer));
            	            
            } else if (0< match_number && match_number < 2){
            	//Only one match_rule
            	ArrayList<ArrayList<String>> match_rule = match_rules.get(0);
            	ArrayList<String> Rise_Down = match_rule.get(match_rule.size()-1);
            	result.put(i,Rise_Down);
            	
            } else {
            	result.put(i,answer);            	         	           
            }
        }       
        return result;         	 	
    }
           
    public HashMap<String, Double> evaluate(HashMap<Integer, String> class_table , HashMap<Integer, ArrayList<String>> predict, int traing_data_size, int window_size) {
    	HashMap<String, Double> e = new HashMap<>();
    	HashMap<String, Integer> number = new HashMap<>();
    	for (int i = 1; i <= predict.size(); i++) {
    	    if (number.get(predict.get(i).get(0)) == null) {
    	        number.put(predict.get(i).get(0), 1);
    	    } else {
    	    	int temp = number.get(predict.get(i).get(0));
    	    	temp = temp + 1;
    	    	number.put(predict.get(i).get(0), temp);
    	    }	
    	}
    	
    	if (number.get("Rise") == null) {
    		e.put("Rise", null);	
    	} else {
    	    e.put("Rise", (double)number.get("Rise"));
    	}
    	if (number.get("Down") == null) {
    		e.put("Down", null);
    	} else {
    		e.put("Down", (double)number.get("Down"));
    	}
    	
        int True_Positive  = 0;
        int True_Negative  = 0;
        int False_Positive = 0;
        int False_Negative = 0;     
        int index = traing_data_size;   
        for (int i = 1; i <= predict.size(); i++) {
                if (predict.get(i).get(0).equals("Rise"))	{
                    if (class_table.get(i+index+window_size).equals("Rise")) {
                    	True_Positive += 1;	
                    } else {
                    	False_Negative += 1;
                    }
                	
                } else  {
                	if (class_table.get(i+index).equals("Down")) {
                		True_Negative += 1;	
                    } else {
                    	False_Positive += 1;
                    }              	
                }
        }
        int size = True_Negative +  True_Positive + False_Positive + False_Negative;
        e.put("True_Positive", (double)True_Positive);
        e.put("True_Negative", (double)True_Negative);
        e.put("False_Positive", (double)False_Positive);
        e.put("False_Negative", (double)False_Negative);
        
        double acc =  (True_Positive + True_Negative)/ (double)(size);
        e.put("acc", acc);
        double precision_rise =  True_Positive / (double)(True_Positive + False_Positive);
        e.put("precision_rise", precision_rise);
        double recall_rise =  True_Positive / (double)(True_Positive + False_Negative);
        e.put("recall_rise", recall_rise);
        double precision_down =  True_Negative / (double)(True_Negative +  False_Negative);
        e.put("precision_down", precision_down);
        double recall_down =  True_Negative / (double)( True_Negative + False_Positive);
        e.put("recall_down", recall_down );
        
        double macro_precision = ( precision_rise + precision_down) / (double) 2;
        e.put("macro_precision", macro_precision);
        double macro_recall = ( recall_rise + recall_down) / (double) 2;
        e.put("macro_recall", macro_recall);
        
        return e;
        }    
}
