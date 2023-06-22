
package model_v0;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tools.Operator;
import tools.ClauseAdder;

import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class TimeSubmodel  extends Submodel{
	
	/** This class allows to represent a part of model related to time */

	
	

	private IntVar[][] tPr;							   		//the total of operation time for rotation r at port/canal p	
    private IntVar[] depR;						      		//the port from which rotation r starts
    private IntVar[][] timeInPr;  					 		//the time the vessel of rotation k arrives at port p
    private IntVar[][] timeOutPr; 							//the time the vessel of rotation k leaves port p
    private IntVar[][] speedRp ;				   			//the average speed for rotation r when going from port/canal p to port/canal s_rp
    private IntVar[][] stRp;								//the sailing time between ports/canals p  and s_rp for rotation r, 0 if port/canal p does not belong to rotation r=s
    private IntVar[] TR ;						  			//the total time for achieving one round trip of service r

    
	private boolean  withRefinedDomains;            		//true if the model used refined domains, False otherwise
	private boolean allowedIfConstraint ;          			//true if the function if is allowed in intention constraints, False otherwise
	private Set<Integer> totalLoad;
	private  ArrayList<Set<Integer>> tValues;
	private List<Integer> trav;						  	     //list of total operation time for canals	
	private ArrayList<Tuples> tableProductionTime ;   	     //list of all the possible production times  
	private int ratioMu;							 	     //the ratio which allows us to convert the number of TEU to the number of containers we have to handle,	
	private int hmax;							   		     //the maximal number of hours that a rotation may last.	
	private ArrayList<Integer> speeds;           			//list to store speeds of vessels
	private List<Tuples> tableSailingTime;					//stores all the possible sailing times
	private int speedMin;								    //Minimal speed			
	private int speedMax;									//Maximal speed
	private int speedStep;									//the step between two speeds	
	private List<List<Integer>> successors=AuxiliaryFunctions.successorLists(infra, nbCanals, nbPorts);	//lists of possible successors for each port/canal in infra

	/**
	 * Constructor for creating a TimeSubmodel object
	 * @param model  The Model object	 */
	 public TimeSubmodel(Model model) {
			super(model);
			
			this.withRefinedDomains=model.isWithRefinedDomains();
			this.allowedIfConstraint=model.isAllowedIfConstraint();
			
			this.ratioMu=(int) (model.getRatioMu()*100);
			this.hmax=model.getHmax();
			this.speedStep=model.getSpeedStep();
				  
		    //we compute all the possible production times
			 this.totalLoad = AuxiliaryFunctions.getTotalLoad(model.getInstance());  
			      
		        
		        List<String> ports = new ArrayList<String>(getModel().getInstance().getPorts());
		        List<String> vesselsTypes = new ArrayList<String>(getModel().getInstance().getVesselTypes());
			 if (withRefinedDomains) {  
			        tableProductionTime = new ArrayList<>();
			        tValues = new ArrayList<>();
			        for (int p = 0; p < nbPorts; p++) {
			            tValues.add(new HashSet<>());
			            Tuples tuples = new Tuples(true);
			            tuples.setUniversalValue(-10);
			            tuples.add(0,tuples.getStarValue(),0);
			            for (int vt = 0; vt < nbVessels; vt++) {
			                int prod = getModel().getInstance().getPort(ports.get(p)).getProductivity(vesselsTypes.get(vt));
			                int cap = getModel().getInstance().getVesselType(vesselsTypes.get(vt)).getRealCapacity();
	
			                for (int teu : totalLoad) {
			                    if (teu <= 2 * cap) {
			                        int val =(int) ((ratioMu * teu)/(prod*100));
			                        tValues.get(p).add(val);
			                        tuples.add(new int[]{teu, vt + 1, val});
			                    }
			                }
			            }
	
	
			            tableProductionTime.add(tuples);
			        }
			 }else {
			        tableProductionTime = new ArrayList<>();
			       
			        for (int p = 0; p < nbPorts; p++) {
			            Tuples tuples = new Tuples(true);
			            tuples.setUniversalValue(-10);
			            tuples.add(0,tuples.getStarValue(),0);
			            for (int vt = 0; vt < nbVessels; vt++) {
			                int prod = getModel().getInstance().getPort(ports.get(p)).getProductivity(vesselsTypes.get(vt));
			                int cap = getModel().getInstance().getVesselType(vesselsTypes.get(vt)).getRealCapacity();
	
			                for (int teu : totalLoad) {
			                    if (teu <= 2 * cap) {
			                        double val =(double)(ratioMu * teu)/(prod*100);
  		                        tuples.add(new int[]{teu, vt + 1, (int)val});
			                    }
			                }
			            }
	
	
			            tableProductionTime.add(tuples);
			        }
			      }
		      //total operation time for canals
		      trav= new ArrayList<Integer>();
		      trav.add(0);
		      
		      for(String name:getModel().getInstance().getCanals()) {
		     	 trav.add(getModel().getInstance().getCanal(name).getDuration());
		     	 
		      }
		      
		      for(String name:getModel().getInstance().getCanals()) {
		     	 trav.add(getModel().getInstance().getCanal(name).getDuration());
		     	 
		      }
		        
		        
		        
		        
	 		//the average speed per rotation and per leg

	 		     List<Integer> speedMinByVessel = new ArrayList<>();
	 		     List<Integer> speedMaxByVessel = new ArrayList<>();

	 		    for (String vt : getModel().getInstance().getVesselTypes()) {
	 		    	speedMinByVessel.add(getModel().getInstance().getVesselType(vt).getMinimalSpeed());
	 		    	speedMaxByVessel.add(getModel().getInstance().getVesselType(vt).getMaximalSpeed());

	 		    }
	 		  speedMin = Collections.min(speedMinByVessel);
	 		  speedMax = Collections.max(speedMaxByVessel);
	 		   	 		  
	 		//the average speed for rotation r when going from port/canal p to port/canal s_rp
	 		    speeds = new ArrayList<Integer>();
	 		    speeds.add(0);
				for (int i = speedMin; i <= speedMax; i += speedStep) {
				    speeds.add(i);
				}
				
			//the sailing time between ports/canals p  and s_rp for rotation r, 0 if port/canal p does not belong to rotation r
	        tableSailingTime = AuxiliaryFunctions.computeAllSailingTimes(getModel().getInstance(), infra, successors, speedMin, speedMax, speedStep, hmax);

	 }

	@Override
	public void defineVariables() {
		
	    //the total of operation time for rotation r at port/canal p
	      if (withRefinedDomains) {  
	            tPr = new IntVar[nbInfra][nbRotations];
	            for (int i = 0; i < infra.size(); i++) {
	                for (int j = 0; j < nbRotations; j++) {
	                    if (i < nbPorts) {
	                        tPr[i][j] = getModel().getChocoModel().intVar("t_pr[" + i + "][" + j+"]", tValues.get(i).stream().mapToInt(Integer::intValue).toArray());
	                    } else {
	                        tPr[i][j] = getModel().getChocoModel().intVar("t_pr[" + i + "][" + j+"]", 0, getModel().getInstance().getCanal(infra.get(i)).getDuration());
	                    }
	                }
	            }
	      }else {
	    	  tPr = new IntVar[nbInfra][nbRotations];
	    	  for (int i = 0; i < nbInfra; i++) {
	    	      for (int j = 0; j < nbRotations; j++) {
	    	          if (i < nbPorts) {
	    	              tPr[i][j] =  getModel().getChocoModel().intVar("t_pr[" + i + "][" + j+"]", 0, hmax);
	    	          } else {
	    	              tPr[i][j] =  getModel().getChocoModel().intVar("t_pr[" + i + "][" + j+"]", 0, getModel().getInstance().getCanal(infra.get(i)).getDuration());
	    	          }
	    	      }
	    	  }

	    	  
	      }
	      addVariables("t_pr", tPr);	
	     
          //the port from which rotation r starts (this choice is arbitrary), -1 if the rotation is not used
	      depR = getModel().getChocoModel().intVarArray("dep_r", nbRotations, -1, nbPorts-1);
	      addVariables("dep_r", depR);

	      //the time the vessel of rotation k arrives at port p
	      timeInPr = getModel().getChocoModel().intVarMatrix("timeIn_pr", nbInfra, nbRotations, 0, hmax);
	      addVariables("timeIn_pr", timeInPr);

	      //the time the vessel of rotation k leaves port p
	      timeOutPr = getModel().getChocoModel().intVarMatrix("timeOut_pr", nbInfra, nbRotations, 0, hmax);
	      addVariables("timeOut_pr", timeOutPr);

	      
	      //the average speed for rotation r when going from port/canal p to port/canal s_rp
		  speedRp=getModel().getChocoModel().intVarMatrix("speed_rp",nbRotations,nbInfra,speeds.stream().mapToInt(Integer::intValue).toArray());
	      addVariables("speed_rp", speedRp);
	      
	      
		  //the sailing time between ports/canals p  and s_rp for rotation r, 0 if port/canal p does not belong to rotation r

	         stRp= new IntVar[nbRotations][nbInfra];

	        if (withRefinedDomains) {
	            @SuppressWarnings("unchecked")
				Set<Integer>[] stValues = new Set[nbInfra];
	            for (int p = 0; p < nbInfra; p++) {
	                stValues[p] = new HashSet<>();
	                for (int i = 0; i < tableSailingTime.get(p).nbTuples(); i++) {
	                    stValues[p].add(tableSailingTime.get(p).get(i)[2]);
	                }
	            }
	            for (int i = 0; i < nbRotations; i++) {
	                for (int j = 0; j < infra.size(); j++) {
	                    stRp[i][j] = getModel().getChocoModel().intVar("st_rp[" + i + "][" + j + "]", stValues[j].stream().mapToInt(Integer::intValue).toArray());
	                }
	            }

	        } else {
	            int maxSt = getModel().getInstance().getDistances().getMaximalDistance() / speedMin + 1;
	            for (int i = 0; i < nbRotations; i++) {
	                for (int j = 0; j < infra.size(); j++) {
	                    stRp[i][j] = getModel().getChocoModel().intVar("st_rp[" + i + "][" + j + "]", 0, Math.min(hmax, maxSt));
	                }
	            }
	        }
		     addVariables("st_rp", stRp);
		     
           // the total time for achieving one round trip of service r
             TR = getModel().getChocoModel().intVarArray("T_r", nbRotations, 0,hmax);
		     addVariables("T_r", TR);

	}

	@Override
	public void defineConstraints() {
        ClauseAdder clauseAdder = new ClauseAdder(this.model);
        
        //total operation time for ports
        if(withRefinedDomains) {
        	
        	//compute the total operation time at port p (T.1)	
	        for (int p = 0; p < nbPorts; p++) {
	        	  for (int r = 0; r < nbRotations; r++) {
	        	    getModel().getChocoModel().table(new IntVar[]{((IntVar[][])model.getVariable("teu_pr"))[p][r], ((IntVar[])model.getVariable("v_r"))[r], tPr[p][r]}, tableProductionTime.get(p)).post();
	        	  
	        	  }
	        	}
        }else {
        	if(allowedIfConstraint){
        		
        		//compute the total operation time at port p (T.1)
        		for (int p = 0; p < nbPorts; p++) {
        		    for (int r = 0; r < nbRotations; r++) {
        		        IntVar resultProd = getModel().getChocoModel().intVar(((IntVar[][])model.getVariable("teu_pr"))[p][r].getLB()*ratioMu,((IntVar[][])model.getVariable("teu_pr"))[p][r].getUB()*ratioMu);
        		        getModel().getChocoModel().times(((IntVar[][])model.getVariable("teu_pr"))[p][r], getModel().getChocoModel().intVar(ratioMu), resultProd).post();;
        		        IntVar resultDiv = getModel().getChocoModel().intVar(resultProd.getLB()/((IntVar[][])model.getVariable("prod_pr"))[p][r].getLB(),resultProd.getUB()/((IntVar[][])model.getVariable("prod_pr"))[p][r].getUB());
        		        getModel().getChocoModel().div(resultProd, ((IntVar[][])model.getVariable("prod_pr"))[p][r], resultDiv).post();
        		        getModel().getChocoModel().arithm(tPr[p][r], "=", resultDiv).post();

        		    }
        		}       		
        	}else {
        		
        		//compute the total operation time at port p (T.1)       		
        		    for (int r = 0; r < nbRotations; r++) {
                		for (int p = 0; p < nbPorts; p++) {

        		    getModel().getChocoModel().ifThen(
        		          		getModel().getChocoModel().arithm(((IntVar[][])model.getVariable("s_rp"))[r][p], "!=", p),
        		            
        		          		getModel().getChocoModel().table(new IntVar[]{((IntVar[][])model.getVariable("teu_pr"))[p][r], ((IntVar[])model.getVariable("v_r"))[r], tPr[p][r]}, tableProductionTime.get(p))
        		    			);
        		    }    
        		    
        		
        		    
        		}
        		    
        		    
        		    }
        	}
       //the total operation time is zero if port is not used (T.2)

       for (int r = 0; r < nbRotations; r++) {
            for (int p = 0; p < nbPorts; p++) {
                clauseAdder.addEquivalence (((IntVar[][])model.getVariable("s_rp"))[r][p], Operator.EQ, p, tPr[p][r], Operator.EQ, 0);
            }
        }
        
        
        
        
        //the total operation time at canal p corresponds to the duration for traversing the canal (T.3)
       for (int r = 0; r < nbRotations; r++) {
      	    for (int p = nbPorts; p <nbInfra; p++) {
      	       getModel().getChocoModel().times(getModel().getChocoModel().intVar(trav.get(p - nbPorts+1)), 
  							    	        		 getModel().getChocoModel().arithm(((IntVar[][])model.getVariable("s_rp"))[r][p], "!=", p).reify(), 
  							    	        		 tPr[p][r]).post();
      	        
      	    }
        }
        
        
        //if rotation r is not used, there is no starting port and conversely (T.4)

        for (int r = 0; r < nbRotations; r++) {
        	clauseAdder.addEquivalence (((IntVar[])model.getVariable("v_r"))[r], Operator.EQ, 0, depR[r], Operator.EQ, -1);
        }

          
        //defines the starting port as one having the largest index (T.5)
        for (int r = 0; r < nbRotations; r++) {
        	//computes the port having the largest index (T.5)
  	    	IntVar[] products = new IntVar[nbPorts];
      	    for (int p = 0; p <nbPorts; p++) {  
      	        BoolVar b=getModel().getChocoModel().arithm(((IntVar[][])model.getVariable("s_rp"))[r][p],"!=",p).reify();
      	        products[p] = getModel().getChocoModel().intVar( p*b.getLB(), p*b.getUB());
        	    getModel().getChocoModel().arithm(products[p], "=", b,"*",p).post();
      	    }
            clauseAdder.addImplication(((IntVar[])model.getVariable("v_r"))[r], Operator.GT, 0, getModel().getChocoModel().max(depR[r], products).reify(), Operator.EQ, 1);
        }
        
        //the starting port is the reference for time of rotation r (T.6)
        for (int r = 0; r < nbRotations; r++) {
            for (int p = 0; p < nbPorts; p++) {
              clauseAdder.addImplication (depR[r], Operator.EQ, p, timeInPr[p][r], Operator.EQ, 0);
            }
        }
        
        //the time of departure from port p is the time of arrival at the port plus the operation time (T.7)
        for (int r = 0; r < nbRotations; r++) {
            for (int p = 0; p < nbInfra; p++) {
            	getModel().getChocoModel().post(
            			getModel().getChocoModel().arithm(timeInPr[p][r], "+", tPr[p][r],"=",timeOutPr[p][r]));
            }
        }
 
        //the sailing time and speed must be consistent with respect to the corresponding leg (T.8)

        for (int r = 0; r < nbRotations; r++) {
            for (int p = 0; p <nbInfra; p++) {
    		    for (int sp : successors.get(p)) {
    		    	getModel().getChocoModel().ifThen(
          		getModel().getChocoModel().arithm(((IntVar[][])model.getVariable("s_rp"))[r][p], "=", sp),
            	getModel().getChocoModel().table(new IntVar[] { ((IntVar[][])model.getVariable("s_rp"))[r][p], speedRp[r][p], stRp[r][p]}, (Tuples) tableSailingTime.get(p))

            	 );
            }
            }
	}
        
        //the speed cannot be under the minimal speed of the vessels used in rotation r (T.9)
        for (int r = 0; r < nbRotations; r++) {
        	  for (int p = 0; p < nbInfra; p++) {
        		  getModel().getChocoModel().ifThen(
        						  getModel().getChocoModel().arithm(((IntVar[][])model.getVariable("s_rp"))[r][p], "!=", p),
        						  
        				  
        				  getModel().getChocoModel().arithm(speedRp[r][p], ">=", ((IntVar[])model.getVariable("speed_r_min"))[r])
        	    );
        	  }
        	}
        
       //the speed cannot exceed the maximal speed of the vessels used in rotation r (T.10)
        for (int r = 0; r < nbRotations; r++) {
        	  for (int p = 0; p < nbInfra; p++) {
						  getModel().getChocoModel().arithm(speedRp[r][p], "<=",((IntVar[])model.getVariable("speed_r_max"))[r]).post();
		  
        				  
        	  }
        	}
        
       //the time of arrival at port/canal s_rp from port/canal p in rotation r (T.11)
        for (int r = 0; r < nbRotations; r++) {
        	  for (int p = 0; p < nbInfra; p++) {
        		    for (int sp : successors.get(p)) {
        		    	getModel().getChocoModel().ifThen(
              				  getModel().getChocoModel().and(
              						  getModel().getChocoModel().arithm(((IntVar[][])model.getVariable("s_rp"))[r][p], "=", sp),
              						  getModel().getChocoModel().arithm(depR[r], "!=", sp)

              						  ),             				  
              				  getModel().getChocoModel().sum(new IntVar[]{timeOutPr[p][r], ((IntVar[][])model.getVariable("manOut_pr"))[p][r], stRp[r][p], ((IntVar[][])model.getVariable("wt_pr"))[sp][r], ((IntVar[][])model.getVariable("manIn_pr"))[sp][r]}, "=", timeInPr[sp][r])
              	    );
        		    	
        		    }
        		    }
 }
        
        
        //the time when k enters in the rotation at port p precedes the time when k leaves it at port s_rp (T.12)
        for (int r = 0; r < nbRotations; r++) {
      	  for (int p = 0; p < nbPorts; p++) {
      		  for(int k=0;k<nbCommodities;k++) {
      		    for (int sp : successors.get(p)) {
      		    	if (sp<nbPorts) {

			      			getModel().getChocoModel().ifThen(
			        				  getModel().getChocoModel().and(
			        						  getModel().getChocoModel().arithm(((IntVar[][][])model.getVariable("from_kpr"))[k][p][r], "=", 1),
			        						  getModel().getChocoModel().arithm(((IntVar[][][])model.getVariable("to_kpr"))[k][sp][r], "=",1 )
			
			        						  ),             				  
			        	    
			        				 getModel().getChocoModel().or(
			        						 getModel().getChocoModel().arithm(timeInPr[p][r], "<", timeInPr[sp][r]),
			                				 getModel().getChocoModel().and(

			                						 getModel().getChocoModel().arithm(timeInPr[sp][r], "<", timeInPr[p][r]),
			                						 getModel().getChocoModel().arithm(timeInPr[p][r], "<", timeInPr[sp][r],"+",TR[r])


			                						 						)
			        						 						)
			      							);
      		    }
      			  
      		  }
      		}
      	  } 
        }
        
        
        // if port/canal p does not belong to rotation r, the sailing time is zero (T.13)
        for (int r = 0; r < nbRotations; r++) {
        	  for (int p = 0; p < nbInfra; p++) {
              clauseAdder.addEquivalence(((IntVar[][])model.getVariable("s_rp"))[r][p], Operator.EQ, p, stRp[r][p], Operator.EQ, 0);
        	  }
        }

        //if port/canal p does not belong to rotation r, the entering time is zero (T.14)
        for (int r = 0; r < nbRotations; r++) {
      	  for (int p = 0; p < nbInfra; p++) {
            clauseAdder.addImplication(((IntVar[][])model.getVariable("s_rp"))[r][p], Operator.EQ, p, timeInPr[p][r], Operator.EQ, 0);
      	  }
      }
       
        
        //if port/canal p does not belong to rotation r, the speed from p is zero (T.15)
        for (int r = 0; r < nbRotations; r++) {
      	  for (int p = 0; p < nbInfra; p++) {
      		  clauseAdder.addEquivalence (((IntVar[][])model.getVariable("s_rp"))[r][p], Operator.EQ, p, speedRp[r][p], Operator.EQ, 0);
      	  }
      }


        
	}



}

