package model_v0;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.chocosolver.solver.variables.IntVar;

public class FeatureSubmodel extends Submodel{
	
	/**  This class allows to represent a part of model related to features of rotations */

	private IntVar[] fpR;								//the price of the fuel used for rotation r
	private IntVar[] kR;								//the real capacity of vessels used for rotation r
	private IntVar[] tcR;								//the  time charter rate per day for rotation r
	private IntVar[][]pcPr;								//the fixed port call cost of port p (p ∈ P ∪ C) for rotation r
	private IntVar[][]wtPr;						    	//the waiting time for port p (p ∈ P ∪ C) and rotation r 	
	private IntVar[][] manInPr ;                    	//the manoeuvring time for entering port p for rotation r 
    private IntVar[][] manOutPr ;						//the manoeuvring time for leaving port p for rotation r
    private IntVar[][] prodPr ;					    	//the productivity (in number of moves per hour) of port p for rotation r

	private IntVar[] speedRMin ;    					//the minimal speed for each rotation r
	private IntVar[] speedRMax ;    					//the maximal speed for each rotation r
	
	private ArrayList<Integer> capacity;         		//stores the capacities of vessels
	private ArrayList<Integer> fuelCosts;       		//stores the fuel costs of vessels
    private ArrayList<ArrayList<Integer>> callCost ;	//stores the fixed port call costs
	private ArrayList<Integer> timeCharterRate;  		//stores the time charter rates of vessels
    private ArrayList<ArrayList<Integer>> waitingTime;	//stores the waiting times of vessels

    private ArrayList<ArrayList<Integer>> manIn;    	//stores the manoeuvring times for entering ports
    private ArrayList<ArrayList<Integer>> manOut;   	//stores the manoeuvring times for leaving ports
	
	private List<Integer> speeds_min;    				//stores the minimal speeds of vessels
	private List<Integer> speeds_max;   		    	//stores the maximal speeds of vessels
	
	private ArrayList<ArrayList<Integer>> prodi;		//stores the productivity of ports
	
