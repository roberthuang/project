package ruleMapping;

import java.io.*;
import java.util.*;

public class RuleMapping {
	
	/**CBE_CBS**/
	public static double Cacluate_all_entropy(HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_training) {
	    double globalEntropy = 0; 	
		int rise_number = 0;
		int down_number = 0;
		for (Integer i :  SDB_for_training.keySet()) {
			ArrayList<ArrayList<String>> rule = SDB_for_training.get(i);
		    String str = rule.get(rule.size()-1).get(0);	
//		    System.out.println(str);
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
			globalEntropy -= frequencyDouble1 * Math.log(frequencyDouble1) / Math.log(2);		
		}	
		return globalEntropy;		
	}
	
	/**CBE_CBS**/
	public static ArrayList<String> getinstance(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules, ArrayList<ArrayList<ArrayList<String>>> match_rules, HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_training) {
		
	
		double globalEntropy = Cacluate_all_entropy(SDB_for_training);
		double score_1 = 0;
		double score_2 = 0;
		
		ArrayList<ArrayList<ArrayList<String>>> class1_set = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<String>>> class2_set = new ArrayList<>();
		
		ArrayList<ArrayList<ArrayList<String>>> match_rules_2 = new ArrayList<>();
		List<ArrayList<ArrayList<String>>> list = new ArrayList<>();
		
        for (ArrayList<ArrayList<String>> match_rule : match_rules) {
            list.add(match_rule);
        }
        
        for (int i = 0 ; i < list.size(); i++) {
    	    boolean same = false;
    	    for (int j = i+1; j < list.size(); j++) {
    	        ArrayList<ArrayList<String>> temp1 = new ArrayList<>();
    		for (int k1 = 0; k1 < list.get(i).size()-1; k1++) {
    		    temp1.add(list.get(i).get(k1));
    	    }    
    	    String str1 = list.get(i).get(list.get(i).size()-1).get(0);
    	    ArrayList<ArrayList<String>> temp2 = new ArrayList<>();
    		for (int k1 = 0; k1 < list.get(j).size()-1; k1++) {
    		    temp2.add(list.get(j).get(k1));
    	        }
    	        String str2 = list.get(j).get(list.get(j).size()-1).get(0);
    	        if ( (temp1.equals(temp2)) && (!str1.equals(str2)) ) {
    	            //System.out.println(temp1 + " " + temp2);
    		    same = true;
    		    list.remove(j--);		   
    	        } 
    	    }    
    	    if (same) {
    	        //System.out.println(i);
    	        list.remove(i--);	       
    	    }
    	}
		
        for (ArrayList<ArrayList<String>> match_rule : list) {
            match_rules_2.add(match_rule);

        }
		
		for (ArrayList<ArrayList<String>> match_rule : match_rules_2) {
		    String str = match_rule.get(match_rule.size()-1).get(0);
		    if (str.equals("Rise")) {
		    	class1_set.add(match_rule);	
		    } else {
		    	class2_set.add(match_rule);
		    }			
		}
		
//		for (ArrayList<ArrayList<String>> class_member:class1_set) {
//			System.out.println(class_member);
//		}
//		for (ArrayList<ArrayList<String>> class_member:class2_set) {
//			System.out.println(class_member);
//		}
		
		if (class1_set.isEmpty()) {
			//System.out.println("Empty: Rise");
			ArrayList<String> temp = new ArrayList<>();
			temp.add("Down");
			return temp;
		} else if (class2_set.isEmpty()) {
			//System.out.println("Empty: Down");
			ArrayList<String> temp = new ArrayList<>();
			temp.add("Rise");
			return temp;
		} else {
			//System.out.println("Mapping:");
		
		for (ArrayList<ArrayList<String>> class1_member : class1_set) {
//			System.out.println(class1_member);
			int match_number = 0;
			double Entropy = 0;
			double SplitInfo = 0;
			double gainratio = 0;
			int match_c1_number = 0;
			int match_c2_number = 0;
			/*int none_match_c1_number = 0;
			int none_match_c2_number = 0;*/
		    for (Integer i : SDB_for_training.keySet()) {
		    	ArrayList<ArrayList<String>> rule = SDB_for_training.get(i);
		        int size = 0;
		        int current = 0;
		 
		        for (int i_1 = 0; i_1 <  class1_member.size()-1; i_1++) {                	
                    for (int j = current; j < rule.size()-1; j++) {                                         
                        if (rule.get(j).containsAll(class1_member.get(i_1))) {    
                            current = j;
                            current++;
                            size++;
                        }  
                        break;
                    }   
                                       
                }       
		        if (size == class1_member.size()-1) {
                	match_number++;      
                	//System.out.println(rule.get(rule.size()-1).get(0));
                	if (rule.get(rule.size()-1).get(0).equals("Rise")) {
                	    match_c1_number++;	
                	} else if (rule.get(rule.size()-1).get(0).equals("Down")){
                		match_c2_number++;
                	}
                }
		    }
		    
		    int total = SDB_for_training.keySet().size();
		    double left_ratio = match_number / (double) total;
		    //System.out.println("Rise: " + left_ratio + "  match_number: " + match_number + " total: " + total);   
		    double l_l_ratio = match_c1_number / (double) match_number;
		    
		    double l_r_ratio = match_c2_number / (double) match_number;
		    //System.out.println(l_l_ratio + " " + l_r_ratio);
		    int other = SDB_for_training.keySet().size() - match_number;
		    
		    double right_ratio = other / (double) total;
		    //double r_l_ratio = none_match_c1_number / (double) other;
		    //double r_r_ratio = none_match_c2_number / (double) other;
		    if (match_c2_number  == 0) {
		    	Entropy -= (l_l_ratio*Math.log(l_l_ratio)/ Math.log(2));
		    } else if (match_c1_number == 0) {
		    	Entropy -= (l_r_ratio*Math.log(l_r_ratio)/ Math.log(2));
		    } else {
		    	Entropy -= (l_l_ratio*Math.log(l_l_ratio)/ Math.log(2)) + (l_r_ratio*Math.log(l_r_ratio)/ Math.log(2));
		    }	 	    
		    /*double right_entropy = right_ratio*(-(r_l_ratio*(Math.log(r_l_ratio)/ Math.log(2))) - (r_r_ratio*(Math.log(r_r_ratio)/ Math.log(2))));*/		 		    
		    SplitInfo -= (left_ratio * Math.log(left_ratio) / Math.log(2))+ ( right_ratio * Math.log( right_ratio) / Math.log(2));				  
		    double gain = globalEntropy - (left_ratio*Entropy);
		    double confidence = rules.get(class1_member).get(1);
		    double length = class1_member.size();
		    gainratio = Math.abs(gain/SplitInfo);
		    score_1 += confidence*gainratio*length;			
		}
		
		for (ArrayList<ArrayList<String>> class2_member : class2_set) {
			int match_number = 0;
			double Entropy = 0;
			double SplitInfo = 0;
			double gainratio = 0;
			int match_c1_number = 0;
			int match_c2_number = 0;

			/*int none_match_c1_number = 0;
			int none_match_c2_number = 0;*/
			for (Integer i : SDB_for_training.keySet()) {
				ArrayList<ArrayList<String>> rule = SDB_for_training.get(i);
		        int size = 0;
		        int current = 0;

		        for (int i_1 = 0; i_1 <  class2_member.size()-1; i_1++) {                	
                    for (int j = current; j < rule.size()-1; j++) {                                         
                        if (rule.get(j).containsAll(class2_member.get(i_1))) {    
                            current = j;
                            current++;
                            size++;
                          
                        }       
                        break;
                    }   
                                       
                }       
		        if (size == class2_member.size()-1) {
                	match_number++;      
                	//System.out.println(rule.get(rule.size()-1).get(0));
                	if (rule.get(rule.size()-1).get(0).equals("Rise")) {
                	    match_c1_number++;	
                	} else if (rule.get(rule.size()-1).get(0).equals("Down")){
                		match_c2_number++;
                	}
                }/* else {
                	if (rule.get(rule.size()-1).get(0).equals("Rise")) {
                		none_match_c1_number++;
                	} else if (rule.get(rule.size()-1).get(0).equals("Down")) {
                		none_match_c2_number++;
                	}
                }*/
		    }
		    
		    int total = SDB_for_training.keySet().size();
		    double left_ratio = match_number / (double) total;
		    //System.out.println("Rise: " + left_ratio + "  match_number: " + match_number + " total: " + total);   
		    double l_l_ratio = match_c1_number / (double) match_number;		    
		    double l_r_ratio = match_c2_number / (double) match_number;
		    //System.out.println(l_l_ratio + " " + l_r_ratio);
		    int other = SDB_for_training.keySet().size() - match_number;
		    
		    double right_ratio = other / (double) total;
		    //double r_l_ratio = none_match_c1_number / (double) other;
		    //double r_r_ratio = none_match_c2_number / (double) other;
		    if (match_c2_number  == 0) {
		    	Entropy -= (l_l_ratio*Math.log(l_l_ratio)/ Math.log(2));
		    } else if (match_c1_number == 0) {
		    	Entropy -= (l_r_ratio*Math.log(l_r_ratio)/ Math.log(2));
		    } else {
		    	Entropy -= (l_l_ratio*Math.log(l_l_ratio)/ Math.log(2)) + (l_r_ratio*Math.log(l_r_ratio)/ Math.log(2));
		    }	 	   
		  
		    /*double right_entropy = right_ratio*(-(r_l_ratio*(Math.log(r_l_ratio)/ Math.log(2))) - (r_r_ratio*(Math.log(r_r_ratio)/ Math.log(2))));*/
		    //System.out.println(left_ratio + "  " + l_l_ratio + " " + l_r_ratio);
		    
		    SplitInfo -= (left_ratio * Math.log(left_ratio) / Math.log(2))+ ( right_ratio * Math.log( right_ratio) / Math.log(2));
		    //System.out.println(Entropy);
		    //System.out.println("Class2: " + Entropy);
		    double gain = globalEntropy - (left_ratio*Entropy);
		    double confidence = rules.get(class2_member).get(1);
		    double length = class2_member.size();
		    gainratio = Math.abs(gain/SplitInfo);
		    score_2 += confidence*gainratio*length;			
		}
		
		double L_score_1 = score_1/(double)class1_set.size();
		double L_score_2 = score_2/(double)class2_set.size();
		System.out.println("Rise: " + L_score_1 + " " +"Down: "+  L_score_2);
		if (L_score_1  > L_score_2 ){
			ArrayList<String> temp  = new ArrayList<>();
			temp.add("Rise");
			return temp;
		} else if (L_score_1  == L_score_2 ) {
			ArrayList<String> temp = new ArrayList<>();					
		    if (class1_set.size() > class2_set.size()) {
		        temp.add("Rise");
		        return temp;
		    } else {
		    	temp.add("Down");
		        return temp;
		    }
		} else {
			ArrayList<String> temp = new ArrayList<>();
			temp.add("Down");
			return temp;
		}
		
		}
	}
	
