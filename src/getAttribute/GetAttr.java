package getAttribute;

import java.io.*;
import java.util.*;

public class GetAttr {
	//private static HashMap<Integer, Double> temp_sl = new HashMap<>();
	//private static HashMap<Integer, Double> temp_ll = new HashMap<>();
	
	public static HashMap<Integer, String> feature(int att_index, ArrayList<ArrayList<String>> records) {
		 HashMap<Integer, String> result = new HashMap<>(); 	    
	     int col = att_index; 
	     for (int i = 1; i < records.size(); i++ ) {       
	            if (i == 1) {
	                result.put(i, "D" );     
	                continue;
	            }
	            
	            if (Double.parseDouble(records.get(i).get(col))- Double.parseDouble(records.get(i-1).get(col)) > 0 ) {
	    	    	result.put(i, "R");     
	    	    } else {
	    	    	result.put(i, "D");  
	    	    }	
        }       	        	
	    return result;		
	}
	
	public static HashMap<Integer, String> feature2(int att_index, ArrayList<ArrayList<String>> records) {
		 HashMap<Integer, String> result = new HashMap<>(); 	    
	     int col = att_index; 
	     for (int i = 1; i < records.size(); i++ ) {       
	            if (i == 1) {
	            	result.put(i, records.get(0).get(col) + "_R");      
	                continue;
	            }
	            
	            if (Double.parseDouble(records.get(i).get(col))- Double.parseDouble(records.get(i-1).get(col)) > 0 ) {
	    	    	result.put(i,  records.get(0).get(col) + "_R");     
	    	    } else {
	    	    	result.put(i,  records.get(0).get(col) + "_D");  
	    	    }	
       }       	        	
	    return result;		
	}
	
	
	public static HashMap<Integer, String> match_source_target(HashMap<Integer, String> s, HashMap<Integer, String> t) {
		HashMap<Integer, String> result = new HashMap<>(); 
	    for (int i = 1;i < t.size(); i++) {
	        if (s.get(i) == t.get(i)) {
	        	result.put(i, "Same");
	        } else {
	        	result.put(i, "Diff");
	        }
	    }	    
	    return result;
	}
	
	
	
    public static HashMap<Integer, String> Move_Average(int length, String att, int att_index, ArrayList<ArrayList<String>> records) {
        //System.out.printf("================Moving Average(%d)==================\n",length); 	
        HashMap<Integer, String> result = new HashMap<>(); 
        //int training_data = (int)((records.size()-1)*0.8);  
        //System.out.println("Training Data Size: " + training_data);
        //System.out.println("Record Data Size: " + records.size());
        
        //The column of Target
        int col = att_index;                                                                                                                            
        for (int i = 1; i < records.size(); i++ ) {       
            if (i <= length) {
                result.put(i, "MA"+ att.charAt(0) + length + "_0");     
                continue;
            }
            
            double sum_t = 0;
            double sum_t_1 = 0;
            if (i - length + 1 >= 1) {         
                for (int p_1 = i; p_1 >= i-length+1; p_1--) {                
                    sum_t = sum_t + Double.parseDouble(records.get(p_1).get(col));
                } 
                     
                int j = i - 1;
                if (j - length + 1 >=1) {
                    
                    for (int p_2 = j; p_2 >= j-length+1; p_2--) {
                       
                        sum_t_1 = sum_t_1 + Double.parseDouble(records.get(p_2).get(col));
                    }
                }
            }          
            
            //Rise or Down
            double MA = sum_t/length - sum_t_1/length;     
            if (MA > 0) {
                //System.out.println("i: " + i + " " + MA);
                result.put(i, "MA" + att.charAt(0) + length + "_1");    
            } else {
                //System.out.println("i: " + i + " " + MA);
                result.put(i, "MA" + att.charAt(0) + length + "_0"); 
            }              
        }       
        //System.out.println("Moving avearge number :" + result.size());
        //System.out.println("===================================================\n");      
        return result;
    }
	 /*	
	 //for Weka
	 public static HashMap<Integer, Double> Move_Average(int length, String att, int att_index, ArrayList<ArrayList<String>> records) {	        
	        HashMap<Integer, Double> result = new HashMap<>(); 
	        int training_data = (int)((records.size()-1)*0.8);  	        
	        
	        //The column of Target
	        int col = att_index;                                                                                                                            
	        for (int i = 1; i < records.size(); i++ ) {       
	            if (i < length) {	                  
	                continue;
	            }	            
	            double sum_t = 0;	           
	            if (i - length + 1 >= 1) {         
	                for (int p_1 = i; p_1 >= i-length+1; p_1--) {                
	                    sum_t = sum_t + Double.parseDouble(records.get(p_1).get(col));
	                } 	
	                result.put(i, sum_t/(double)2);  
	            }          	            	                    
	        }       
	           
	        return result;
	    }*/
    
