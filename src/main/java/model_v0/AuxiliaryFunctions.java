package model_v0;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import instance.Commodity;
import instance.Instance;

public class AuxiliaryFunctions {
	private static int maxKv;								  //the maximum capacity of vessels whatever the vessel type
	private static int  sumK;								 //the number of TEU for all the commodities
	private static Set<Integer> allLoads;				    //all possible loads	
	private static Set<Integer> totalLoad;				   //all the possible values for the sum of the load entering port p and the load leaving port p for a given rotation
	private static List<Integer> rev;          			  //list to store revenue for each commodity
	
	
    /**
     * @param infra list of infrastructure
     * @param nb_canals the number of canals
     * @param nb_ports the number of port
     * @return returns the lists of possible successors for each port/canal in infra
     */
    public static List<List<Integer>> successorLists(List<String> infra, int nb_canals,int nb_ports) {
        List<List<Integer>> L = new ArrayList<>();
        for (int p = 0; p < infra.size(); p++) {
            L.add(new ArrayList<>());
            for (int sp = 0; sp < infra.size(); sp++) {
                if (sp != p && (p < nb_ports || sp < nb_ports || Math.abs(sp - p) != nb_canals)) {
                    L.get(p).add(sp);
                }
            }
        }

        return L;
    }
    

    /**
     * @param quantity  the quantity for each commodity
     * @return eturns all the possible loads obtained by combining commodities
     */
    public static Set<Integer> computeAllPossibleLoads(List<Integer>quantity) {

        Set<Integer> values = new HashSet<>();
        values.add(0);

        for (int i = 1; i <= quantity.size(); i++) {
            for (List<Integer> combi : combinations(quantity, i)) {
                values.add(combi.stream().mapToInt(Integer::intValue).sum());
            }
        }

        return values;
    }
    private static List<List<Integer>> combinations(List<Integer> list, int length) {
        List<List<Integer>> result = new ArrayList<>();
        combinationsHelper(list, length, result, new ArrayList<>(), 0);
        return result;
    }

