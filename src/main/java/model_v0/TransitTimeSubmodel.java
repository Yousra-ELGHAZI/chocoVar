package model_v0;

import java.util.ArrayList;
import java.util.List;

import tools.Operator;
import tools.ClauseAdder;

import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import org.chocosolver.solver.constraints.extension.Tuples;


public class TransitTimeSubmodel extends Submodel {
	
	 /** This class allows to represent a part of model related to transit time */
	
	private IntVar[][]ctimeInIk ;					//the time when commodity k enters in the ith rotation
	private IntVar[][]ctimeOutIk ;					// the time when commodity k leaves the ith rotation
	private IntVar[][] indexCtimeInKi;				//stores the indices of the CtimeIn elements to search for
	private IntVar[][] indexCtimeOutKi;				//stores the indices of the CtimeOut elements to search for
	private IntVar[][] ctimeInModIk;				//the time when commodity k enters in the ith rotation modulo one week
	private IntVar[][] ctimeOutModIk;				//the time when commodity k leaves the ith rotation modulo one week
	
	private IntVar[][] deltaIk ;					//the time spent by commodity k on its ith rotation
	private IntVar[][] DeltaIk;						//the time that commodity k must wait between the ith rotation and the next one
	private IntVar[] timeOut;						//the time the vessel of rotation r leaves port p (p ∈ P ∪ C)
	private IntVar[] timeIn;						//the time the vessel of rotation r arrives at port p (p ∈ P ∪ C)

	private Tuples tableIndexIn;					//stores indices
    private List<Integer> commoditiesWithTt;        //list of commodities With Transit time

	private int hmax; 							    //the maximal number of hours that a rotation may last.	
	private Tuples tableMod;						//stores indices
	private Tuples tableDelta;						//stores indices
	private IntVar[][] lessThanKi;					//auxiliary variables specifying if the number of rotations used for commodity k is less than i

	
	
	/**
	 * Constructor for creating a Transitmodel object
	 * @param model  The Model object
	  */
	public TransitTimeSubmodel(Model model) {
		super(model);

		this.hmax=model.getHmax();
		
		////List of commodities With Transit time
		commoditiesWithTt= new ArrayList<Integer>();
		for (int i = 0; i < getModel().getInstance().getNumberCommodityWithTt(); i++) {
			commoditiesWithTt.add(i);
		}

		
		//stores indices
	    tableIndexIn = new Tuples(true);
	    tableIndexIn.setUniversalValue(-10);
		tableIndexIn.add(tableIndexIn.getStarValue(), -1, nbPorts * nbRotations);
		for (int p = 0; p < nbPorts; p++) {
		    for (int r = 0; r < nbRotations; r++) {
		        tableIndexIn.add(p, r, p * nbRotations + r);
		    }
		}
		
		// stores the time and time modulo one week
	    tableMod = new Tuples(true);
	    for (int t = 0; t <hmax+1; t++) {
	    	tableMod.add(t,t%168);
	    	
	    }

	    tableDelta=new Tuples(true);
	    tableDelta.setUniversalValue(-10);
	    tableDelta.add(0,tableDelta.getStarValue(),tableDelta.getStarValue(),0);
	    for(int t1 = 0; t1 < 168; t1++){
	        for(int t2 = 0; t2 < 168; t2++){
	        	if (t1<= t2){
	        		tableDelta.add(1,t1,t2,t2-t1);
	        	}else {
	        		tableDelta.add(1,t1,t2,168+t2-t1);

	        	}
	        	
	        }
	    }

            

	}
	
	
	
	
	

