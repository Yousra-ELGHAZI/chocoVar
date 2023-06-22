package model_v0;

import java.util.ArrayList;
import java.util.List;

import tools.Operator;
import tools.ClauseAdder;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class RotationSubmodel extends Submodel{
	
	
	 /**  This class allows to represent a part of model related to rotations and routes   */
	
	
    private IntVar[] vR;						//the type of vessels used for rotation r
    private IntVar[][] sRp ;					//the successor of port/canal p in the rotation r
    private IntVar[] lR;						//the length of the circuit related to rotation r
    	
    
	/**
	 * Constructor for creating a RotationSubmodel object
	 * @param model  The Model object
	 */
	public RotationSubmodel(Model model) {
		super(model);

	}
	
	

	@Override
    public void defineVariables() {
		
	        //the type of vessels used for rotation r (0 if rotation r is not used)
          vR= getModel().getChocoModel().intVarArray("v_r",nbRotations, 0,nbVessels);
	        addVariables("v_r", vR);
        
        
	        //sRp is set to p' if port/canal p' is the next port/canal after port/canal p in rotation r, p if port/canal p is not involved in rotation r
	        sRp = new IntVar[nbRotations][nbInfra];
	        
	        for (int i = 0; i < nbRotations; i++) {
	            for (int j = 0; j < nbInfra; j++) {
	                List<Integer> domain = new ArrayList<Integer>();
	                    domain.add(j);
	                    for (int k = 0; k < nbInfra; k++) {
	                        if (k != j && (getModel().getInstance().getDistances().getDistance(infra.get(j), infra.get(k))) > 0) {
	                            domain.add(k);
	                        }
	                    }
		            sRp[i][j] = getModel().getChocoModel().intVar("s_rp[" + i + "][" + j + "]", domain.stream().mapToInt(k -> k).toArray());

	            }       
	        }
	        addVariables("s_rp", sRp);
	        
	        //the number of nodes in the circuit
	        lR = getModel().getChocoModel().intVarArray("aux_subCircuitLength",nbRotations, 0, nbInfra);
	        addVariables("subCircuitLength", lR);


    }
	
	@Override
	public void defineConstraints() {

	//if rotation r is used, it must correspond to a circuit (R.1)
		
    for (int r = 0; r < nbRotations; r++) {
      getModel().getChocoModel().subCircuit(sRp[r],0,lR[r]).post();
    }
       
    //if rotation r is not used, an empty circuit will be created (R.2)
    ClauseAdder clauseAdder = new ClauseAdder(this.model);
		for (int r = 0; r < nbRotations; r++) {
      clauseAdder.addEquivalence(vR[r], Operator.EQ, 0, lR[r], Operator.EQ, 0);
    }
    
    //if rotation r is used, the length of the associated circuit is at least 3 (R.3)
		for (int r = 0; r < nbRotations; r++) {
      clauseAdder.addEquivalence(vR[r], Operator.NE, 0, lR[r], Operator.GE, 3);
    }
    
    //if rotation r is not used,no port or canal is exploited for this rotation (R.4)
    for (int r = 0; r < nbRotations; r++) {
        for (int p = 0; p < nbInfra; p++) {
          clauseAdder.addImplication(vR[r], Operator.EQ, 0, sRp[r][p], Operator.EQ, p);
        }
    }
    
    //if rotation r is used,it cannot involve only canals (R.5)
    BoolVar[] vars = new BoolVar[nbPorts];
    for (int r = 0; r < nbRotations; r++) {
        for (int p = 0; p <nbPorts ; p++) {
            vars[p] = getModel().getChocoModel().arithm(sRp[r][p], "!=",p).reify();
        }

        clauseAdder.addImplication (vR[r], Operator.NE, 0, getModel().getChocoModel().sum(vars, ">=",3).reify(), Operator.EQ, 1); 
    }
    
    
    	
	}

	
	

}
