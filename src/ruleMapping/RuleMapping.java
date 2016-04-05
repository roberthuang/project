package ruleMapping;

import java.io.*;
import java.util.*;

import ca.pfv.spmf.patterns.rule_itemset_array_integer_with_count.Rule;

public class RuleMapping {
	//LIFT_MEASURE
	
	//rules:是探勘過後的規則
	HashMap<ArrayList<ArrayList<String>>, Double> Lift_Measure(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>>  rules, HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules_all, HashMap <String, Integer>rise_down_number){
		ArrayList<ArrayList<ArrayList<String>>> rules_after_pruning = new ArrayList<>();
		for (ArrayList<ArrayList<String>> rule : rules.keySet()) {
			rules_after_pruning.add(rule);
		}
	
		//Evaluate score
		HashMap<ArrayList<ArrayList<String>>, Double> weight_scores = new HashMap<>();
		for (ArrayList<ArrayList<String>> rule : rules.keySet()){
		    double score = 0;
		    String target = rule.get(rule.size()-1).get(0);
		    score = rules.get(rule).get(1)/  (double) rise_down_number.get(target);	
//		    System.out.println(rules.get(rule).get(1)+ "  "  + rise_down_number.get(target) + "  "+score);
		    if (score != 1) {
		    	if (score == 0) System.out.println("!!!!!!!!!!!11");
		    	if (score < 1) {
		    		score = 1 / (double) score; 
		    		//System.out.println(rule + "   " + score);
		    		weight_scores.put(rule, score);
		    	} else {
		    		weight_scores.put(rule, score);
		    	}
		    }
		}		

		System.out.println(rules_after_pruning.size());
		//Pruning
		for (int i = 0; i < rules_after_pruning.size(); i++) {

			ArrayList<ArrayList<String>> i_rule = rules_after_pruning.get(i);
		    for (int j = i+1; j < rules_after_pruning.size(); j++) {
		    	ArrayList<ArrayList<String>> j_rule = rules_after_pruning.get(j);
		    	
		    	//判別j是否是i的子集
		    	//The size of mapping items in rule                
                int size = 0;
                int current = 0;
                for (int i_1 = 0; i_1 < i_rule.size(); i_1++) {                	
                    for (int j_1 = current; j_1 < j_rule.size(); j_1++) {                                         
                        if (i_rule.get(i_1).containsAll(j_rule.get(j_1))) {    
                            current = j_1;
                            current++;
                            size++;
                        }   
                        break;
                    }                                                            
           
                }                
                if (size == j_rule.size()) {
                    if (weight_scores.get(j_rule) > weight_scores.get(i_rule)) {
                    	//刪除i_rule
                    	rules_after_pruning.remove(i--);
                    	break;
                    }

                }	
                
                //清空
                size = 0;
                current = 0;                
                //判別i是否是j的子集

                for (int j_1 = 0; j_1 < j_rule.size(); j_1++) {                	
                    for (int i_1 = current; i_1 < i_rule.size(); i_1++) {                                         
                        if (j_rule.get(j_1).containsAll(i_rule.get(i_1))) {    
                            current = i_1;
                            current++;
                            size++;
                        }   
                        break;
                    }                                                            
           
                }                
                if (size == i_rule.size()) {
                    if (weight_scores.get(i_rule) > weight_scores.get(j_rule)) {
                    	//刪除j_rule
                    	rules_after_pruning.remove(j--);                    	
                    }
                }	
 
		    }
			
		}
		
		System.out.println(rules_after_pruning.size());
		HashMap<ArrayList<ArrayList<String>>, Double> result = new HashMap<>();
		for (ArrayList<ArrayList<String>> rule : rules_after_pruning) {
			weight_scores.put(rule,weight_scores.get(rule));
		}
		return result;
	}
	