	@Override
	public void defineVariables() {
		
		if (getModel().getInstance().getNumberCommodityWithTt() > 0) {

		    // the time when commodity k enters in the ith rotation
			ctimeInIk= getModel().getChocoModel().intVarMatrix("ctimeIn_ik", tsmax+1, getModel().getInstance().getNumberCommodityWithTt() ,0, hmax);
		    addVariables("ctimeIn_ik", ctimeInIk);
		    
		    //the time when commodity k enters in the ith rotation modulo one week
		    ctimeInModIk= getModel().getChocoModel().intVarMatrix("ctimeInMod_ik", tsmax+1, getModel().getInstance().getNumberCommodityWithTt() ,0,168);
		    addVariables("ctimeInMod_ik", ctimeInModIk);		    
		    



		}		
		
		if (getModel().getInstance().getNumberCommodityWithTt() > 0) {
			
		    // the time when commodity k leaves the ith rotation
			ctimeOutIk =  getModel().getChocoModel().intVarMatrix("ctimeOut_ik", tsmax+1,  getModel().getInstance().getNumberCommodityWithTt(), 0, hmax);
		    addVariables("ctimeOut_ik", ctimeOutIk);
		    
		    //the time when commodity k leaves the ith rotation modulo one week
		    ctimeOutModIk =  getModel().getChocoModel().intVarMatrix("ctimeOutMod_ik", tsmax+1,  getModel().getInstance().getNumberCommodityWithTt(), 0,168);
		    addVariables("ctimeOutMod_ik", ctimeOutModIk);

		}

		
		//since we treat a matrix as a vector these variables will be used to store the indices of the elements to search

		if (getModel().getInstance().getNumberCommodityWithTt() > 0) {
			indexCtimeInKi = getModel().getChocoModel().intVarMatrix("index_ctimeIn_ki", getModel().getInstance().getNumberCommodityWithTt(), tsmax+1, 0, nbPorts*nbRotations);
		    addVariables("index_ctimeIn_ki", indexCtimeInKi);

			indexCtimeOutKi = getModel().getChocoModel().intVarMatrix("index_ctimeOut_ki", getModel().getInstance().getNumberCommodityWithTt(), tsmax+1, 0, nbPorts*nbRotations+nbRotations);
		    addVariables("index_ctimeOut_ki", indexCtimeOutKi);

		}
		
		//the time the vessel of rotation r leaves port p (p ∈ P ∪ C)
		timeOut = new IntVar[nbPorts * nbRotations + 1];
		
		for (int p = 0; p < nbPorts; p++) {
		  for (int r = 0; r < nbRotations; r++) {
			  timeOut[p * nbRotations + r]=((IntVar[][]) model.getVariable("timeOut_pr"))[p][r];
		  }
		}
		timeOut[nbPorts * nbRotations] = getModel().getChocoModel().intVar( 0);
	    addVariables("time_Out", timeOut);

		
	   //the time the vessel of rotation r arrives at port p (p ∈ P ∪ C)
		 timeIn = new IntVar[nbPorts*nbRotations+ nbRotations+1];
			
		for (int p = 0; p < nbPorts; p++) {
		  for (int r = 0; r < nbRotations; r++) {
			  timeIn[p * nbRotations + r]=((IntVar[][]) model.getVariable("timeIn_pr"))[p][r];
		  }
		}
		  for (int r = nbPorts * nbRotations; r < nbPorts * nbRotations + nbRotations; r++) {

				  timeIn[r]=((IntVar[]) model.getVariable("T_r"))[r-nbPorts * nbRotations];
		  
		  }
		timeIn[nbPorts*nbRotations+ nbRotations]=getModel().getChocoModel().intVar(0);


	    addVariables("time_In", timeIn);
		if (getModel().getInstance().getNumberCommodityWithTt() > 0) {
		    //the time spent by commodity k on its ith rotation
			 deltaIk = getModel().getChocoModel().intVarMatrix("delta_ik", tsmax+1, getModel().getInstance().getNumberCommodityWithTt() , 0, hmax);
		}	    
		addVariables("delta_ik", deltaIk);

		if (getModel().getInstance().getNumberCommodityWithTt()  > 0) {
		    //the time that commodity k must wait between the ith rotation and the next one
		  DeltaIk = getModel().getChocoModel().intVarMatrix("Delta_ik", tsmax, getModel().getInstance().getNumberCommodityWithTt(), 0, 7*24);
		}
		addVariables("Delta_ik", DeltaIk);


		lessThanKi = getModel().getChocoModel().intVarMatrix("less_than_ki", nbCommodities, tsmax+1,0,1);

	}

