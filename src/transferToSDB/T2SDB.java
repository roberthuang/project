package transferToSDB;
import java.io.*;
import java.util.*;

public class T2SDB {
	public int translate_training_sliding_window(int next_week, String path, HashMap<Integer, String> class_table, String output) {
	       int SDB_Training_Size = 0;
	       try {
	           ArrayList<ArrayList<String>> records = readCSV(path);
	           int training_data = (int)((records.size()- 1)*0.8);
	           //output
	           File fout = new File(output);
		       FileOutputStream fos = new FileOutputStream(fout);
		       OutputStreamWriter osw = new OutputStreamWriter(fos);   
//		       System.out.println(training_data-window_size+1);
	           for (int i = 1; i <  training_data ; i++) { 
	               for (int p = 0; p < next_week; p++) {
	                   int index = i + p; 
	                   if ((index  < training_data ) && ((i + next_week) < training_data )) {    
	                       for (int k = 1; k < records.get(index).size()-1; k++) {
//	                   	       osw.write("("+ index+ ")"+ " "+records.get(index).get(k) + " ");       
	                           osw.write(records.get(index).get(k) + " "); 	       
	                       }                       
	                       osw.write(-1 + " ");
	                   } 
	               }    
	               //Add Target Class
                   int Target_class_index = i + next_week;
	               if (Target_class_index < training_data ) {
	                   osw.write(class_table.get(Target_class_index) + " "+ -1 + " ");
	                   //Debug
//	                   osw.write(class_table.get(Target_class_index) + "(" + Target_class_index + ")" + " "+ -1 + " ");
	               } else {
	                   break;
	               } 
	               SDB_Training_Size++;
	               osw.write(""+-2);
	               osw.write("\r\n");  
	           }
	           osw.close();        
	       } catch (FileNotFoundException e) {
		       System.out.println("[ERROR] File Not Found Exception.");
		    e.printStackTrace();
		   } catch (IOException e) {
	           System.out.println("[ERROR] I/O Exception.");
	           e.printStackTrace();
	       }        
	       return SDB_Training_Size;
	}
	public int translate_training_sliding_window_next_week_target(int next_week, int window_size, String path, HashMap<Integer, String> class_table, String output) {
	       int SDB_Training_Size = 0;
	       try {
	           ArrayList<ArrayList<String>> records = readCSV(path);
	           int training_data = (int)((records.size()- 1)*0.8);
	           //output
	           File fout = new File(output);
		       FileOutputStream fos = new FileOutputStream(fout);
		       OutputStreamWriter osw = new OutputStreamWriter(fos);   
//		       System.out.println(training_data-window_size+1);
	           for (int i = 1; i <= training_data; i++) { 
	        	   if (i + next_week > records.size()) {
	        		   break;                
	        	   } else {
	        	       int target_index = i + next_week;   
	        		   if (next_week == 0 )
	        	   
	        	   
	               for (int p = 0; p < window_size; p++) {
	                   int index = i + p; 
	                   if ((index  <= training_data) && ((i + window_size) <= training_data)) {    
	                       for (int k = 1; k < records.get(index).size()-1; k++) {
//	                   	       osw.write("("+ index+ ")"+ " "+records.get(index).get(k) + " ");       
	                           osw.write(records.get(index).get(k) + " "); 	       
	                       }                       
	                       osw.write(-1 + " ");
	                   } 
	               }    
	               //Add Target Class
                int Target_class_index = i + window_size;
	            if (Target_class_index <= training_data) {
	                osw.write(class_table.get(Target_class_index) + " "+ -1 + " ");
	                //Debug
//	                osw.write(class_table.get(Target_class_index) + "(" + Target_class_index + ")" + " "+ -1 + " ");
	            } else {
	                   break;
	            } 
	            SDB_Training_Size++;
	            osw.write(""+-2);
	            osw.write("\r\n"); 
	            
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
	       return SDB_Training_Size;
	}
    public int translate_training(int window_size, int next_week, String path, HashMap<Integer, String> class_table, String output) {
       int SDB_Training_Size = 0;
       try {
           ArrayList<ArrayList<String>> records = readCSV(path);
           int training_data = (int)((records.size() - 1)*0.8);
           
           //output
           File fout = new File(output);
	       FileOutputStream fos = new FileOutputStream(fout);
	       OutputStreamWriter osw = new OutputStreamWriter(fos);   
//	       System.out.println(training_data-window_size+1);
           for (int i = 1; i <= training_data-window_size+1; i += next_week) { 
                       	
                   for (int j = 0; j < next_week-window_size+1; j++) {
                           
                       for (int p = 0; p < window_size; p++) {
                    	   int index = i + j; 
                    	   index+=p;
                    	   
                           if ((index  < training_data) && ((i + next_week) < training_data)) {    
                               for (int k = 1; k < records.get(index).size()-1; k++) {
//                   	           osw.write("("+ index+ ")"+ " "+records.get(index).get(k) + " ");       
                        	       osw.write(records.get(index).get(k) + " "); 
                        	       
                               }                       
                               osw.write(-1 + " ");
                           } 
                       }
                       //Add Target Class
                       int Target_class_index = i + next_week;
                       if (Target_class_index < training_data) {
                           osw.write(class_table.get(Target_class_index) + " "+ -1 + " ");
                           //Debug
//                         osw.write(class_table.get(Target_class_index) + "(" + Target_class_index + ")" + " "+ -1 + " ");
                       } else {
                    	   break;
                       }
                       osw.write(""+-2);
                       osw.write("\r\n");  
                       SDB_Training_Size++;
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
       return SDB_Training_Size;
   }
    //weka
    public int translate_training_sliding_window_weka(int next_week, String path, HashMap<Integer, String> class_table, String output) {
	       int SDB_Training_Size = 0;
	       try {
	           ArrayList<ArrayList<String>> records = readCSV(path);
	           int training_data = (int)((records.size()- 1)*0.8);
	           //output
	           File fout = new File(output);
		       FileOutputStream fos = new FileOutputStream(fout);
		       OutputStreamWriter osw = new OutputStreamWriter(fos);   
		       
		       //Write Title
	 	        int size = next_week*(records.get(0).size()-2);
		        for (int i = 1; i <= size; i++) {
		        	osw.write("A" + i  + ", ");    	
		        }   	
		        osw.write("Target");  
		        osw.write("\r\n");   
//		       System.out.println(training_data-window_size+1);
	           for (int i = 1; i < records.size(); i++) { 
	               for (int p = 0; p < next_week; p++) {
	                   int index = i + p; 
	                   if ((index  <records.size()) && ((i + next_week) <records.size())) {    
	                       for (int k = 1; k < records.get(index).size()-1; k++) {
//	                   	       osw.write("("+ index+ ")"+ " "+records.get(index).get(k) + " ");       
	                           osw.write(records.get(index).get(k) + ", "); 	       
	                       }                       
	                       //osw.write(-1 + " ");
	                   } 
	               }    
	               //Add Target Class
                int Target_class_index = i + next_week;
	               if (Target_class_index <records.size()) {
	                   osw.write(class_table.get(Target_class_index));
	                   //Debug
//	                   osw.write(class_table.get(Target_class_index) + "(" + Target_class_index + ")" + " "+ -1 + " ");
	               } else {
	                   break;
	               } 
	               SDB_Training_Size++;
	             
	               osw.write("\r\n");  
	           }
	           osw.close();        
	       } catch (FileNotFoundException e) {
		       System.out.println("[ERROR] File Not Found Exception.");
		    e.printStackTrace();
		   } catch (IOException e) {
	           System.out.println("[ERROR] I/O Exception.");
	           e.printStackTrace();
	       }        
	       return SDB_Training_Size;
	}
    
    public void translate_training_weka(int next_week, String path, HashMap<Integer, String> class_table, String output) {
        try {
            ArrayList<ArrayList<String>> records = readCSV(path);
            int training_data = (int)((records.size() - 1)*0.8);
                       
            //output
            File fout = new File(output);
 	        FileOutputStream fos = new FileOutputStream(fout);
 	        OutputStreamWriter osw = new OutputStreamWriter(fos);   
// 	        System.out.println(training_data-window_size+1);
 	        
 	        //Write Title
 	        int size = next_week*(records.get(0).size()-1);
	        for (int i = 1; i <= size; i++) {
	        	osw.write("A" + i  + ", ");    	
	        }   	
	        osw.write("Target");  
	        osw.write("\r\n");   	         	         	         	        
            for (int i = 1; i <= training_data; i += next_week) { 
                        for (int p = 0; p < next_week; p++) {
                     	    int index = i + p;
                            if ((index  <= training_data) && ((i + next_week) <= training_data)) {    
                                for (int k = 1; k < records.get(index).size(); k++) {
//                    	           osw.write("("+ index+ ")"+records.get(index).get(k) + ", ");       
                         	       osw.write(records.get(index).get(k) + ", "); 
                                }                                                      
                            } 
                        }
                        //Add Target Class
                        int Target_class_index = i + next_week;
                        if (Target_class_index <= training_data) {
                            osw.write(class_table.get(Target_class_index));              
//                          osw.write("("+ Target_class_index + ")"+class_table.get(Target_class_index));
                        } else {
                     	   break;
                        }               
                        osw.write("\r\n");                                                                                                                     
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
    
    public int translate_testing_sliding_window(int next_week, String path, String output) {
    	    int SDB_Testing_Size = 0;
    	try {
        	
            ArrayList<ArrayList<String>> records = readCSV(path);                          
            int training_data = (int)((records.size()-1)*0.8);       
            
            //output
            File fout = new File(output);
 	        FileOutputStream fos = new FileOutputStream(fout);
 	        OutputStreamWriter osw = new OutputStreamWriter(fos);       
// 	        System.out.println(training_data + 1);
            for (int i = training_data + 1; i < records.size()-next_week; i++) {
//         	   System.out.println(i);
                for (int j = 0; j < next_week;j++) {
                    int index = i + j; 
                    if (index < records.size()) {           
                 	   
                 	   for (int k = 1;k < records.get(i).size()-1; k++) {
                     	   osw.write(records.get(index).get(k) + " ");    
//                     	   osw.write("("+ index+ ")"+records.get(index).get(k) + " ");       
                       }                       
                        osw.write(-1 + " ");                       
                        
                    } else {
                 	
                 	   break;
                    }
                }                 
                osw.write(""+-2);
                osw.write("\r\n");    
                SDB_Testing_Size++;
            }
            osw.close(); 
            //System.out.println("Testing Data's window number: " + (records.size()- 1 - training_data) );
            //System.out.println("===================================================\n");   
        } catch (FileNotFoundException e) {
 	       System.out.println("[ERROR] File Not Found Exception.");
 	       e.printStackTrace();
 	   } catch (IOException e) {
            System.out.println("[ERROR] I/O Exception.");
            e.printStackTrace();
        }  
        return SDB_Testing_Size;
    } 
    
    
   public void translate_testing(int next_week, String path, String output) {
       try {
    	   
           ArrayList<ArrayList<String>> records = readCSV(path);                          
           int training_data = (int)((records.size()-1)*0.8);       
           
           //output
           File fout = new File(output);
	       FileOutputStream fos = new FileOutputStream(fout);
	       OutputStreamWriter osw = new OutputStreamWriter(fos);           
//	       System.out.println(training_data + 1);
           for (int i = training_data + 1; i < records.size()-next_week; i += next_week) {
//        	   System.out.println(i);
               for (int j = 0; j < next_week;j++) {
                   int index = i + j; 
                   
                   if (index < records.size()) {           
                	   
                	   for (int k = 1;k < records.get(i).size()-1; k++) {
                    	   osw.write(records.get(index).get(k) + " ");        	                     	   
                       }                       
                       osw.write(-1 + " ");                       
                       
                   } else {
                	
                	   break;
                   }
               }                 
               osw.write(""+-2);
               osw.write("\r\n");             
           }
           osw.close(); 
           //System.out.println("Testing Data's window number: " + (records.size()- 1 - training_data) );
           //System.out.println("===================================================\n");   
       } catch (FileNotFoundException e) {
	       System.out.println("[ERROR] File Not Found Exception.");
	       e.printStackTrace();
	   } catch (IOException e) {
           System.out.println("[ERROR] I/O Exception.");
           e.printStackTrace();
       }  
   } 
   //weka
   public int translate_testing_sliding_window_weka(int next_week, String path, HashMap<Integer, String> class_table, String output) {
	       int SDB_Training_Size = 0;
	       try {
	           ArrayList<ArrayList<String>> records = readCSV(path);
	           int training_data = (int)((records.size()- 1)*0.8);
	           //output
	           File fout = new File(output);
		       FileOutputStream fos = new FileOutputStream(fout);
		       OutputStreamWriter osw = new OutputStreamWriter(fos);   
//		       System.out.println(training_data-window_size+1);
		     //Write Title
		        int size = next_week*(records.get(0).size()-1);
		        for (int i = 1; i <= size; i++) {
		        	osw.write("A" + i  + ", ");    	
		        }   	
		        osw.write("Target");  
		        osw.write("\r\n");  
	           for (int i = training_data+1; i < records.size(); i++) { 
	               for (int p = 0; p < next_week; p++) {
	                   int index = i + p; 
	                   if ((index  < records.size()) && ((i + next_week) <records.size())) {    
	                       for (int k = 1; k < records.get(index).size()-1; k++) {
//	                   	       osw.write("("+ index+ ")"+ " "+records.get(index).get(k) + " ");       
	                           osw.write(records.get(index).get(k) + ", "); 	       
	                       }                       
	                       //osw.write(-1 + " ");
	                   } 
	               }    
	               //Add Target Class
               int Target_class_index = i + next_week;
	               if (Target_class_index < records.size()) {
	                   osw.write(class_table.get(Target_class_index));
	                   //Debug
//	                   osw.write(class_table.get(Target_class_index) + "(" + Target_class_index + ")" + " "+ -1 + " ");
	               } else {
	                   break;
	               } 
	               SDB_Training_Size++;
	             
	               osw.write("\r\n");  
	           }
	           osw.close();        
	       } catch (FileNotFoundException e) {
		       System.out.println("[ERROR] File Not Found Exception.");
		    e.printStackTrace();
		   } catch (IOException e) {
	           System.out.println("[ERROR] I/O Exception.");
	           e.printStackTrace();
	       }        
	       return SDB_Training_Size;
	}
   
   
   public void translate_testing_weka(int next_week, String path, HashMap<Integer, String> class_table, String output) {
       try {
           ArrayList<ArrayList<String>> records = readCSV(path);
           int training_data = (int)((records.size() - 1)*0.8);
                      
           //output
           File fout = new File(output);
	       FileOutputStream fos = new FileOutputStream(fout);
	       OutputStreamWriter osw = new OutputStreamWriter(fos);   
//	       System.out.println(training_data-window_size+1);
	        
	       //Write Title
	       int size = next_week*(records.get(0).size()-1);
	       for (int i = 1; i <= size; i++) {
	    		   osw.write("A" + i + ", "); 
	       }   	 
	       osw.write("Target")
	       ;  
	       osw.write("\r\n");     
	       
	       for (int i = 1+training_data; i < records.size(); i += next_week) { 
               for (int p = 0; p < next_week; p++) {
            	    int index = i + p;
                   if ((index  < records.size()) && ((i + next_week) < records.size())) {    
                       for (int k = 1; k < records.get(index).size(); k++) {
//           	           osw.write("("+ index+ ")"+records.get(index).get(k) + ", ");       
                	       osw.write(records.get(index).get(k) + ", "); 
                       }                                                      
                   } 
               }
               //Add Target Class
               int Target_class_index = i + next_week;
               if (Target_class_index < records.size()) {
                   osw.write(class_table.get(Target_class_index));              
//                   osw.write("("+ Target_class_index + ")"+class_table.get(Target_class_index));
               } else {
            	   break;
               }               
               osw.write("\r\n");                                                                                                                     
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
   
   
}
