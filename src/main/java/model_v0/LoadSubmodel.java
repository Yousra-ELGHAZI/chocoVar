package model_v0;

import java.util.List;
import java.util.Set;

import tools.Operator;
import tools.ClauseAdder;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;



public class LoadSubmodel  extends Submodel{
	
	/**This class allows to represent a part of model related to vessel loads  */

	
	private BoolVar[][][]leaveKpr;	   						     //true if the commodity k leaves the port/canal p using rotation r 
	private IntVar[][] teuPr;									//the number of TEU that must be loaded or unloaded at port p for rotation r	

	private List<Integer> quantity;							   //quantity of commodities		
	private List<List<Integer>> successors=AuxiliaryFunctions.successorLists(infra, nbCanals, nbPorts);	//lists of possible successors for each port/canal in infra
	private Set<Integer> totalLoad;							 //all the possible values for the sum of the load entering port p and the load leaving port p for a given rotation
	
	private int maxKv;									 	//the maximum capacity of vessels whatever the vessel type
	private int  sumK;										//the number of TEU for all the commodities
	private boolean  withRefinedDomains ;    		   		//true if the model used refined domains, False otherwise

	/**
	 * Constructor for creating a LoadSubmodel object.
	 * @param model The model object 
	 */
	public LoadSubmodel(Model model) {
		super(model);
		this.withRefinedDomains=model.isWithRefinedDomains();
		
		//we get the information about quantity of commodities
	      this.quantity= AuxiliaryFunctions.getQuantity(model.getInstance());

	   //the maximum capacity of vessels whatever the vessel type
		  this.maxKv=AuxiliaryFunctions.getMaxKv(model.getInstance());
	         
	         
	   //the number of TEU for all the commodities
		  this.sumK = AuxiliaryFunctions.getSumK(model.getInstance());
      
	  //we compute all the possible production times
		 this.totalLoad = AuxiliaryFunctions.getTotalLoad(model.getInstance());        
	}	

	@Override
	public void defineVariables() {
		
		
	//True if the commodity k leaves the port/canal p using rotation r, False otherwise		
		leaveKpr = new BoolVar[nbCommodities][nbInfra][nbRotations];
        for(int k=0; k<nbCommodities; k++){
       	  for(int p=0; p<nbInfra; p++){
       	    for(int r=0; r<nbRotations; r++){
       	    	leaveKpr[k][p][r] = getModel().getChocoModel().boolVar("leave_kpr["+k+"]["+p+"]["+r+"]");
       	    }
       	  }
       	}
        addVariables("leave_kpr", leaveKpr);
        
        
      //the number of TEU that must be loaded or unloaded at port p for rotation r
	      if (withRefinedDomains) {
	    	     teuPr = getModel().getChocoModel().intVarMatrix("teu_pr", nbPorts,nbRotations,totalLoad.stream().mapToInt(i -> i).toArray());
	
	
	      } else {
	          	  teuPr = getModel().getChocoModel().intVarMatrix("teu_pr",nbPorts,nbRotations, 0,2 * Math.min(sumK, maxKv) + 1);
	    	      }
	      
	      addVariables("teu_pr", teuPr);




	}