	/**CBE_METHOD1**/
	public  ArrayList<String> MTHODE1 (HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules, HashMap<ArrayList<ArrayList<String>>, ArrayList<Integer>> MATCH_RULES, int i) {
		ArrayList<ArrayList<ArrayList<String>>> match_rules_multi_length = new ArrayList<>();
		for (ArrayList<ArrayList<String>> rule : MATCH_RULES.keySet()) {
			if (rule.size() <= 10) {
				match_rules_multi_length.add(rule);		
			}
		}
		ArrayList<ArrayList<ArrayList<String>>> match_rules = new ArrayList<>();
		for (ArrayList<ArrayList<String>> rule : match_rules_multi_length) {
			
				match_rules.add(rule);		
			
		}
		
		int max = 0;
        double max_sup = rules.get(match_rules.get(0)).get(0);
        double max_confidence = rules.get(match_rules.get(0)).get(1);              
        for (int j = 1; j < match_rules.size(); j++) {
            double confidence = rules.get(match_rules.get(j)).get(1);
            double sup = rules.get(match_rules.get(j)).get(0);
            if (confidence > max_confidence) {
                max = j;
                max_confidence = confidence;
                max_sup = sup;		
            } else if (confidence == max_confidence) {
                if (sup > max_sup) {
             	    max = j;  
             		max_confidence = confidence;
                    max_sup = sup;		
             	} else if (sup == max_sup) {
//             	    System.out.println("Sup same!");
             	    int length_j = match_rules.get(j).size()-1;
             	    int length_max = match_rules.get(max).size()-1;
             	    if (length_j > length_max) {
             	        max = j;
             	        max_sup = sup;
             	        max_confidence = confidence;                  
             	    } else {
             	        continue;
             	    }	    
                }
            }
         	
        }            	
        ArrayList<ArrayList<String>> match_rule = match_rules.get(max);
        ArrayList<String> Rise_Down = match_rule.get(match_rule.size()-1);
        System.out.println(i + "_m1" + match_rule + " " + MATCH_RULES.get(match_rule));
		return Rise_Down;
	}
	