	@Override
	public void defineConstraints() {		

		//the time of entry into the rotation is linked to the time when the vessel leaves the port (TT.1)
			for (int k : commoditiesWithTt) {
			    for (int i = 0; i <= tsmax; i++) {
			    	getModel().getChocoModel().table(new IntVar[]{((IntVar[][]) model.getVariable("port_ki"))[k][i], ((IntVar[][]) model.getVariable("rot_ki"))[k][i], indexCtimeInKi[k][i]}, tableIndexIn,"CT+").post();
			    }
			}						
				
							
			for (int k : commoditiesWithTt) {
			    for (int i = 0; i <= tsmax; i++) {
			    	getModel().getChocoModel().element(ctimeInIk[i][k], timeOut, indexCtimeInKi[k][i],0).post();


			    }
			}	
			
		
		//the time to leave the rotation is linked to the time the vessel enters the port (TT.2)
		for(int r=0;r<nbRotations;r++) {
			for (int k : commoditiesWithTt) {
				for (int i = 0; i <= tsmax; i++) {
    		        IntVar prodPortRotation = getModel().getChocoModel().intVar(((IntVar[][]) model.getVariable("port_ki"))[k][i].getLB()*nbRotations,((IntVar[][]) model.getVariable("port_ki"))[k][i].getUB()*nbRotations);
					getModel().getChocoModel().ifThen(
							getModel().getChocoModel().and(									
									getModel().getChocoModel().arithm(((IntVar[][]) model.getVariable("port_ki"))[k][i+1],"!=",((IntVar[]) model.getVariable("dep_r"))[r]),
									
									getModel().getChocoModel().arithm(((IntVar[][]) model.getVariable("rot_ki"))[k][i],"=",r)

								),
							getModel().getChocoModel().and(
									getModel().getChocoModel().arithm(prodPortRotation,"=",((IntVar[][]) model.getVariable("port_ki"))[k][i+1],"*",nbRotations),
									getModel().getChocoModel().arithm(prodPortRotation,"+",((IntVar[][]) model.getVariable("rot_ki"))[k][i],"=",indexCtimeOutKi[k][i])
									)


							);					
		    	
		    }}}
		
		for (int k : commoditiesWithTt) {
				for (int i = 0; i <= tsmax; i++) {
					getModel().getChocoModel().ifThen(
							getModel().getChocoModel().arithm(((IntVar[][]) model.getVariable("rot_ki"))[k][i],"=",-1),
							getModel().getChocoModel().arithm(indexCtimeOutKi[k][i],"=",nbPorts*nbRotations+nbRotations)
							
							
							);
				}}

		for(int r=0;r<nbRotations;r++) {
			for (int k : commoditiesWithTt) {
				for (int i = 0; i <= tsmax; i++) {
    		      getModel().getChocoModel().ifThen(
							getModel().getChocoModel().and(
									
									getModel().getChocoModel().arithm(((IntVar[][]) model.getVariable("port_ki"))[k][i+1],"=",((IntVar[]) model.getVariable("dep_r"))[r]),
									
									getModel().getChocoModel().arithm(((IntVar[][]) model.getVariable("rot_ki"))[k][i],"=",r)

								),

							
							getModel().getChocoModel().arithm(indexCtimeOutKi[k][i],"=",nbPorts*nbRotations+r)
							);					
		    	
		    }}}

		for (int k : commoditiesWithTt) {
			for (int i = 0; i <= tsmax; i++) {
				
				getModel().getChocoModel().element(ctimeOutIk[i][k], timeIn, indexCtimeOutKi[k][i],0).post();
				
			}
			}
	
	   //the time when commodity k enters in /leave the ith rotation  and time modulo one week 
		for (int k : commoditiesWithTt) {
		    for (int i = 0; i <= tsmax; i++) {
		    	getModel().getChocoModel().table(new IntVar[]{ctimeInIk[i][k],ctimeInModIk[i][k]}, tableMod,"CT+").post();
		    	getModel().getChocoModel().table(new IntVar[]{ctimeOutIk[i][k],ctimeOutModIk[i][k]}, tableMod,"CT+").post();

		    }
		}	
		
		
		
	 //computes the time elapsed between the loading and unloading for the ith rotation (TT.3a)
		
		for (int k : commoditiesWithTt) {
		    for (int i = 0; i <= tsmax; i++) {
		    	getModel().getChocoModel().ifThen(				    			
		    			getModel().getChocoModel().arithm(ctimeInIk[i][k], "<=", ctimeOutIk[i][k]),
						getModel().getChocoModel().arithm(deltaIk[i][k], "=",ctimeOutIk[i][k],"-", ctimeInIk[i][k])


		    			);	
		         }
		    }
		
		
	//computes the time elapsed between the loading and unloading for the ith rotation (TT.3b)
		
		for (int k : commoditiesWithTt) {
		    for (int i = 0; i <= tsmax; i++) {
		        for (int r = 0; r < nbRotations; r++) {
		            IntVar ctimeInIkSousCtimeOutIk = getModel().getChocoModel().intVar(-deltaIk[i][k].getUB(),deltaIk[i][k].getUB()) ;
		            IntVar sum = getModel().getChocoModel().intVar(0,deltaIk[i][k].getUB()) ;
			    	getModel().getChocoModel().ifThen(	
			    			getModel().getChocoModel().and(
			    					getModel().getChocoModel().arithm(ctimeInIk[i][k], ">", ctimeOutIk[i][k]),
			    					getModel().getChocoModel().arithm(((IntVar[][]) model.getVariable("rot_ki"))[k][i], "=",r)
			    					),
			    			getModel().getChocoModel().and(

			    					getModel().getChocoModel().arithm(ctimeInIkSousCtimeOutIk, "=",ctimeOutIk[i][k],"-", ctimeInIk[i][k]),
			    					getModel().getChocoModel().arithm(sum, "=",ctimeInIkSousCtimeOutIk,"+",((IntVar[]) model.getVariable("T_r"))[r]),
			    					getModel().getChocoModel().arithm(deltaIk[i][k], "=",sum))

			    			);	
		        }
		    }
		}
		
	    ClauseAdder clauseAdder = new ClauseAdder(this.model);

		
	//we compute the time elapsed between the end of the ith rotation and the beginning  of the i + 1th. (TT.4)
		for (int k : commoditiesWithTt) {
		    for (int i = 0; i < tsmax; i++) {
		    	getModel().getChocoModel().table(new IntVar[]{lessThanKi[k][i+1], ctimeOutModIk[i][k], ctimeInModIk[i+1][k],DeltaIk[i][k]}, tableDelta,"CT+").post();

		    }
		}
		
	// ensures that if the value of  nbr_k[k] is less than or equal to i+1, then  DeltaIk[i][k] must be equal to zero, (TT.5)
		for (int k : commoditiesWithTt) {
		    for (int i = 0; i < tsmax; i++) {
          clauseAdder.addImplication (((IntVar[]) model.getVariable("nbr_k"))[k], Operator.LE, i+1, DeltaIk[i][k], Operator.EQ, 0);
		    }
		}
		
	//assigns less_than_ik w.r.t. nbr_k(TT.6)

	    for (int k = 0; k < nbCommodities; k++) {
		    for (int i = 0; i <= tsmax; i++) {
		    	clauseAdder.addEquivalence(lessThanKi[k][i], Operator.EQ, 1,((IntVar[]) model.getVariable("nbr_k"))[k], Operator.GT, i);
		    
		    }
		}
	//computes the total transit time of commodity k from origin to destination (TT.7)

		int[] ttMax = new int[getModel().getInstance().getNumberCommodityWithTt()];
		for (int k : commoditiesWithTt) {
		    ttMax[k] = getModel().getInstance().getCommodities().get(k).getTransitTime();
		}
		
				for (int k : commoditiesWithTt) {
				    IntVar[] deltaK = new IntVar[tsmax];
				    IntVar[] DeltaK = new IntVar[tsmax];
				    for (int i = 0; i < tsmax; i++) {
				        deltaK[i] = deltaIk[i][k];
				        DeltaK[i] = DeltaIk[i][k];
				    }
				    getModel().getChocoModel().sum(ArrayUtils.append(deltaK, DeltaK), "<=",ttMax[k]).post();
				}
		



	}
		
		
		
		
		
		
		
		
	


}