	public  ArrayList<String> getLift(HashMap<ArrayList<ArrayList<String>>, Double> Lift_Score, ArrayList<ArrayList<ArrayList<String>>> match_rules, int i, double min_conf, ArrayList<String> answer, int min_sup){
		
		
		
		ArrayList<ArrayList<ArrayList<String>>> Rise_set = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<String>>> Down_set = new ArrayList<>();
		

		for (ArrayList<ArrayList<String>> match_rule : match_rules) {
			 String str = match_rule.get(match_rule.size()-1).get(0);
			    if (str.equals("Rise")) {			    	   	 
			    	Rise_set.add(match_rule);	
			    } else {
			    	Down_set.add(match_rule);
			    }			
	    }		
		
		double rise_score = 0;
		double down_score = 0;
		
		for (ArrayList<ArrayList<String>> rise_rule : Rise_set) {
			if (Lift_Score.get(rise_rule) == null) continue;
			rise_score += Lift_Score.get(rise_rule);
		}
		for (ArrayList<ArrayList<String>> down_rule : Down_set) {
			if (Lift_Score.get(down_rule) == null) continue;
			down_score += Lift_Score.get(down_rule);
		}
		
		if (rise_score > down_score) {
			ArrayList<String> result = new ArrayList<>();
			result.add("Rise");
			return result;
		} else if (rise_score == down_score) {
			return answer;
		} else {
			ArrayList<String> result = new ArrayList<>();
		    result.add("Down");
		    return result;
		} 
		
		//Highest 
		/*
		String target = answer.get(0);
		double max = 0;
		for (ArrayList<ArrayList<String>> match_rule : match_rules) {
			if (Lift_Score.get(match_rule) == null) continue;
		    if (Lift_Score.get(match_rule) > max) {
		    	max = Lift_Score.get(match_rule);
		    	target = match_rule.get(match_rule.size()-1).get(0);
		    }
		}
		
		ArrayList<String> result = new ArrayList <>();
		result.add(target);
		return result;*/
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**CBE_CBS**/
	static int rise_set_size = 0;
	static int down_set_size = 0;
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
	
	//比較C1 與 C2 的總分，大者為預設的Class 總分相同則取擁有較多數的為主
	/**CBE_CBS**/
	ArrayList<String> getDefault(HashMap<ArrayList<ArrayList<String>>, Double> classifier) {
		ArrayList<String> result = new ArrayList<>();
		double default_socre_rise = 0;
		double default_socre_down = 0;
		for (ArrayList<ArrayList<String>> rule : classifier.keySet()) {
		    if (rule.get(rule.size()-1).get(0).equals("Rise")) {
		    	default_socre_rise += classifier.get(rule)/(double) rise_set_size;
		    } else {
		    	default_socre_down += classifier.get(rule)/(double) down_set_size;
		    }
		}
		//System.out.println(rise_set_size + " " + down_set_size);
		//System.out.println(default_socre_rise + "  " + default_socre_down);
		if (default_socre_rise > default_socre_down) {
			result.add("Rise");
			return result;
		} else if (default_socre_rise == default_socre_down) {
			if (rise_set_size > down_set_size) {
				result.add("Rise");
				return result;	
			} else {
				result.add("Down");
				return result;	
			}
		} else {
			result.add("Down");
			return result;
		}
	}
	
	/**CBE_CBS
	 * @throws IOException **/
	ArrayList<String> getinstance(HashMap<ArrayList<ArrayList<String>>, Double> classifier, ArrayList<ArrayList<ArrayList<String>>> match_rules, ArrayList<String> defaultclass, HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules, int index, HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules_all, double min_conf, ArrayList<String> answer, int min_sup) throws IOException {
		//test
		//File fout_test = new File("C:\\user\\workspace\\project\\testing_data\\test_"+index+"_"+ min_sup + ".txt");
	    //FileOutputStream fos_test = new FileOutputStream(fout_test);
        //OutputStreamWriter osw_test = new OutputStreamWriter(fos_test);       
		
		
	    File fout = new File("C:\\user\\workspace\\project\\matching_problem\\testing_" + index + "_"+ min_conf+ "_"+ min_sup +".txt");
	    FileOutputStream fos = new FileOutputStream(fout);
        OutputStreamWriter osw = new OutputStreamWriter(fos);           
			

		ArrayList<ArrayList<ArrayList<String>>> Rise_set = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<String>>> Down_set = new ArrayList<>();
		
		
		File fout_all = new File("rules_all_for_matching.txt");
		FileOutputStream fos_all = new FileOutputStream(fout_all);
	    OutputStreamWriter osw_all = new OutputStreamWriter(fos_all);  
		
		for (ArrayList<ArrayList<String>> match_rule : match_rules) {
			 String str = match_rule.get(match_rule.size()-1).get(0);
			    if (str.equals("Rise")) {			    	   	 
			    	Rise_set.add(match_rule);	
			    } else {
			    	Down_set.add(match_rule);
			    }			
	    }
		
		int INDEX = 1;
		HashMap<ArrayList<ArrayList<String>>, Integer> rules_all_index = new HashMap<>();
		for (ArrayList<ArrayList<String>> rule_all : rules_all.keySet()) {
			osw_all.write(Integer.toString(INDEX)+ "    " + rule_all + "\r\n");
			rules_all_index.put(rule_all, INDEX);			
         	INDEX++;
        }     
		
		osw_all.close();
		
		//PRINT ALL MATCH RULE 
		osw.write("Rise ("+ Rise_set.size()+"):" + "\r\n");
		for (ArrayList<ArrayList<String>> rise_match_rule : Rise_set) {			
            osw.write(rules_all_index.get(rise_match_rule) + "(C: " + rules.get(rise_match_rule).get(1) + "   S: " + rules.get(rise_match_rule).get(0)+ "   L:"+ rise_match_rule.size()+ ")" +"\r\n");                 
		}
		//PRINT ALL MATCH RULE
		osw.write("Down ("+ Down_set.size()+"):" + "\r\n");
		for (ArrayList<ArrayList<String>> down_match_rule : Down_set) {
            osw.write(rules_all_index.get(down_match_rule) +  "(C: " + rules.get(down_match_rule).get(1) + "   S: " + rules.get(down_match_rule).get(0)+ "   L:"+ down_match_rule.size()+ ")" +"\r\n"); 
		}
		
		ArrayList<String> result = new ArrayList<>();
		if (Rise_set.isEmpty()) {
			result.add("Down");
			osw.write("Rise_set.isEmpty()" + "\r\n");
			osw.close();
			return result;
		} else if (Down_set.isEmpty()) {
			result.add("Rise");
			osw.write("Down_set.isEmpty()" + "\r\n");
			osw.close();
			return result;
		} else {
			//int top_k = 2;
			//ArrayList<Double> top_k_list_rise = new ArrayList<>();
			//int r = 0;
		    double score_rise = 0;
		    for (ArrayList<ArrayList<String>> rise_match_rule : Rise_set) {
		    	if (classifier.get(rise_match_rule) == null) {		    		
		    		continue;
		    	} else {
		    		//PRINT ALL MATCH RULES IN CLASSIFIER
		    		osw.write("Rise " + rules_all_index.get(rise_match_rule) + " " + rules.get(rise_match_rule).get(1) + " " + rules.get(rise_match_rule).get(0)+ "\r\n");
		    		double score = classifier.get(rise_match_rule);
		    		//top_k_list_rise.add(score);
		    		//r++;
			    	score_rise += score;
		    	}		    	
		    }
		    
		    /*
		    Comparator<Double> comp = (Double a, Double b) -> {
	            return b.compareTo(a);
	        };
	        
	        Collections.sort(top_k_list_rise, comp);
	        if (r == 0) {
	        	score_rise  = 0;
	        } else {
	        	 for (int i = 0; i < top_k; i++) {
	        		if (i < r) {
	        			double score = top_k_list_rise.get(i);	
		        		score_rise += score;
	        			      
	        		}
	 	        		        	
	 	        }
	        	
	        	
	        }*/
	       
		    score_rise /= (double) rise_set_size;
		    
		    //int d = 0;
		    //ArrayList<Double> top_k_list_down= new ArrayList<>();
		    double score_down = 0;
		    for (ArrayList<ArrayList<String>> down_match_rule : Down_set) {
		    	if (classifier.get(down_match_rule) == null) {
		    		
		    		//System.out.println(index + "   " + rules_all_index.get(down_match_rule) + "null");		    		
		    		//osw_test.write(index + "   " + rules_all_index.get(down_match_rule) + "null" + "\r\n");
		    		continue;
		    	} else {
		    		//PRINT ALL MATCH RULES IN CLASSIFIER
		    		osw.write("Down " + rules_all_index.get(down_match_rule) + " " + rules.get(down_match_rule).get(1)+ " " + rules.get(down_match_rule).get(0) + "\r\n");
		    	    double score = classifier.get(down_match_rule);
		    	    score_down += score;
		    	    //d++;
		    	    //top_k_list_down.add(score);
		    	}
		    }
		   /*
		    Collections.sort(top_k_list_down, comp);
		    if (d == 0) {
		    	score_down = 0;	
		    } else {
		    	for (int i = 0; i < top_k; i++) {
		        	if (i < d) {
		        		double score = top_k_list_down.get(i);	
		        		score_down += score;
		        	}
		        			        	
		        }
		    	
		    	
		    }*/
	     
		    
		    score_down /= (double) down_set_size;
		    
//		    System.out.println(index + "  " + r + "  " + d);
		    
		    if (score_rise  > score_down) {
		    	result.add("Rise");
		    	//PRINT RESULT
		    	osw.write("\r\n");
		    	osw.write("score_rise  > score_down\r\n");
		    	osw.write(score_rise+ "  " +  score_down+"\r\n");
		    	osw.write("r_s: " + rise_set_size  +"\r\n");
		    	osw.write("d_s: " + down_set_size  +"\r\n");
		    	osw.close();
				return result;	
		    } else if (score_rise == score_down) {
		    	//PRINT RESULT
		    	osw.write("\r\n");
		    	osw.write("score_rise  == score_down\r\n");
		    	osw.write(score_rise+ "  " +  score_down+"\r\n");
		    	osw.write("r_s: " + rise_set_size  +"\r\n");
		    	osw.write("d_s: " + down_set_size  +"\r\n");
		    	osw.close();
		    	return answer;
		    } else {
		    	result.add("Down");
		    	//PRINT RESULT
		    	osw.write("\r\n");
		    	osw.write("score_rise < score_down\r\n");
		    	osw.write(score_rise+ "  " +  score_down+"\r\n");
		    	osw.write("r_s: " + rise_set_size  +"\r\n");
		    	osw.write("d_s: " + down_set_size  +"\r\n");
		    	osw.close();
				return result;	
		    }
		    
		}
	}
	
	/**CBE_CBS
	 * @throws IOException **/
	public static HashMap<ArrayList<ArrayList<String>>, Double> CBS_build_classifier(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules, HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_training, int window_size, HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules_all, int min_sup, double min_conf) throws IOException {
	    
		//FIND RULE INDEX
		int INDEX = 1;
		HashMap<ArrayList<ArrayList<String>>, Integer> rules_all_index = new HashMap<>();
		for (ArrayList<ArrayList<String>> rule_all : rules_all.keySet()) {			
			rules_all_index.put(rule_all, INDEX);			
         	INDEX++;
        }     
		
		//PRINT CBS DEMOVE DUPLICATES BY MOVING SUPPORT
	    File fout = new File("C:\\user\\workspace\\project\\CBS_R_BY_S\\"+ min_sup + "_" + min_conf+".txt");
	    FileOutputStream fos = new FileOutputStream(fout);
        OutputStreamWriter osw = new OutputStreamWriter(fos);      
		
		
		
		
		HashMap<ArrayList<ArrayList<String>>, Double> result = new HashMap<>();
		ArrayList<ArrayList<ArrayList<String>>> rule_set = new ArrayList<>();
		for (ArrayList<ArrayList<String>> rule : rules.keySet()) {			
			rule_set.add(rule);
		}
		
		

		int large_than_rise = 0;
        int large_than_down = 0;
		
		double globalEntropy = Cacluate_all_entropy(SDB_for_training);		
		int pairt_of_redundant = 0;
		int real = rule_set.size();
		/**Remove Duplicates**/ 
        for (int i = 0 ; i < rule_set.size(); i++) {
    	    boolean same = false;
    	    for (int j = i+1; j < rule_set.size(); j++) {
    	        ArrayList<ArrayList<String>> temp1 = new ArrayList<>();
    		    for (int k1 = 0; k1 < rule_set.get(i).size()-1; k1++) {
    		        temp1.add(rule_set.get(i).get(k1));
    	        }    
    	        String str1 = rule_set.get(i).get(rule_set.get(i).size()-1).get(0);
    	        ArrayList<ArrayList<String>> temp2 = new ArrayList<>();
    		    for (int k1 = 0; k1 < rule_set.get(j).size()-1; k1++) {
    		        temp2.add(rule_set.get(j).get(k1));
    	        }
    	        String str2 = rule_set.get(j).get(rule_set.get(j).size()-1).get(0);
    	        if ((temp1.equals(temp2)) && (!str1.equals(str2))) {
    	        	//PRINT DUPLICATES
    	        	//int index_1 = rules_all_index.get(rule_set.get(i));  
    	        	//int index_2 = rules_all_index.get(rule_set.get(j));
    	        	//osw.write(index_1 + "    " + index_2 + "\r\n");
    	        	//osw.write(rules.get(rule_set.get(i)).get(1) + "    " + rules.get(rule_set.get(j)).get(1) + "\r\n");

    		        same = true;
    		        rule_set.remove(j--);		    		        	
    		        break;
    	        } 
    	    }    
    	    if (same) {
    	        //System.out.println(i);
    	    	
    	    		rule_set.remove(i--);	 
    	    	
       
    	    }
    	}
      
        osw.close();
        /**Caculate size**/
        for (ArrayList<ArrayList<String>> rule : rule_set) {
        	String rise_down = rule.get(rule.size()-1).get(0);        	
            if (rise_down.equals("Rise")) {
            	
            	rise_set_size++;
            } else {

            	down_set_size++;
            }
        	
        }
        //System.out.println(min_conf  + " "+ real + " "  + "Rise: " + rise_set_size + "Down: " + down_set_size);
        //System.out.println((large_than/ (double)rule_set.size()));
        //System.out.println(min_conf + "   " +large_than_rise + "   " + large_than_down);
//      System.out.println(min_conf + "   " +rise_set_size + "   " + down_set_size);
//		for (ArrayList<ArrayList<String>> class_member:class1_set) {
//			System.out.println(class_member);
//		}
//		for (ArrayList<ArrayList<String>> class_member:class2_set) {
//			System.out.println(class_member);
//		}
		
		//System.out.println("Mapping:");
		
	    ArrayList<Integer> compare_last = new ArrayList<>();
		compare_last.add(window_size-1);
		compare_last.add(window_size-2);
		for (ArrayList<ArrayList<String>> rule : rule_set) {	
		//for (ArrayList<ArrayList<String>> rule : rule_set_removed_duplicates) {
//			System.out.println(class1_member);
			double score = 0;
			int match_number = 0;
			double Entropy = 0;
			double SplitInfo = 0;
			double gainratio = 0;
			int match_c1_number = 0;
			int match_c2_number = 0;
			
			/**Evaluate Weight**/
			int support = 0;
			int match_last = 0;
			ArrayList<Integer> periods = new ArrayList<>();
			
			/*int none_match_c1_number = 0;
			int none_match_c2_number = 0;*/
		    for (Integer i : SDB_for_training.keySet()) {
		    	ArrayList<ArrayList<String>> rule_from_training = SDB_for_training.get(i);
		        int size = 0;
		        int current = 0;
		        
		        for (int i_1 = 0; i_1 <  rule_from_training.size()-1; i_1++) {                	
                    for (int j = current; j < rule.size()-1; j++) {                                         
                        if (rule_from_training.get(i_1).containsAll(rule.get(j))) {    
                            current = j;
                            current++;
                            periods.add(i_1);
                            size++;
                        }  
                        break;
                    }   
                                       
                }       
		        if (size == rule.size()-1) {
                	match_number++;      
                	support++;
                	if (periods.containsAll(compare_last)) {
                		match_last++;
                	}
                	//System.out.println(rule.get(rule.size()-1).get(0));
                	if (rule_from_training.get(rule_from_training.size()-1).get(0).equals("Rise")) {
                	    match_c1_number++;	
                	} else if (rule_from_training.get(rule_from_training.size()-1).get(0).equals("Down")){
                		match_c2_number++;
                	}
                }
		    }
		    /**Weight**/
		    int not_match_last = support - match_last;
		    double match_frequency = match_last / (double) support;
		    double not_match_frequency = not_match_last / (double) support;
		    double weight = match_frequency*2 + not_match_frequency*1;
 		    
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
		    double confidence = rules.get(rule).get(1);
		    double length = rule.size();
		    gainratio = Math.abs(gain/SplitInfo);
		    score = confidence*gainratio*length*weight;	
		    result.put(rule, score);
		}
		return result;

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
	
		
	
		comp3.add(3);
		comp3.add(4);
	
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
	
	/**CBE_CBA
	 * @throws IOException **/
	public  ArrayList<String> CBA (HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules, ArrayList<ArrayList<ArrayList<String>>> match_rules, int index, ArrayList<String> answer, double min_conf, HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules_all, int min_sup) throws IOException {
		 
	    File fout = new File("C:\\user\\workspace\\project\\matching_problem\\testing_" + index + "_"+min_conf+ "_" + min_sup +".txt");        
	    FileOutputStream fos = new FileOutputStream(fout);
	    OutputStreamWriter osw = new OutputStreamWriter(fos);
		
		ArrayList<ArrayList<ArrayList<String>>> rule_set = new ArrayList<>();
		for (ArrayList<ArrayList<String>> rule : match_rules) {
			rule_set.add(rule);
		}
		/*
		int number_1 = 0;
		int number_2 = 0;
		int number_3 = 0;
		int number_4 = 0;
		int number_5 = 0;
		
		for (ArrayList<ArrayList<String>> rule : rules.keySet()) {
			double conf = rules.get(rule).get(1);
		    if (conf  >= 0.6) {
		    	double sup = rules.get(rule).get(0);
		    	
		    	double i_1 = 2/ (double) 282;
		        if (sup >= i_1)	number_1++;
		        double i_2 = 5/ (double) 282;
		        if (sup >= i_2)	number_2++;
		        double i_3 = 8/ (double) 282;
		        if (sup >= i_3)	number_3++;
		        double i_4 = 11/ (double) 282;
		        if (sup >= i_4)	{
		        	number_4++;
		        }
		        double i_5 = 14/ (double) 282;
		        if (sup >= i_5)	number_5++;
		    }
			
			
		}
		System.out.println(number_1 + " " + number_2 + " " + number_3 + " " + number_4 + " " + number_5);
		*/
		
		/**Remove Duplicates label**/
		ArrayList<ArrayList<ArrayList<String>>> rule_set_removed_duplicates = new ArrayList<>();
		List<ArrayList<ArrayList<String>>> temp_rule_set = new ArrayList<>();
		
        for (ArrayList<ArrayList<String>> rule : rule_set) {
        	temp_rule_set.add(rule);
        }
        /*
        for (int i = 0 ; i < temp_rule_set.size(); i++) {
    	    boolean same = false;
    	    for (int j = i+1; j < temp_rule_set.size(); j++) {
    	        ArrayList<ArrayList<String>> temp1 = new ArrayList<>();
    		for (int k1 = 0; k1 < temp_rule_set.get(i).size()-1; k1++) {
    		    temp1.add(temp_rule_set.get(i).get(k1));
    	    }    
    	    String str1 = temp_rule_set.get(i).get(temp_rule_set.get(i).size()-1).get(0);
    	    ArrayList<ArrayList<String>> temp2 = new ArrayList<>();
    		for (int k1 = 0; k1 < temp_rule_set.get(j).size()-1; k1++) {
    		    temp2.add(temp_rule_set.get(j).get(k1));
    	        }
    	        String str2 = temp_rule_set.get(j).get(temp_rule_set.get(j).size()-1).get(0);
    	        if ( (temp1.equals(temp2)) && (!str1.equals(str2)) ) {
    	            //System.out.println(temp1 + " " + temp2);
    		    same = true;
    		    temp_rule_set.remove(j--);		   
    	        } 
    	    }    
    	    if (same) {
    	        //System.out.println(i);
    	    	temp_rule_set.remove(i--);	       
    	    }
    	}
		*/
        for (ArrayList<ArrayList<String>> rule : temp_rule_set) {        	
        	rule_set_removed_duplicates.add(rule);

        }		
       
        //System.out.println(rule_set_removed_duplicates.size());
        if (rule_set_removed_duplicates.size() == 0 ) return answer;
		int max = 0;		
        double max_sup = rules.get(rule_set_removed_duplicates.get(0)).get(0);
        double max_confidence = rules.get(rule_set_removed_duplicates.get(0)).get(1);              
        for (int j = 1; j <rule_set_removed_duplicates.size(); j++) {
            double confidence = rules.get(rule_set_removed_duplicates.get(j)).get(1);
            double sup = rules.get(rule_set_removed_duplicates.get(j)).get(0);
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
             		int INDEX = 1;
             		int j_number = 0;
             		int max_number = 0;
             	    for (ArrayList<ArrayList<String>> rule : rules_all.keySet()) {
             	        if (rule.equals(rule_set_removed_duplicates.get(j))) {
             	        	j_number  = INDEX;	
             	        }
             	        if (rule.equals(rule_set_removed_duplicates.get(max))) {
             	        	max_number = INDEX;
             	        }
             	        INDEX++;
             	    }
             	    if (j_number < max_number) {
             	        max = j;
             	        max_sup = sup;
             	        max_confidence = confidence;                  
             	    } else {
             	        continue;
             	    }	    
                }
            }
         	
        }   
        
        osw.write(index + "\r\n");
        
        for (ArrayList<ArrayList<String>> match_rule : match_rules) {
        	 int INDEX = 1;
        	 for (ArrayList<ArrayList<String>> rule_all : rules_all.keySet()) {
                 if (rule_all.equals(match_rule)) {
                    osw.write(INDEX + "(C: " + rules.get(match_rule).get(1) + "   S: " + rules.get(match_rule).get(0)+ "   L:"+ match_rule.size()+ ")" +"\r\n"); 
                    INDEX++;
                    break;
                 }
             	 INDEX++;
             }        	

        }
       	 
        //幾中幾
        ArrayList<ArrayList<String>> match_rule = rule_set_removed_duplicates.get(max);
        int INDEX = 1;
   	    for (ArrayList<ArrayList<String>> rule_all : rules_all.keySet()) {
            if (rule_all.equals(match_rule)) {
               osw.write("LAST: " + INDEX + "\r\n"); 
               INDEX++;
               break;
            }
        	 INDEX++;
        }    
   	    
   	
   	    osw.write("Matching_rule_size:" + match_rules.size()+ "\r\n"); 
        
        ArrayList<String> Rise_Down = match_rule.get(match_rule.size()-1);
       	  
//      System.out.println(index + "_m3" +  " " +  match_rule);
        
		
        osw.close();
          
		return Rise_Down;
	
	}
	
	
	double contain_last (HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_training,  ArrayList<ArrayList<String>> match_rule, int min_sup, int window_size) {
		ArrayList<Integer> comp2 = new ArrayList<>();		
		comp2.add(window_size-1);
		comp2.add(window_size-2);
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
//		if (match_number >= 1) {
//			f = true;
//		}
		double contain_last = match_number / (double) sup;
		int rest = sup - match_number;
		double not_contain_last = rest / (double) sup;
//		System.out.println(match_number +  " " + f + " " + sup);
		return contain_last*2 + not_contain_last*1;
	}
	