	/**CBE_METHOD2**/
	public  ArrayList<String> MTHODE2 (HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules, HashMap<ArrayList<ArrayList<String>>, ArrayList<Integer>> MATCH_RULES, int i) {
		
		ArrayList<Integer> comp3 = new ArrayList<>();
	
		
	
		comp3.add(8);
		comp3.add(9);
	
//		for (ArrayList<ArrayList<String>> rule : MATCH_RULES.keySet()) {
//		    System.out.println(i + ": " + MATCH_RULES.get(rule));	
//		}
		HashMap<ArrayList<ArrayList<String>>, ArrayList<Integer>> NEW_MATCH_RULES = new HashMap<>();
		for (ArrayList<ArrayList<String>> rule : MATCH_RULES.keySet()) {
		    if (MATCH_RULES.get(rule).containsAll(comp3)) {
		    	NEW_MATCH_RULES.put(rule, MATCH_RULES.get(rule));    	
		    }
		}
//		for (ArrayList<ArrayList<String>> rule : new_match_rules.keySet()) {
//			System.out.println(rule);
//		}
		
		ArrayList<ArrayList<ArrayList<String>>> match_rules_multi_length = new ArrayList<>();
		for (ArrayList<ArrayList<String>> rule : NEW_MATCH_RULES.keySet()) {
			if (rule.size() <= 10) {
				match_rules_multi_length.add(rule);		
			}
		}
		
		ArrayList<ArrayList<ArrayList<String>>> match_rules = new ArrayList<>();
		for (ArrayList<ArrayList<String>> rule : match_rules_multi_length) {
				match_rules.add(rule);		
		}
		
		if (match_rules.isEmpty()) {	
			System.out.println("No!" + i);
			return MTHODE1(rules, MATCH_RULES, i);
		}
		int max = 0;
        double max_sup = rules.get(match_rules.get(0)).get(0);
        double max_confidence = rules.get(match_rules.get(0)).get(1);              
        for (int j = 1; j < match_rules.size(); j++) {
            double confidence = rules.get(match_rules.get(j)).get(1);
            double sup = rules.get(match_rules.get(j)).get(0);
            if (confidence > max_confidence) {
                max = j;
                max_confidence = confidence;
                max_sup = sup;		
            } else if (confidence == max_confidence) {
                if (sup > max_sup) {
             	    max = j;  
             		max_confidence = confidence;
                    max_sup = sup;		
             	} else if (sup == max_sup) {
//             	    System.out.println("Sup same!");
             	    int length_j = match_rules.get(j).size()-1;
             	    int length_max = match_rules.get(max).size()-1;
             	    if (length_j > length_max) {
             	        max = j;
             	        max_sup = sup;
             	        max_confidence = confidence;                  
             	    } else {
             	        continue;
             	    }	    
                }
            }
         	
        }            	
        ArrayList<ArrayList<String>> match_rule = match_rules.get(max);
        ArrayList<String> Rise_Down = match_rule.get(match_rule.size()-1);
        System.out.println(i + "_m2" + match_rule + " " + MATCH_RULES.get(match_rule));
		return Rise_Down;
	}
	
