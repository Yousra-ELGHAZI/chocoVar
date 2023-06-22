package model_v0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tools.Operator;
import tools.ClauseAdder;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class CargoFlowRotationSubmodel extends Submodel{
	
	
	/**  This class allows to represent a part of model related to the link between cargo flow and rotations and routes  */	
	
	
	/**
	 * Constructor for creating a CargoFlowmodel object.
	 * 
	 * @param model The model object 
	 */
	public CargoFlowRotationSubmodel(Model model) {
		super(model);

	}
	
	
	@Override
	public void defineVariables() {
		
	}

	
	@Override
	public void defineConstraints() {

		ClauseAdder clauseAdder = new ClauseAdder(this.model);
		
		//the port for which commodity k enters in the ith rotation belongs to the rotation (CR.1)
		for(int k = 0; k < nbCommodities; k++) {
			  for(int p = 0; p < nbPorts; p++) {
			    for(int r = 0; r < nbRotations; r++) {
            clauseAdder.addImplication (((BoolVar[][][]) model.getVariable("from_kpr"))[k][p][r], Operator.EQ, 1, ((IntVar[][]) model.getVariable("s_rp"))[r][p], Operator.NE, p);
			    }
			  }
			}
		
		
		//the port for which commodity k leaves the ith rotation belongs to the rotation (CR.2)
		for(int k = 0; k < nbCommodities; k++) {
			  for(int p = 0; p < nbPorts; p++) {
			    for(int r = 0; r < nbRotations; r++) {
			    	clauseAdder.addImplication (((BoolVar[][][]) model.getVariable("to_kpr"))[k][p][r], Operator.EQ, 1, ((IntVar[][]) model.getVariable("s_rp"))[r][p], Operator.NE, p);
			    }
			  }
			}
		
		//if port p belongs to rotation r, there is at least a commodity k that is loaded, unloaded or transshipped at port p  (CR.3)

		for (int r = 0; r < nbRotations; r++) {
	        for (int p = 0; p < nbPorts; p++) {
	           clauseAdder.addImplication(((IntVar[][]) model.getVariable("s_rp"))[r][p], Operator.NE, p,
	        		     		   getModel().getChocoModel().among(getModel().getChocoModel().intVar(1,((IntVar[][]) model.getVariable("port_ki")).length), Arrays.stream(((IntVar[][]) model.getVariable("port_ki"))).flatMap(Arrays::stream).toArray(IntVar[]::new),
	        		     				  new int[]{p}).reify(), Operator.EQ, 1);
	        }
	    }
		//if port p belongs to rotation r, there is at least a commodity k that is carried on rotation r (CR.4)

	    for (int r = 0; r < nbRotations; r++) {
	        for (int p = 0; p < nbPorts; p++) {
	           clauseAdder.addImplication(((IntVar[][]) model.getVariable("s_rp"))[r][p], Operator.NE, p,
	        		   
	        		   getModel().getChocoModel().among(getModel().getChocoModel().intVar(1,((IntVar[][]) model.getVariable("rot_ki")).length), Arrays.stream(((IntVar[][]) model.getVariable("rot_ki"))) .flatMap(Arrays::stream).toArray(IntVar[]::new),
	        				   new int[]{r}).reify(), Operator.EQ, 1);
	        }
	    }
	    
	    //if rotation r is not used, no commodity is carried on this rotation (CR.5)
	    for (int r = 0; r < nbRotations; r++) {
	        for (int k = 0; k< nbCommodities; k++) {
	           clauseAdder.addEquivalence (((IntVar[]) model.getVariable("v_r"))[r], Operator.EQ, 0,
	        		   getModel().getChocoModel().among(getModel().getChocoModel().intVar(0), Arrays.stream(((IntVar[][]) model.getVariable("rot_ki"))) .flatMap(Arrays::stream).toArray(IntVar[]::new),
	        				   new int[]{r}).reify(), Operator.EQ, 1);
	        }
	    }
	    
	    
	    //ensures that the number of rotations that uses a port p does not exceed the maximum between  "the set of commodities for which p is the origin port "and "the set of commodities for which p is the  destination port". (CR.6)

	    ArrayList<Integer>  nbCommoditiesIn=new ArrayList<Integer>();
	    ArrayList<Integer>  nbCommoditiesOut=new ArrayList<Integer>();

		 List<Integer> pol = new ArrayList<>();    	//origin  ports of commodity
		 List<Integer> pod = new ArrayList<>();    	//destination ports of commodity
		 
		    for (int p = 0; p < nbPorts; p++) {
		    	nbCommoditiesIn.add(0);
		    	nbCommoditiesOut.add(0);
		    	
		    	
		    }

	        pol=AuxiliaryFunctions.getPol(model.getInstance());
	        pod=AuxiliaryFunctions.getPod(model.getInstance());
	        
			for (int x : pod) {
				nbCommoditiesIn.set(x, nbCommoditiesIn.get(x) + 1);
			}
			for (int x : pol) {
				nbCommoditiesOut.set(x, nbCommoditiesOut.get(x) + 1);
			}

			 for (int p = 0; p < nbPorts; p++) {
			   	    BoolVar[] exist = getModel().getChocoModel().boolVarArray(nbRotations);
			   	    for (int r = 0; r < nbRotations; r++) {
			   	    	getModel().getChocoModel().ifThen(
			   	    				getModel().getChocoModel().arithm(((IntVar[][]) model.getVariable("s_rp"))[r][p], "!=", p),
			   	    				getModel().getChocoModel().arithm(exist[r], "=", 1));

			   	    }
			   	 getModel().getChocoModel().sum(exist, "<=", Math.max(nbCommoditiesIn.get(p),nbCommoditiesOut.get(p))).post(); 
			   	    
			   	}
						
						
				

	}



}