	/**Rule Matching**/
    public HashMap<Integer, ArrayList<String>> RuleMapping(HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules,
    HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_testing, HashMap<Integer, ArrayList<ArrayList<String>>> SDB_for_training, HashMap<Integer, String> target_class, HashMap<ArrayList<ArrayList<String>>, ArrayList<Double>> rules_all,int min_sup, int window_size, double min_conf, HashMap<String, Integer> rise_down_number ) throws IOException {
    	//System.out.println(rules_all.size());

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
    	/**Lift_CLASSIFIER**/
    	HashMap<ArrayList<ArrayList<String>>, Double> Lift_Score = Lift_Measure(rules, rules_all, rise_down_number); 
    		
    	
    	
    	
    	
        /**CBS_CLASSIFIER**/
//    	HashMap<ArrayList<ArrayList<String>>, Double> classifier = CBS_build_classifier(rules, SDB_for_training, window_size, rules_all, min_sup, min_conf);
//    	ArrayList<String> defaultclass = getDefault(classifier);
    	
    	//2.Begin Matching 
        HashMap<Integer, ArrayList<String>> result = new HashMap<>(); 
        //try {    
    	    //File fout = new File("match.txt");
	        //FileOutputStream fos = new FileOutputStream(fout);
            //OutputStreamWriter osw = new OutputStreamWriter(fos);
        for (Integer i : SDB_for_testing.keySet()) {      
            //Match rule's number
            int match_number = 0;
//          HashMap<ArrayList<ArrayList<String>>, Double> MATCH_RULES = new HashMap<>();     
            ArrayList<ArrayList<ArrayList<String>>> match_rules = new ArrayList<>();
            //The sequence in SDB_Testing
            ArrayList<ArrayList<String>> itemsets = SDB_for_testing.get(i);                            
            for (ArrayList<ArrayList<String>> rule : rules.keySet()) {
                //The size of mapping items in rule                
                int size = 0;
                int current = 0;
//                MATCH_RULES = matching(MATCH_RULES, itemsets, rule,  periods, size, current, match_number);
                for (int i_1 = 0; i_1 < itemsets.size(); i_1++) {                	
                    for (int j = current; j < rule.size()-1; j++) {                                         
                        if (itemsets.get(i_1).containsAll(rule.get(j))) {    
                            current = j;
                            current++;
                            size++;
                        }   
                        break;
                    }                                                            
           
                }                
                if (size == rule.size()-1) {
                	match_number++;      
                	match_rules.add(rule);

                }
            } 
            int cbs_cba_for_null_rule = 0;     
            if (match_number >= 2){ 
            	int choose = 2;
            	cbs_cba_for_null_rule = choose;
            	if (choose == 1) {

            	} else if (choose == 2) {
            		//LIFT_MEASURE         
            	    result.put(i, getLift(Lift_Score, match_rules, i, min_conf, answer, min_sup));            	 
            	} else if (choose == 3) {
                   	//CBA
            		result.put(i, CBA(rules, match_rules, i, answer, min_conf, rules_all, min_sup));	
            	} else {
            		//CBS            
//            	    System.out.println(i+"th======================");
//            		result.put(i, getinstance(classifier, match_rules,  defaultclass, rules, i, rules_all, min_conf, answer, min_sup));
            	}
            } else if (0< match_number && match_number < 2){
            	//Only one match_rule
//            	for (ArrayList<ArrayList<String>> rule : MATCH_RULES.keySet()) {
//                    ArrayList<String> Rise_Down = rule.get(rule.size()-1);	
//            		result.put(i,Rise_Down);
//            	}                    	
            	File fout = new File("C:\\user\\workspace\\project\\matching_problem\\testing_" + i + "_"+min_conf + "_" +min_sup+"one_rule.txt");        
        	    FileOutputStream fos = new FileOutputStream(fout);        	    
        	    OutputStreamWriter osw = new OutputStreamWriter(fos);
            	ArrayList<ArrayList<String>> rule = match_rules.get(0);
            	ArrayList<String> Rise_Down = rule.get(rule.size()-1);
            	System.out.println(i + " Only one rule" + Rise_Down);
            	osw.write(i + "   " + Rise_Down + "\r\n"); 
            	result.put(i,Rise_Down);
            	osw.close();
            } else {
            	
            	//CBA OR CBS
            	File fout = new File("C:\\user\\workspace\\project\\matching_problem\\testing_" + i + "_"+min_conf+  "_" +min_sup+"Guess.txt");        
        	    FileOutputStream fos = new FileOutputStream(fout);
        	    OutputStreamWriter osw = new OutputStreamWriter(fos);
        	        
        	    if (cbs_cba_for_null_rule == 4) {
        	    	//CBS
//                	System.out.println(i + " Guess" +  	defaultclass);     
//                	osw.write(i + "   " + answer + "\r\n");             	
//               	    result.put(i,answer);   
//                	osw.close();	
        	    } else {
        	    	//CBA
                	System.out.println(i + " Guess" +  	answer);     
                	osw.write(i + "   " + answer + "\r\n");             	
                	result.put(i,answer);   
                	osw.close();
        	    }            	
            }

        } //for
        //osw.close();
        //} catch (FileNotFoundException e) {
        //	System.out.println("[ERROR] File Not Found Exception.");
         //   e.printStackTrace();
        //} catch (IOException e) {
        //	System.out.println("[ERROR] I/O Exception.");
        //    e.printStackTrace();  	
        //}  
        rise_set_size = 0;
        down_set_size = 0;
        return result;         	 	
       
    }
    