	/**CBE_METHOD3**/
	public  ArrayList<String> MTHODE3 (HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules, ArrayList<ArrayList<ArrayList<String>>> match_rules, int i) {
		int max = 0;
        double max_sup = rules.get(match_rules.get(0)).get(0);
        double max_confidence = rules.get(match_rules.get(0)).get(1);              
        for (int j = 1; j < match_rules.size(); j++) {
            double confidence = rules.get(match_rules.get(j)).get(1);
            double sup = rules.get(match_rules.get(j)).get(0);
            if (confidence > max_confidence) {
                max = j;
                max_confidence = confidence;
                max_sup = sup;		
            } else if (confidence == max_confidence) {
                if (sup > max_sup) {
             	    max = j;  
             		max_confidence = confidence;
                    max_sup = sup;		
             	} else if (sup == max_sup) {
//             	    System.out.println("Sup same!");
             	    int length_j = match_rules.get(j).size()-1;
             	    int length_max = match_rules.get(max).size()-1;
             	    if (length_j > length_max) {
             	        max = j;
             	        max_sup = sup;
             	        max_confidence = confidence;                  
             	    } else {
             	        continue;
             	    }	    
                }
            }
         	
        }            	
        ArrayList<ArrayList<String>> match_rule = match_rules.get(max);
        ArrayList<String> Rise_Down = match_rule.get(match_rule.size()-1);
		return Rise_Down;	

	}
	
	
	public HashMap<ArrayList<ArrayList<String>>, ArrayList<Integer>> matching (HashMap<ArrayList<ArrayList<String>>, ArrayList<Integer>> MATCH_RULES, ArrayList<ArrayList<String>> itemsets, ArrayList<ArrayList<String>> rule, ArrayList<Integer> periods, int size, int current, int match_number) {
		for (int i_1 = 0; i_1 <  itemsets.size(); i_1++) {                	
            for (int j = current; j < rule.size()-1; j++) {                                         
                if (itemsets.get(i_1).containsAll(rule.get(j))) {    
                    current = j;
                    periods.add(i_1);
                    current++;
                    size++;
                    
                }   
                break;
            }                                                            
        }                
        if (size == rule.size()-1) {
        	match_number++;      
        	MATCH_RULES.put(rule, periods);
//        	System.out.println(i+" "+rule);
        } 
        return MATCH_RULES;
	}
	