	public static void featureExtraction(String output_filename, ArrayList<ArrayList<String>> records) {				
		
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		HashMap<Integer, String> FS2 = feature2(1, records);
		HashMap<Integer, String> FT2 = feature2(2, records);
		
		HashMap<Integer, String> FS = feature(1, records);
		HashMap<Integer, String> FT = feature(2, records);		
		HashMap<Integer, String> Match = match_source_target(FS, FT);
		
		HashMap<Integer, String> MAS1_2 = Move_Average(2, records.get(0).get(1), 1, records);		
		HashMap<Integer, String> MAS1_3 = Move_Average(3, records.get(0).get(1), 1, records);
		HashMap<Integer, String> MAT_2 = Move_Average(2, records.get(0).get(2), 2, records);
		HashMap<Integer, String> MAT_3 = Move_Average(3, records.get(0).get(2), 2, records);
		
		for (int i = 0; i < records.size(); i++) {		
			ArrayList<String> temp = new ArrayList<>();
			//Add Date
			temp.add(records.get(i).get(0));
			if(i == 0) {			 
			       //temp.add(records.get(i).get(1));
			       temp.add("Feature_S");
			       temp.add("Feature_T");
			       temp.add("MAS1_2");			     
			       temp.add("MAS1_3");			       			    
			       temp.add("MAT_2");	
			       temp.add("MAT_3");			      
			       temp.add("Match");			      
			} else {
				//All the conditional att need to add. eg. x -> x x_3 x_4
		       
		        	//temp.add(records.get(i).get(1));
		           temp.add(FS2.get(i));
		           temp.add(FT2.get(i));
		           temp.add(MAS1_2.get(i));
		           temp.add(MAS1_3.get(i));		           
		           temp.add(MAT_2.get(i));
		           temp.add(MAT_3.get(i));	
		           temp.add(Match.get(i));			        	     		     
			}
			//Add the last one of every line
			temp.add(records.get(i).get(records.get(i).size()-1));			
			result.add(temp);
		}		
		try {
		writeCSV("", output_filename,result);
		} catch (IOException e) {
			System.out.println("[ERROR] I/O Exception.");
			e.printStackTrace();
		}
	}
	/*
    //weka
    public static void featureExtraction(ArrayList<ArrayList<String>> records) {		      
		String output_filename = "weka.csv";
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		HashMap<Integer, Double> table = Move_Average(2, records.get(0).get(1), 1, records);		
		HashMap<Integer, Double> table1 = Move_Average(3, records.get(0).get(1), 1, records);
		HashMap<Integer, Double> table2 = Move_Average(4, records.get(0).get(1), 1, records);
				
		for (int i = 0; i < records.size(); i++) {		
			ArrayList<String> temp = new ArrayList<>();
			//Add time
			temp.add(records.get(i).get(0));
			if(i == 0) {
			   for (int j = 1; j < records.get(i).size()-1; j++) {			       		     
			       temp.add("MA2");			     
			       temp.add("MA3");			      
			       temp.add("MA4");
			   }	
			   
			} else {				
		        for (int j = 1; j < records.get(i).size()-1; j++) {	
		        	if (table.get(i) == null) {		
		        		temp.add("86.68336");
		        	} else {
		        		temp.add(table.get(i).toString());	
		        	}
		        	if (table1.get(i) == null) {		
		        		temp.add("130.1424");
		        	} else {
		        		temp.add(table1.get(i).toString());	
		        	}
		        	if (table2.get(i) == null) {		
		        		temp.add("173.6877");
		        	} else {
		        		temp.add(table2.get(i).toString());	
		        	}		           
		        }
		                
			}	
			temp.add(records.get(i).get(records.get(i).size()-1));	
			result.add(temp);
		}		
		try {
		writeCSV("", output_filename,result);
		} catch (IOException e) {
			System.out.println("[ERROR] I/O Exception.");
			e.printStackTrace();
		}
	}*/
	
    public static HashMap<Integer, String> featureExtraction_target(ArrayList<ArrayList<String>> records) {
    	HashMap<Integer, String> result = new HashMap<>();
    	int index_of_target_att = records.get(0).size()-1;
    	for (int i = 1; i < records.size(); i++) {
    	    if (i==1) {
    	    	result.put(i, "Rise"); 
    	    	continue;
    	    }
    	    if (Double.parseDouble(records.get(i).get(index_of_target_att))- Double.parseDouble(records.get(i-1).get(index_of_target_att)) > 0 ) {
    	    	result.put(i, "Rise");     
    	    } else {
    	    	result.put(i, "Down");  
    	    }	
    	}    	  
    	return result; 
    }
       
    /*
    public static HashMap<Integer, String> MACD(int sl, int ll, int tl, ArrayList<ArrayList<String>> records) {
    	//System.out.printf("================MACD(sl=%d,ll=%d,tl=%d)==================\n", sl, ll, tl);
    	HashMap<Integer, String> result = new HashMap<>(); 
    	for (int i = 1; i < records.size(); i++) {
    	    double MACD = DIF(i, sl, ll, records) - DEM(i, sl, ll, tl, records);        	
    		if (MACD <= 0) {
    			result.put(i, "Down");
    		} else {
    			result.put(i, "Rise");			
    		}
    	}
    	//System.out.println("Moving avearge number :" + result.size());
        //System.out.println("===================================================\n"); 
    	return result;
    } 
     
    public static double EMA(int t, int l, ArrayList<ArrayList<String>> records, String s) {
    	if (t == 0) {  
    		return 0.0;
    	}
    	int col = 2;
    	double alpha = 2/(double)(l+1);
    	double p = Double.parseDouble(records.get(t).get(col));
        if (s.equals("sl")) {
        	temp_sl.put(0, 0.0);
            temp_sl.put(t, temp_sl.get(t-1) + alpha*(p - temp_sl.get(t-1)));
            return temp_sl.get(t);
        } else {  	
        	temp_ll.put(0, 0.0); 
            temp_ll.put(t, temp_ll.get(t-1) + alpha*(p - temp_ll.get(t-1)));
            return temp_ll.get(t);
        }  
    	
    }
    
    public static double DIF(int t, int sl, int ll, ArrayList<ArrayList<String>> records) {
        return EMA(t, sl, records, "sl") - EMA(t, ll, records, "ll"); 	
    }
    
    public static double DEM(int t, int sl, int ll, int tl, ArrayList<ArrayList<String>> records) {
        return 	(DIF(t, sl, ll, records) + DIF(t-1, sl, ll, records))/(double) tl;
    }*/
        
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