	private boolean  withRefinedDomains ;           	//true if the model used refined domains, False otherwise

	
	
	
	/**
	 * Constructor for creating a FeatureSubmodel object
	 * @param model The model object 
	 */
	public FeatureSubmodel(Model model) {
		super(model);
		this.withRefinedDomains=model.isWithRefinedDomains();
		
        //we get the information about capacities
			capacity = new ArrayList<>();
	        capacity.add(0);
	        for (String name:getModel().getInstance().getVesselTypes()) {
	      	  capacity.add(getModel().getInstance().getVesselType(name).getMaximalCapacity());
	
	        }
        
       //we get the information about fuel costs
	        fuelCosts= new ArrayList<>();
	        fuelCosts.add(0);
	        for(String name : getModel().getInstance().getVesselTypes()) {
	            Set<String> fueltype=getModel().getInstance().getVesselType(name).getFuelType();
	            String labelFuel="";
	            for(String valeur : fueltype) {
	            	labelFuel=valeur;
	            }
	        	int cost=getModel().getInstance().getFuelType(labelFuel).getCost()/10;
	        	fuelCosts.add(cost);
	        }

       //we get the information about fixed port call costs
        
	        callCost=new ArrayList<ArrayList<Integer>>();
	
	        for (String name:getModel().getInstance().getPorts()) {
	            ArrayList<Integer> innerList = new ArrayList<Integer>();
	            innerList.add(0);
	        	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
	                innerList.add(getModel().getInstance().getPort(name).getCallCost(nameVessel));
	
	            }
	        	  callCost.add(innerList);
	        }
	        for (String name:getModel().getInstance().getCanals()) {
	            ArrayList<Integer> innerList = new ArrayList<Integer>();
	            innerList.add(0);
	        	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
	        		  innerList.add(getModel().getInstance().getCanal(name).getCanalCost(nameVessel));
	
	            }
	        	  callCost.add(innerList);
	        }
	        
	        for (String name:getModel().getInstance().getCanals()) {
	            ArrayList<Integer> innerList = new ArrayList<Integer>();
	            innerList.add(0);
	        	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
	        		  innerList.add(getModel().getInstance().getCanal(name).getCanalCost(nameVessel));
	
	            }
	        	  callCost.add(innerList);
	        }
        
        //we get the information about time Charter Rate
	        timeCharterRate = new ArrayList<>();
	        timeCharterRate.add(0);
	        
	        for (String name:getModel().getInstance().getVesselTypes()) {
	      	  timeCharterRate.add((Integer) getModel().getInstance().getVesselType(name).getRate());
	        }
	        
        
        //we get the information about waiting Time
	        waitingTime = new ArrayList<ArrayList<Integer>>();
	
	        for (String name:getModel().getInstance().getPorts()) {
	            ArrayList<Integer> innerList = new ArrayList<Integer>();
	            innerList.add(0);
	        	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
	                innerList.add(getModel().getInstance().getPort(name).getWaitingTime(nameVessel));
	
	            }
	        	  waitingTime.add(innerList);
	        }
	        for (String name:getModel().getInstance().getCanals()) {
	            ArrayList<Integer> innerList = new ArrayList<Integer>();
	            innerList.add(0);
	        	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
	        		  innerList.add(getModel().getInstance().getCanal(name).getWaitingTime(nameVessel));
	
	            }
	        	  waitingTime.add(innerList);
	        }
	        for (String name:getModel().getInstance().getCanals()) {
	            ArrayList<Integer> innerList = new ArrayList<Integer>();
	            innerList.add(0);
	        	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
	        		  innerList.add(getModel().getInstance().getCanal(name).getWaitingTime(nameVessel));
	
	            }
	        	  waitingTime.add(innerList);
	        }

        
        //the manoeuvring time for entering ports
	         manIn = new ArrayList<>();
	         for (String name:getModel().getInstance().getPorts()) {
	         ArrayList<Integer> innerList = new ArrayList<Integer>();
	         innerList.add(0);
	     	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
	     		  innerList.add(getModel().getInstance().getPort(name).getManoeuvringTimeIn(nameVessel));
	
	         }
	     	  manIn.add(innerList);
	     }
        
        //the manoeuvring time for leaving ports
	        manOut = new ArrayList<>();
	        for (String name:getModel().getInstance().getPorts()) {
	        ArrayList<Integer> innerList = new ArrayList<Integer>();
	        innerList.add(0);
	    	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
	    		  innerList.add(getModel().getInstance().getPort(name).getManoeuvringTimeOut(nameVessel));
	
	        }
	    	  manOut.add(innerList);
	    }
        
        //the minimal and maximal speed for each rotation r
	        speeds_min = new ArrayList<>();
	        speeds_max = new ArrayList<>();
	
	        speeds_min.add(0); 
	        speeds_max.add(0); 
	
	        for (String name:getModel().getInstance().getVesselTypes()) {
	     	   speeds_min.add(getModel().getInstance().getVesselType(name).getMinimalSpeed());
	     	   speeds_max.add(getModel().getInstance().getVesselType(name).getMaximalSpeed());
	
	        }

        //the productivity of ports
	        if ( ! withRefinedDomains){
	        prodi = new ArrayList<>();
	        for (String name:getModel().getInstance().getPorts()) {
	        ArrayList<Integer> innerList = new ArrayList<Integer>();
	        innerList.add(-1);
	    	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
	    		  Integer val=(getModel().getInstance().getPort(name).getProductivity(nameVessel));
	    		  innerList.add(val*100);
	
	        }
	    	  prodi.add(innerList);
	    }   }   

	}

	@Override
	public void defineVariables() {

        
       //the price of the fuel used for rotation r (0 if rotation r is not used)
        fpR = getModel().getChocoModel().intVarArray("fp_r", nbRotations, fuelCosts.stream().mapToInt(i -> i).toArray());
        addVariables("fp_r", fpR);
        
       //the real capacity of vessels used for rotation r (0 if rotation r is not used)
        kR = getModel().getChocoModel().intVarArray("k_r", nbRotations, capacity.stream().mapToInt(i -> i).toArray());
        addVariables("k_r", kR);
        
        //the fixed port call cost of port/canal p for rotation r (0 if rotation r is not used)
        pcPr = getModel().getChocoModel().intVarMatrix("pc_pr", nbInfra,nbRotations,callCost.stream().flatMap(innerList -> innerList.stream()).mapToInt(Integer::intValue).toArray());
        addVariables("pc_pr", pcPr);

        //the time charter rate per day for rotation r (0 if rotation r is not used)
        tcR= getModel().getChocoModel().intVarArray("tc_r", nbRotations, timeCharterRate.stream().mapToInt(i -> i).toArray());
        addVariables("tc_r", tcR);

        //the waiting time for port/canal p and rotation r (0 if rotation r is not used)
        wtPr = getModel().getChocoModel().intVarMatrix("wt_pr", nbInfra,nbRotations,waitingTime.stream().flatMap(innerList -> innerList.stream()).mapToInt(Integer::intValue).toArray());
        addVariables("wt_pr", wtPr);
        
        //the manoeuvring time for entering port p for rotation r (0 if rotation r is not used)
         manInPr = new IntVar[nbInfra][nbRotations];

        for (int i = 0; i < nbInfra; i++) {
            for (int j = 0; j < nbRotations; j++) {
                if (i < nbPorts) {
                    manInPr[i][j] = getModel().getChocoModel().intVar("manIn_pr[" + i + "][" + j + "]", manIn.get(i).stream().mapToInt(Integer::intValue).toArray());
                } else {
                    manInPr[i][j] = getModel().getChocoModel().intVar("manIn_pr[" + i + "][" + j + "]", 0);
                }
            }
        }
        addVariables("manIn_pr", manInPr);
        
        //the manoeuvring time for leaving port p for rotation r (0 if rotation r is not used)
        manOutPr = new IntVar[nbInfra][nbRotations];

        for (int i = 0; i < nbInfra; i++) {
            for (int j = 0; j < nbRotations; j++) {
                if (i < nbPorts) {
                    manOutPr[i][j] = getModel().getChocoModel().intVar("manOut_pr[" + i + "][" + j + "]", manOut.get(i).stream().mapToInt(Integer::intValue).toArray());
                } else {
              	  manOutPr[i][j] = getModel().getChocoModel().intVar("manOut_pr[" + i + "][" + j + "]", 0);
                }
            }
        }
        addVariables("manOut_pr", manOutPr);
        
                
        //the productivity (in number of moves per hour) of port p for rotation r (0 if rotation r is not used)
       prodPr = new IntVar[nbPorts][nbRotations];

        for (int i = 0; i < nbPorts; i++) {
            for (int j = 0; j < nbRotations; j++) {
                prodPr[i][j] = getModel().getChocoModel().intVar("prod_pr[" + i + "][" + j + "]", prodi.get(i).stream().mapToInt(Integer::intValue).toArray());
            }
        }        
        addVariables("prod_pr", prodPr);

        //the minimal speed for each rotation r
       speedRMin = getModel().getChocoModel().intVarArray("speed_r_min", nbRotations, speeds_min.stream().mapToInt(i -> i).toArray());
       addVariables("speed_r_min", speedRMin);

        //the maximal speed for each rotation r
        speedRMax = getModel().getChocoModel().intVarArray("speed_r_max", nbRotations, speeds_max.stream().mapToInt(i -> i).toArray());
        addVariables("speed_r_max", speedRMax);

        
        
        
	}

	@Override
	public void defineConstraints() {
		
		
	    //the type of fuel must be consistent with the type of vessels used in rotation r (F.1)
		for (int r = 0; r < nbRotations; r++) {
			getModel().getChocoModel().element(fpR[r], fuelCosts.stream().mapToInt(Integer::intValue).toArray(), ((IntVar[]) model.getVariable("v_r"))[r],0).post();		
		}	
		
		
		//the capacity must be consistent with the type of vessels used in rotation r (F.2)
		for (int r = 0; r < nbRotations; r++) {
			getModel().getChocoModel().element(kR[r], capacity.stream().mapToInt(Integer::intValue).toArray(), ((IntVar[]) model.getVariable("v_r"))[r],0).post();		
		}
		
		
	    //the time charter rate must be consistent with the type of vessels used in rotation r (F.3)
		for (int r = 0; r < nbRotations; r++) {
			getModel().getChocoModel().element(tcR[r], timeCharterRate.stream().mapToInt(Integer::intValue).toArray(), ((IntVar[]) model.getVariable("v_r"))[r],0).post();		
		}
	
	    //the fixed port call cost must be consistent with the type of vessels used in rotation r (F.4)		
		for (int p=0;p<nbInfra;p++) {
			for (int r = 0; r < nbRotations; r++) {
				getModel().getChocoModel().element(pcPr[p][r],callCost.stream().map(row -> row.stream().mapToInt(Integer::intValue).toArray()).toArray(int[][]::new),
			    		getModel().getChocoModel().intVar(p), 0, ((IntVar[]) model.getVariable("v_r"))[r], 0);
					}
				}


		
	
	   //the waiting time must be consistent with the type of vessels used in rotation r (F.5) 		
		for (int p=0;p<nbInfra;p++) {
			for (int r = 0; r < nbRotations; r++) {
					 getModel().getChocoModel().element(wtPr[p][r], waitingTime.stream().map(row -> row.stream().mapToInt(Integer::intValue).toArray()).toArray(int[][]::new),
					    		getModel().getChocoModel().intVar(p), 0, ((IntVar[]) model.getVariable("v_r"))[r], 0);				
					}
	   			}
	   //the manoeuvring time when entering ports must be consistent with the type of vessels used in rotation r (F.6)
		for (int p=0;p<nbPorts;p++) {
			for (int r = 0; r < nbRotations; r++) {
					 getModel().getChocoModel().element(manInPr[p][r], manIn.stream().map(row -> row.stream().mapToInt(Integer::intValue).toArray()).toArray(int[][]::new),
					    		getModel().getChocoModel().intVar(p), 0, ((IntVar[]) model.getVariable("v_r"))[r], 0);				
					}
				}
		
	  //the manoeuvring time when leaving ports must be consistent with the type of vessels used in rotation r (F.7)
		for (int p=0;p<nbPorts;p++) {
			for (int r = 0; r < nbRotations; r++) {
					 getModel().getChocoModel().element(manOutPr[p][r], manOut.stream().map(row -> row.stream().mapToInt(Integer::intValue).toArray()).toArray(int[][]::new),
					    		getModel().getChocoModel().intVar(p), 0, ((IntVar[]) model.getVariable("v_r"))[r], 0);				
					}
				}
				
		
        //the productivity must be consistent with the type of vessels used in rotation r (F.8)
		for (int p=0;p<nbPorts;p++) {
			for (int r = 0; r < nbRotations; r++) {
					 getModel().getChocoModel().element(prodPr[p][r], prodi.stream().map(row -> row.stream().mapToInt(Integer::intValue).toArray()).toArray(int[][]::new),
					    		getModel().getChocoModel().intVar(p), 0, ((IntVar[]) model.getVariable("v_r"))[r], 0);				
					}
				}
		
	    //the minimal speed must be consistent with the type of vessels used in rotation r (F.9)

		for (int r = 0; r < nbRotations; r++) {
			getModel().getChocoModel().element(speedRMin[r], speeds_min.stream().mapToInt(Integer::intValue).toArray(), ((IntVar[]) model.getVariable("v_r"))[r],0).post();		
		}
		
		
	    //the maximal speed must be consistent with the type of vessels used in rotation r (F.10)

		for (int r = 0; r < nbRotations; r++) {
			getModel().getChocoModel().element(speedRMax[r], speeds_max.stream().mapToInt(Integer::intValue).toArray(), ((IntVar[]) model.getVariable("v_r"))[r],0).post();		
		}
		

	}


}