    private static void combinationsHelper(List<Integer> list, int length, List<List<Integer>> result,
            List<Integer> current, int index) {
        if (current.size() == length) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = index; i < list.size(); i++) {
            current.add(list.get(i));
            combinationsHelper(list, length, result, current, i + 1);
            current.remove(current.size() - 1);
        }
    }
    
    
    /**
     * @param val_min minimum value
     * @param val_max maximum value
     * @return  returns the largest unit we can use for value between val_min and val_max
     */
    public static int computeUnit(int val_min, int val_max) {

        int unit = 1;
        if (val_min == 0) {
            return 1;
        }
        while (val_min % (unit * 10) == 0) {
            unit = unit * 10;
        }
        int unit_min = (int) (Math.log10(val_min));
        int unit_max = (int) (Math.log10(val_max));
        if (unit_min == unit_max) {
            return Math.max(unit, (int) Math.pow(10, unit_min - 1));
        } else {
            return Math.max(unit, (int) Math.pow(10, unit_min - 1));
        }
    }
    
    
            
        /**
         * @param instance the instance 
         * @param infra   list of infrastructure
         * @param successors lists of possible successors for each port/canal in infra
         * @param speed_min  minimum speed
         * @param speed_max  maximum speed
         * @param speed_step step of speed 
         * @param hmax  the maximal number of hours that a rotation may last
         * @return returns  all the possible sailing time
         */
        public  static List<Tuples> computeAllSailingTimes(Instance instance ,List<String> infra, List<List<Integer>> successors,int speed_min, int speed_max,int speed_step, int hmax) {
            List<Tuples> tableSailingTime = new ArrayList<>();
            for (int p = 0; p < infra.size(); p++) {
                Tuples tuples = new Tuples(true);
                tuples.add(p, 0, 0);
                for (int sp : successors.get(p)) {
                    int distance = instance.getDistances().getDistance(infra.get(p), infra.get(sp));
                    if (distance > 0) {
                        for (int speed = speed_min; speed <= speed_max; speed += speed_step) {
                            double time = distance / (double) speed;
                            if (time <= hmax*24) {
                                if (0 <= time - (int) time && time - (int) time <= 0.5) {
                                    tuples.add(sp, speed, (int) time);
                                } else {
                                    tuples.add(sp, speed, (int) time + 1);
                                }
                            }
                        }
                    }
                }
                tableSailingTime.add(tuples);
            }
            return tableSailingTime;
        }
        
        
        
        
        
        /**
         * @param instance the instance 
         * @param Vtype  vessel type
         * @param speed  speed
         * @return returns the consumption for a given vessel type and a given speed 
         */
        public static int computeConsumption(Instance instance,int Vtype,int speed) {
            ArrayList<Integer> designSpeed = new ArrayList<>();
            ArrayList<Float> designConsumption = new ArrayList<>();
            
            for (String name:instance.getVesselTypes()) {
            	for(String name_fuel:instance.getFuelTypes()) {
                if(instance.getVesselType(name).getDesignSpeed(name_fuel)!=0) {
                	designSpeed.add(instance.getVesselType(name).getDesignSpeed(name_fuel));

                }
                if(instance.getVesselType(name).getDesignConsumption(name_fuel)!=0.0) {
                	designConsumption.add(instance.getVesselType(name).getDesignConsumption(name_fuel));

                }


            }

        

            }
        	
            double  cons= Math.pow((double)speed/designSpeed.get(Vtype), 3) * designConsumption.get(Vtype)*10;

        	return (int) cons;
        }
     
        public static void ports(Instance instance) {
        	List<Integer> pol = new ArrayList<>();    	//origin  ports of commodity
            List<Integer> pod = new ArrayList<>();    	// destination ports of commodity

            //origin and destination ports of commodity
            for (Commodity c: instance.getCommodities()) {
            	List<String> portCodes = new ArrayList<>(instance.getPorts());  
         	    pol.add(portCodes.indexOf(c.getPol().toString()));
        	    pod.add(portCodes.indexOf(c.getPod().toString()));
            	
            }
        	
        }
        
        
    	/**
    	 * @return returns origin  ports of commodity
    	 */
    	public static List<Integer> getPol(Instance instance) {
        	List<Integer> pol = new ArrayList<>();    	//origin  ports of commodity
            //origin ports of commodity
            for (Commodity c: instance.getCommodities()) {
            	List<String> portCodes = new ArrayList<>(instance.getPorts());  
         	    pol.add(portCodes.indexOf(c.getPol().toString()));
            	
            }
    		return pol;
    	}





    	/**
    	 * @return returns destination ports of commodity
    	 */
    	public static List<Integer> getPod(Instance instance) {
    		
            List<Integer> pod = new ArrayList<>();    	// destination ports of commodity

            // destination ports of commodity
            for (Commodity c: instance.getCommodities()) {
            	List<String> portCodes = new ArrayList<>(instance.getPorts());  
        	    pod.add(portCodes.indexOf(c.getPod().toString()));
            	
            }
    		return pod;
    	}

    	
    	/**
    	 * @param instance the instance 
    	 * @return returns the quantity of commodities
    	 */
    	public static  List<Integer> getQuantity(Instance instance) {
    		List<Integer> quantity= new ArrayList<Integer>();
  	      for (int i =0;i<instance.getCommodities().size();i++) {
  	    	  quantity.add(instance.getCommodities().get(i).getNumber());  	  
  	      }
		return quantity;
    		
    	}

    	/**
    	 * @param instance the instance 
    	 * @return  returns the maximum capacity of vessels
    	 */
		public static int getMaxKv(Instance instance) {
			
			   // the maximum capacity of vessels whatever the vessel type
	        List<Integer> MaximalCapacity=new ArrayList<Integer>();
	        for (String name:instance.getVesselTypes()) {
	        	MaximalCapacity.add(instance.getVesselType(name).getMaximalCapacity());
	        }
	         maxKv=Collections.max(MaximalCapacity);
		        List<Integer> NumberTeu=new ArrayList<Integer>();
		        for (Commodity commodity:instance.getCommodities()) {
		        	NumberTeu.add(commodity.getNumber());
		        	
		        }	
			return maxKv;
		}

		
    	/**
    	 * @param instance the instance 
    	 * @return returns the number of TEU for all the commodities
    	 */
		public static int getSumK(Instance instance) {
			
	        List<Integer> NumberTeu=new ArrayList<Integer>();
	        for (Commodity commodity:instance.getCommodities()) {
	        	NumberTeu.add(commodity.getNumber());
	        	
	        }	 
	        
	        //the number of TEU for all the commodities
		    sumK = NumberTeu.stream().mapToInt(Integer::intValue).sum();
			return sumK;
		}


    	/**
    	 * @param instance the instance 
    	 * @return returns all possible loads
    	 */
		public static Set<Integer> getAllLoads(Instance instance) {
		       allLoads = computeAllPossibleLoads(getQuantity(instance));

			return allLoads;
		}

		
		
		
    	/**
    	 * @param instance the instance 
    	 * @return returns all the possible values for the sum of the load entering port p and the load leaving port p for a given rotation

    	 */
		public static Set<Integer> getTotalLoad(Instance instance) {
	        //we compute all the possible production times
	        totalLoad = new HashSet<Integer>();//all the possible values for the sum of the load entering port p and the load leaving
	        totalLoad.add(0);
	        for (int load1 : getAllLoads( instance)) {
	            for (int load2 : getAllLoads( instance)) {
	                if (load1 != load2) {
	                    int sum = load1 + load2;
	                    if (sum <= 2 * getMaxKv(instance)) {
	                        totalLoad.add(sum);
	                    }
	                }
	            }
	        }       
			
			return totalLoad;
		}
    	


    	/**
    	 * @param instance the instance 
    	 * @return returns list of revenue for each commodity


    	 */
		public static List<Integer> getRev(Instance instance) {
   		 List<Integer> quantity=getQuantity(instance);		    //list to store quantity for each commodity 
   		 List<Integer> revenues=new ArrayList<Integer> ();		//list to store the total revenue for each commodity  (rev*quantity)
   		 rev=new ArrayList<Integer> ();
		       // we get the information about revenue
				for(int k = 0; k < instance.getCommodities().size(); k++){
				    rev.add(instance.getCommodities().get(k).getRevenue());
				    quantity.add(instance.getCommodities().get(k).getNumber());
				    revenues.add( rev.get(k) * quantity.get(k));

				}
			return rev;
		}
    	
		
    	/**
    	 * @param filename the file name
    	 * @param data the data to add to the file 
    	 * @param position the position where we will insert the data
    	 */
		public static void InsertLineToFile(String filename,String data,int position) throws IOException
		{
	        String newLine = data;

	        // Read the contents of the file into a list of strings
	        Path path = Paths.get(filename);
	        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

	        // Insert the new line at index 'position'
	        lines.add(position, newLine);

	        // Write the modified lines back to the file
	        Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		}
	
	/**
	 * @param model the model
	 * @param filename the file name
	 * write the result of the model to a file
	 */
	public static void save(Model model,String filename) throws IOException {
		String path=filename;
	    FileWriter writer = new FileWriter(path);
        List<String>variablesList=new ArrayList<String>();
        String objective="";
        String optimal="";
		 optimal="r OPTIMAL";
   for (Variable var : model.getChocoModel().getVars()) {
        if (var instanceof IntVar) {
              IntVar intVar = (IntVar) var;

        	if(var.getName().equals("objective")) {
	               System.out.println(intVar.getName() + " = " + intVar.getValue() + "\n");
	               objective="o "+ intVar.getValue();
        		
        	    }else if ( !var.getName().startsWith("REIF_") && !var.getName().startsWith("aux_") && !var.getName().startsWith("IV_") &&  !var.getName().startsWith("not") && !var.getName().startsWith("cste") && !var.getName().startsWith("nLoops")){
        		
           if (intVar.getName().contains("[")) {
         	   String newName;
         	   newName= intVar.getName().substring(0, intVar.getName().indexOf("["));
	               Object obj=model.getVariable(newName);
	               if (obj instanceof IntVar[]) {
	            	    int size = ((IntVar[]) obj).length; 			            	    
	            	    variablesList.add(newName+"["+ size+"]") ;
		                writer.write("s "+intVar.getName()+" " + intVar.getValue()+"\n"); 

	            	   }

	               if (obj instanceof IntVar[][]) {
	            	    int n = ((IntVar[][]) obj).length;
	            	    int m = ((IntVar[][]) obj)[0].length;
	            	       variablesList.add(newName+"["+ n+"]"+"["+m+"]");
			               writer.write("s "+intVar.getName()+" " + intVar.getValue()+"\n"); 



	            	}
	               if (obj instanceof IntVar[][][]) {
            	    int n = ((IntVar[][][]) obj).length;
            	    int m = ((IntVar[][][]) obj)[0].length;
            	    int k = ((IntVar[][][]) obj)[0][0].length;
            	    	   variablesList.add(newName+"["+ n+"]"+"["+m+"]"+"["+k+"]");
			               writer.write("s "+intVar.getName()+" " + intVar.getValue()+"\n"); 


            	}
	               if (obj instanceof BoolVar[][]) {
            	    int n = ((BoolVar[][]) obj).length;
            	    int m = ((BoolVar[][]) obj)[0].length;
            	           variablesList.add(newName+"["+ n+"]"+"["+m+"]");
			               writer.write("s "+intVar.getName()+" " + intVar.getValue()+"\n"); 


            	}
	               if (obj instanceof BoolVar[][][]) {
            	    int n = ((BoolVar[][][]) obj).length;
            	    int m = ((BoolVar[][][]) obj)[0].length;
            	    int k = ((BoolVar[][][]) obj)[0][0].length;
            	           variablesList.add(newName+"["+ n+"]"+"["+m+"]"+"["+k+"]");
			               writer.write("s "+intVar.getName()+" " + intVar.getValue()+"\n"); 



            	}
	               
           
           }


        	}
    }

    }		            
        writer.close();

        List<String> uniqueList = variablesList.stream().distinct().collect(Collectors.toList());
        String result = "v "+String.join(" ", uniqueList);
        InsertLineToFile(path, optimal, 0);
        InsertLineToFile(path, objective, 1);
       InsertLineToFile(path, result, 2);
		
	}
}