	boolean contain_last (HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_training,  ArrayList<ArrayList<String>> match_rule, int min_sup) {
		boolean f = false;
		ArrayList<Integer> comp2 = new ArrayList<>();		
		comp2.add(8);
		comp2.add(9);
		int sup = 0;
		int match_number = 0;
		for (Integer i : SDB_for_training.keySet()) {
			ArrayList<ArrayList<String>> itemsets = SDB_for_training.get(i);
			ArrayList<Integer> periods = new ArrayList<>();
			int size = 0;
            int current = 0;
            for (int i_1 = 0; i_1 < itemsets.size(); i_1++) {                	
                for (int j = current; j < match_rule.size(); j++) {                                         
                    if (itemsets.get(i_1).containsAll(match_rule.get(j))) {    
                        current = j;
                        periods.add(i_1);
                        current++;
                        size++;                  
                    }     
                    break;
                }                                                            
            }                
            if (size == match_rule.size()) {  
            	sup++;
                if (periods.containsAll(comp2)) {
                	match_number++;           
                }
            }    
            
		}
		if (match_number >= 0.5*min_sup) {
			f = true;
		}
		System.out.println(match_number +  " " + f + " " + sup);
		return f;
	}
	
	/**Rule Matching**/
    public HashMap<Integer, ArrayList<String>> RuleMapping(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules,
    HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_testing, HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_training, HashMap<Integer, String> target_class, int min_sup) throws IOException {
    	
//        for (Integer i  : SDB_for_training.keySet()) {
//        	System.out.println(SDB_for_training.get(i));
//        }
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
            if (Rise_number >= Down_number) {
                answer.add("Rise");        	
            } else {
            	answer.add("Down");   
            }	
        }
                                                 	 