	@Override
	public void defineConstraints() {
		ClauseAdder clauseAdder = new ClauseAdder(this.model);

        //computes the number of TEU that are loaded or unloaded at port p (L.1)
        for (int p = 0; p < nbPorts; p++) {
            for (int r = 0; r < nbRotations; r++) {
    		   
		        int[] weights = new int[nbCommodities];
		
			     for (int k = 0; k < nbCommodities; k++) {
			         weights[k] = 1; 
			     }
			     IntVar[] tempVars = new IntVar[nbCommodities]; 
			     for (int k = 0; k < nbCommodities; k++) {
			         IntVar tempsum = getModel().getChocoModel().intVar(((BoolVar[][][])model.getVariable("from_kpr"))[k][p][r].getLB()+((BoolVar[][][])model.getVariable("to_kpr"))[k][p][r].getLB(),((BoolVar[][][])model.getVariable("from_kpr"))[k][p][r].getUB()+((BoolVar[][][])model.getVariable("to_kpr"))[k][p][r].getUB()); 
			         IntVar tempVar = getModel().getChocoModel().intVar(0,sumK*100); 
				     getModel().getChocoModel().arithm(((BoolVar[][][])model.getVariable("from_kpr"))[k][p][r], "+", ((BoolVar[][][])model.getVariable("to_kpr"))[k][p][r],"=",tempsum).post();
				      getModel().getChocoModel().times(tempsum, getModel().getChocoModel().intVar(quantity.get(k)), tempVar).post();
				     tempVars[k] = tempVar;
			     }
			     getModel().getChocoModel().scalar(tempVars, weights, "=", teuPr[p][r]).post();
		            }
        }
	    //sets leave[k,p,r] to 1 when commodity k enters in rotation r at port p (L.2) 
        for(int k=0; k<nbCommodities; k++){
         	  for(int p=0; p<nbPorts; p++){
         	    for(int r=0; r<nbRotations; r++){
         	    	clauseAdder.addImplication (((BoolVar[][][])model.getVariable("from_kpr"))[k][p][r], Operator.EQ, 1, leaveKpr[k][p][r], Operator.EQ, 1);
                }
            }
        }
        
                
        //sets leave[k,p,r] to 0 when commodity k leaves rotation r at port p (L.3)         
        for(int k=0; k<nbCommodities; k++){
         	  for(int p=0; p<nbPorts; p++){
                for(int r=0; r<nbRotations; r++){
                  clauseAdder.addImplication (((BoolVar[][][])model.getVariable("to_kpr"))[k][p][r], Operator.EQ, 1, leaveKpr[k][p][r], Operator.EQ, 0);
                }
            }
        }
        
        
        
        //ensures the transitivity (L.4)
        for (int r = 0; r < nbRotations; r++) {
            for (int k = 0; k < nbCommodities; k++) {
                for (int p = 0; p < nbInfra; p++) {
                    for (int sp : successors.get(p)) {
                        if (sp < nbPorts) {
                            getModel().getChocoModel().ifThen(
                            		getModel().getChocoModel().and(
		                           		 getModel().getChocoModel().arithm(((IntVar[][]) model.getVariable("s_rp"))[r][p], "=", sp),
		                           		 getModel().getChocoModel().arithm(((BoolVar[][][])model.getVariable("from_kpr"))[k][sp][r], "=", 0),
		                           		 getModel().getChocoModel().arithm(((BoolVar[][][])model.getVariable("to_kpr"))[k][sp][r], "=", 0)),
                            		 getModel().getChocoModel().arithm(leaveKpr[k][p][r], "=", leaveKpr[k][sp][r])
                            );
                        }
                    }
                }
            }
        }
		
        //if port/canal p is not used in rotation r, no commodity k can leave this port/canal for rotation r (L.5) 
        for (int r = 0; r < nbRotations; r++) {
            for (int k = 0; k < nbCommodities; k++) {
                for (int p = 0; p < nbInfra; p++) {
                  clauseAdder.addImplication(((IntVar[][]) model.getVariable("s_rp"))[r][p], Operator.EQ, p, leaveKpr[k][p][r], Operator.EQ, 0);
                }
            }
        }
        
        
        
       //computes the load of vessel leaving the port/canal p for rotation r (L.6)
        for (int r = 0; r < nbRotations; r++) {
        	  for (int p = 0; p < nbPorts; p++) {
                IntVar[] vars = new IntVar[nbCommodities];
                int[] coeffs = new int[nbCommodities];
                for (int k = 0; k < nbCommodities; k++) {
                  vars[k] = leaveKpr[k][p][r];
                  coeffs[k] = quantity.get(k);
                }
                getModel().getChocoModel().scalar(vars, coeffs, "<=",((IntVar[]) model.getVariable("k_r"))[r]).post();
            }
        }
        //a vessel must carry at least one commodity (L.8)       
        for (int r = 0; r < nbRotations; r++) {
            for (int p = 0; p < nbPorts; p++) {
                IntVar[] vars = new IntVar[nbCommodities];
                for (int k = 0; k < nbCommodities; k++) {
                    vars[k] = leaveKpr[k][p][r];
                }
                clauseAdder.addEquivalence(((IntVar[][]) model.getVariable("s_rp"))[r][p], Operator.EQ, p, getModel().getChocoModel().arithm(getModel().getChocoModel().sum("sum_leave_kpr", vars),"=",0).reify(), Operator.EQ, 1);
            }
        }
        
        
        //link between the acceptance of a commodity and its transport (L.9)        
        for (int k = 0; k < nbCommodities; k++) {
            BoolVar[] leave_kpr_sum = new BoolVar[nbPorts * nbRotations];
            int index = 0;
            for (int p = 0; p < nbPorts; p++) {
                for (int r = 0; r < nbRotations; r++) {
                    leave_kpr_sum[index] = leaveKpr[k][p][r];
                    index++;
                }
            }
            clauseAdder.addEquivalence(((IntVar[]) model.getVariable("alpha_k"))[k], Operator.EQ, 1,getModel().getChocoModel().arithm( getModel().getChocoModel().sum("sum",leave_kpr_sum), ">", 0).reify(), Operator.GT, 0);

        }
	}

	

}