    /**Performance Evaluation**/       
    public HashMap<String, Double> evaluate(HashMap<Integer, String> class_table , HashMap<Integer, ArrayList<String>> predict, int traing_data_size, int next_week, int records_size, double  min_conf, int min_sup) throws FileNotFoundException {
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
        int testing_start = traing_data_size+1;   
        int index = testing_start + next_week;
 
//       System.out.println(class_table.size());
        try {
        	File fout = new File("C:\\user\\workspace\\project\\matching_problem\\not_equal_" +min_conf+ "_" + min_sup +".txt");        
     	    FileOutputStream fos = new FileOutputStream(fout);
            OutputStreamWriter osw = new OutputStreamWriter(fos);        	
        
        for (int i = 1; i <= predict.size(); i++) {
        	    
        	    if (index > records_size) continue; 
        	    if (!predict.get(i).get(0).equals(class_table.get(index))) {
        	    //	System.out.println("*" + i+ " " + predict.get(i).get(0) + " vs " + " " + (index) + " "+ class_table.get(index));
        	    	osw.write(i + "  Prediction:  " + predict.get(i).get(0) + "  Real:  " +  class_table.get(index) + "\r\n");
        	    } else {
        	    //	System.out.println(i+ " " + predict.get(i).get(0) + " vs " + " " + (index) + " "+class_table.get(index));	
        	    }
        		
                if (predict.get(i).get(0).equals("Rise"))	{
                    if (class_table.get(index).equals("Rise")) {
                    	True_Positive += 1;	
                    } else {
                    	False_Positive ++;
                    }
                	
                } else  {
                	if (class_table.get(index).equals("Down")) {
                		True_Negative += 1;	
                    } else {
                    	False_Negative ++;
                    }              	
                }
                index += 1;
        }
        osw.close(); 
        
        } catch (IOException e1) {
        	System.out.println("[ERROR] I/O Exception.");
            e1.printStackTrace();  	
        }     
        int size = True_Negative +  True_Positive + False_Positive + False_Negative;
        e.put("True_Positive", (double)True_Positive);
        e.put("True_Negative", (double)True_Negative);
        e.put("False_Positive", (double)False_Positive);
        e.put("False_Negative", (double)False_Negative);
        
        double acc =  (True_Positive + True_Negative)/ (double)(size);
        e.put("acc", acc);
        
        double precision_rise = 0;
        if (True_Positive == 0 ) {        	
            e.put("precision_rise", 0.0);	
        } else {
        	precision_rise =  True_Positive / (double)(True_Positive + False_Positive);
            e.put("precision_rise", precision_rise);
        }       
        
        double recall_rise =  True_Positive / (double)(True_Positive + False_Negative);
        e.put("recall_rise", recall_rise);
        
        
        double precision_down = 0;
        if (True_Negative == 0 ) {        	
            e.put("precision_down", 0.0);	
        } else {
        	precision_down =  True_Negative / (double)(True_Negative +  False_Negative);
            e.put("precision_down", precision_down);
        }
        
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
