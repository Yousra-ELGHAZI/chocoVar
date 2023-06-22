package model_v0;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tools.Operator;
import tools.ClauseAdder;

import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class VesselAvailabilitySubmodel extends Submodel{
	
	  /** This class allows to represent a part of model related to vessel availability */
	
	private IntVar[] nR;							//the number of vessels deployed on rotation r.

	private ArrayList<Integer> numberOfVessels;     //list to stores number of vessels 

	private int hmax; 							    //the maximal number of hours that a rotation may last.	
	private int  tNbv;								//the version of table NbVessel used	

    private Tuples tableNbVessel;
	private List<List<Integer>> successors=AuxiliaryFunctions.successorLists(infra, nbCanals, nbPorts);	//lists of possible successors for each port/canal in infra


	/**
	 * Constructor for creating a VesselAvailabilitySubmodel object
	 * @param model  The Model object
	 * 
	 */
	public VesselAvailabilitySubmodel(Model model) {
		super(model);

		this.hmax=model.getHmax();
		this.tNbv=model.gettNbv();
		
		
		//stores number of vessels 
        numberOfVessels = new ArrayList<>();        
        for (String name:getModel().getInstance().getVesselTypes()) {
      	  numberOfVessels.add(getModel().getInstance().getVesselType(name).getVesselNumber());
        }	
        
        tableNbVessel = new Tuples(true);
        tableNbVessel.add(0,0);
        
        for (int t = 1; t <= hmax; t++) {

            if (t <= 168) {       //168 = 7 * 24, i.e. the duration of a week in hours
            	tableNbVessel.add(t,1);
            } else {
            	if(tNbv==0) {
		            	int modulo=t%168;
		                if (modulo >0) {
		                	tableNbVessel.add(t,t / 168 +1);
	
		                } else {
		                	tableNbVessel.add(t,t / 168);
		                }
            	}else if (tNbv==1) {
	            		int modulo=t%168;
		                if (modulo >=168/2) {
		                	tableNbVessel.add(t,t / 168 +1);
	
		                } else {
		                	tableNbVessel.add(t,t / 168);
		                }
            		
            	}else if (tNbv==2) {
	            		double diff = (double)t / 168 - t / 168; 
	                    if (diff <= 0.5) {
	                    	tableNbVessel.add(t,t / 168);

	                    } else {
	                    	tableNbVessel.add(t,t / 168+1);
            		
	                    }
            	}
            }
        }

	
	}

	@Override
	public void defineVariables() {
		
		
        //the number of vessels deployed on rotation r.
         nR= getModel().getChocoModel().intVarArray("n_r", nbRotations,0,Collections.max(numberOfVessels));
         addVariables("n_r", nR);
         

	}

	@Override
	public void defineConstraints() {
		
	 //determines the number of vessel for each rotation (W.1)
		for(int r=0;r<nbRotations;r++)		{
			 getModel().getChocoModel().table(new IntVar[]{((IntVar[]) model.getVariable("T_r"))[r], nR[r]}, tableNbVessel).post();

		}
		
    ClauseAdder clauseAdder = new ClauseAdder(this.model);
    
    //the total time is zero iff the rotation is not used (W.2)
			for(int r=0;r<nbRotations;r++)		{
	      clauseAdder.addEquivalence (((IntVar[]) model.getVariable("v_r"))[r], Operator.EQ, 0,
	                                  ((IntVar[]) model.getVariable("T_r"))[r], Operator.EQ, 0);
			}



		
	//determines the total time for each used rotation (W.3)    
        for (int r = 0; r < nbRotations; r++) {
                for (int p = 0; p < nbInfra; p++) {
                    for (int sp : successors.get(p)) {
                    	getModel().getChocoModel().ifThen(
                    			
                    			getModel().getChocoModel().and(			
                    					getModel().getChocoModel().arithm(((IntVar[]) model.getVariable("dep_r"))[r], "=", sp),
            							getModel().getChocoModel().arithm(((IntVar[][]) model.getVariable("s_rp"))[r][p], "=", sp)

                    					),
                    			getModel().getChocoModel().sum(new IntVar[]{((IntVar[][]) model.getVariable("timeOut_pr"))[p][r],((IntVar[][]) model.getVariable("manOut_pr"))[p][r],
                    					((IntVar[][]) model.getVariable("st_rp"))[r][p],((IntVar[][]) model.getVariable("wt_pr"))[sp][r],((IntVar[][]) model.getVariable("manIn_pr"))[sp][r]},
                    					"=", ((IntVar[]) model.getVariable("T_r"))[r])


                    
                    );
                        
                    }
                }
            
        }	
        
     //ensures that enough vessels are available (W.4)
	   for (int v = 0; v < nbVessels; v++) {
	        IntVar[] products = new IntVar[nbRotations];
	        for (int r = 0; r < nbRotations; r++) {
	        	BoolVar vType= getModel().getChocoModel().arithm(((IntVar[]) model.getVariable("v_r"))[r], "=", getModel().getChocoModel().intVar(v + 1)).reify();
	            products[r] = getModel().getChocoModel().intVar("product_" + r, 0, nR[r].getUB());
	
	            getModel().getChocoModel().times(nR[r], vType, products[r]).post(); 
	        }
	
	        getModel().getChocoModel().sum(products, "<=", numberOfVessels.get(v)).post();
		}
	        


	}



	

}