    	//2.Begin Matching 
        HashMap<Integer, ArrayList<String>> result = new HashMap<>(); 
        try {    
    	    File fout = new File("match.txt");
	        FileOutputStream fos = new FileOutputStream(fout);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
        for (Integer i : SDB_for_testing.keySet()) {
        
//        	System.out.println("SDB " + i);
            //Match rule's number
            int match_number = 0;
//            HashMap<ArrayList<ArrayList<String>>, ArrayList<Integer>> MATCH_RULES = new HashMap<>();     
            ArrayList<ArrayList<ArrayList<String>>> match_rules = new ArrayList<>();
            //The sequence in SDB_Testing
            ArrayList<ArrayList<String>> itemsets = SDB_for_testing.get(i);                            
            for (ArrayList<ArrayList<String>> rule : rules.keySet()) {
//            	ArrayList<Integer> periods = new ArrayList<>();
                //The size of mapping items in rule                
                int size = 0;
                int current = 0;
//                MATCH_RULES = matching(MATCH_RULES, itemsets, rule,  periods, size, current, match_number);
                for (int i_1 = 0; i_1 < itemsets.size(); i_1++) {                	
                    for (int j = current; j < rule.size()-1; j++) {                                         
                        if (itemsets.get(i_1).containsAll(rule.get(j))) {    
                            current = j;
//                          periods.add(i_1);
                            current++;
                            size++;
                        }   
                        break;
                    }                                                            
           
                }                
                if (size == rule.size()-1) {
                	match_number++;      
//                	MATCH_RULES.put(rule, periods);
//                	System.out.println(rule + " " + rules.get(rule).get(0));
//                	match_rules.add(rule);
                	if (contain_last(SDB_for_training, rule, min_sup)) {
               	        match_rules.add(rule);	
                	}
//                	osw.write("i: "+ i + " " + rule + " " + periods + "\r\n");
                	//System.out.println(i+" "+rule + " " + periods);
                } 
            } 
           
//          System.out.println(i + " match_number:" + match_number);            
            if (match_number >= 2){ 
            	int choose = 3;
            	if (choose == 1) {
            	    //METHOD1
//            	    result.put(i,  MTHODE1(rules, MATCH_RULES, i));
            	} else if (choose == 2) {
            		//METHOD2          
//            	    result.put(i,  MTHODE2(rules, MATCH_RULES, i));            	 
            	} else if (choose == 3) {
                   	//METHOD3
            		result.put(i, MTHODE3(rules, match_rules, i));	
            	} else {
            		//CBS            
//            	    System.out.println(i+"th======================");
            		result.put(i, getinstance(rules, match_rules, SDB_for_training));
            	}
            } else if (0< match_number && match_number < 2){
            	//Only one match_rule
//            	for (ArrayList<ArrayList<String>> rule : MATCH_RULES.keySet()) {
//                    ArrayList<String> Rise_Down = rule.get(rule.size()-1);	
//            		result.put(i,Rise_Down);
//            	}        
            	ArrayList<ArrayList<String>> rule = match_rules.get(0);
            	ArrayList<String> Rise_Down = rule.get(rule.size()-1);
            	result.put(i,Rise_Down);
            } else {
            	result.put(i,answer);            	         	           
            }
        	
        
        }    
        osw.close();
        } catch (FileNotFoundException e) {
        	System.out.println("[ERROR] File Not Found Exception.");
            e.printStackTrace();
        } catch (IOException e) {
        	System.out.println("[ERROR] I/O Exception.");
            e.printStackTrace();  	
        }  
        
        return result;         	 	
       
    }
    
    /**Performance Evaluation**/       
    public HashMap<String, Double> evaluate(HashMap<Integer, String> class_table , HashMap<Integer, ArrayList<String>> predict, int traing_data_size, int next_week) throws FileNotFoundException {
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
        int index = traing_data_size+1;   
        
 
//       System.out.println(class_table.size());
        
        for (int i = 1; i <= predict.size(); i++) {
        		System.out.println(i+ " " + predict.get(i).get(0) + " vs " + " " +(index +next_week) + " "+class_table.get(index +next_week));
                if (predict.get(i).get(0).equals("Rise"))	{
                    if (class_table.get(index +next_week).equals("Rise")) {
                    	True_Positive += 1;	
                    } else {
                    	False_Negative += 1;
                    }
                	
                } else  {
                	if (class_table.get(index +next_week).equals("Down")) {
                		True_Negative += 1;	
                    } else {
                    	False_Positive += 1;
                    }              	
                }
                index += next_week;
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
        double macro_f_measure = 2*(macro_precision*macro_recall)/ (macro_precision+macro_recall);
        e.put("macro_f_measure", macro_f_measure);
        return e;
        } 
 
}
