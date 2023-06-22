package model_v0;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import tools.ClauseAdder;
import tools.Operator;

public class SymmetryBreakingSubmodel extends Submodel {
	
    private int symmetryBreakingPolicy;    	//0 if we add no breaking symmetries constraints to the model, 1 if we add B.1, 2 if we add B.2 or 3 if we add B.1 and B.2




	public SymmetryBreakingSubmodel(Model model){
		super(model);
		symmetryBreakingPolicy=model.getSymmetryBreakingPolicy();
		}

	@Override
	public void defineVariables() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defineConstraints() {
		
		
		//if a rotation is not used, the next one is not used too (B.1)
	    ClauseAdder clauseAdder = new ClauseAdder(this.model);
	    for (int r = 0; r < nbRotations - 1; r++) {
	    	//clauseAdder.addImplication(((IntVar[]) model.getVariable("v_r"))[r], Operator.EQ, 0, ((IntVar[]) model.getVariable("v_r"))[r+1], Operator.EQ, 0);
	    }
	    

		
		//rotations are sorted in decreasing order of their rotation time (B.2)
		getModel().getChocoModel().decreasing(((IntVar[]) model.getVariable("T_r")),0).post();
    
	}


}
