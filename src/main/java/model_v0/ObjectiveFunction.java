package model_v0;

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.variables.IntVar;

public class ObjectiveFunction extends Submodel {
	
	  /** This class allows to represent a part of model related to objective function */


	
	private IntVar objectiveRevenue; 							//stores the revenue
	private IntVar objectiveFuel ; 								//stores fuel and call cost
	private IntVar objectiveOperationalCost;					//stores operational cost
	private IntVar objectiveTsCost;								//stores transshipment cost
	private IntVar objective;								    //stores the global objective		
	private List<Integer> ts=new ArrayList<Integer> ();         //list to store the transshipment cost per TEU

	
	
	/**
	 * Constructor for creating a ObjectiveFunction object
	 * @param model The Model object
	 */
	public ObjectiveFunction(Model model) {
	   super(model);
	   
	   //we get the the transshipment cost per TEU
		for(int p = 0; p < nbPorts; p++){
			for (String name : model.getInstance().getPorts()) {
			    ts.add(model.getInstance().getPort(name).getTransshipmentCost());
			}


		}
	 
		}

		@Override
		public void defineVariables() {

			
		}
		
		

		@Override
		public void defineConstraints() {
			
			
	        //stores revenue
			
			IntVar[] productRevenue = new IntVar[nbCommodities];
			for (int k = 0; k < nbCommodities; k++) {
			    productRevenue[k] = getModel().getChocoModel().intVar("product_" + k,((IntVar[]) model.getVariable("alpha_k"))[k].getLB() * AuxiliaryFunctions.getQuantity(model.getInstance()).get(k)* AuxiliaryFunctions.getRev(model.getInstance()).get(k),((IntVar[]) model.getVariable("alpha_k"))[k].getUB() *AuxiliaryFunctions.getQuantity(model.getInstance()).get(k)* AuxiliaryFunctions.getRev(model.getInstance()).get(k));
			    getModel().getChocoModel().times(((IntVar[]) model.getVariable("alpha_k"))[k], AuxiliaryFunctions.getQuantity(model.getInstance()).get(k)* AuxiliaryFunctions.getRev(model.getInstance()).get(k), productRevenue[k]).post();
			}
			
			objectiveRevenue= getModel().getChocoModel().intVar("objectiveRevenue",getLowerBound(productRevenue),getUpperBound(productRevenue));
			getModel().getChocoModel().sum(productRevenue, "=", objectiveRevenue).post();	
			
			
			//stores fuel and call cost

			IntVar[] sumFuel = new IntVar[nbInfra];
			for (int p = 0; p < nbInfra; p++) {
			    IntVar[] vars = new IntVar[nbRotations];
			    for (int r = 0; r < nbRotations; r++) {
			        vars[r] = ((IntVar[][]) model.getVariable("c_rp"))[r][p];

			    }
			    sumFuel[p] = getModel().getChocoModel().intVar("sum[" + p + "]", getLowerBound(vars), getUpperBound(vars));
			    getModel().getChocoModel().sum(vars, "=", sumFuel[p]).post();
			}
			 objectiveFuel = getModel().getChocoModel().intVar("objectiveFuel", getLowerBound(sumFuel),getUpperBound(sumFuel));

			getModel().getChocoModel().sum(sumFuel, "=", objectiveFuel).post();
			
			
			//stores Operational Cost

			IntVar[] productsOPCost = new IntVar[nbRotations];
			for (int r = 0; r < nbRotations; r++) {
			    productsOPCost[r] = getModel().getChocoModel().intVar("product[" + r + "]", ((IntVar[])model.getVariable("tc_r"))[r].getLB() *((IntVar[])model.getVariable("n_r"))[r].getLB(),((IntVar[])model.getVariable("tc_r"))[r].getUB() * ((IntVar[])model.getVariable("n_r"))[r].getUB());
			    getModel().getChocoModel().times(((IntVar[])model.getVariable("tc_r"))[r], ((IntVar[])model.getVariable("n_r"))[r], productsOPCost[r]).post();
			}
			IntVar sumProduct = getModel().getChocoModel().intVar("sumProduct", getLowerBound(productsOPCost),getUpperBound(productsOPCost)*nbRotations);
			getModel().getChocoModel().sum(productsOPCost, "=", sumProduct).post();
			
			objectiveOperationalCost = getModel().getChocoModel().intVar("objectiveOperationalCost",sumProduct.getLB()*7, sumProduct.getUB()*7);
			getModel().getChocoModel().times(sumProduct, getModel().getChocoModel().intVar(7), objectiveOperationalCost).post();
			
			
			
			
			//stores transshipment cost
			
			IntVar[] productTs = new IntVar[nbPorts];
			for (int p = 0; p < nbPorts; p++) {
				productTs[p] = getModel().getChocoModel().intVar("product_" + p,((IntVar[]) model.getVariable("teuTs_p"))[p].getLB() *ts.get(p),((IntVar[]) model.getVariable("teuTs_p"))[p].getUB() *ts.get(p));
			    getModel().getChocoModel().times(((IntVar[]) model.getVariable("teuTs_p"))[p], ts.get(p), productTs[p]).post();
			}
			 objectiveTsCost = getModel().getChocoModel().intVar("objectiveTsCost",getLowerBound(productTs),getUpperBound(productTs)*nbPorts); 
			getModel().getChocoModel().sum(productTs, "=", objectiveTsCost).post();
			
		
			
			
			

			//Objective function
			
			// Create an expression for the objective function

			IntVar revenueFuel = getModel().getChocoModel().intVar("revenueFuel", 0,objectiveRevenue.getUB()+objectiveFuel.getUB());
		//	IntVar operationalTsCost = getModel().getChocoModel().intVar("operationalTsCost", 0,objectiveOperationalCost.getUB()+objectiveTsCost.getUB());
			IntVar operationalTsCost = getModel().getChocoModel().intVar("operationalTsCost",objectiveOperationalCost.getLB()-objectiveTsCost.getUB(),objectiveOperationalCost.getUB()-objectiveTsCost.getLB());

			objective = getModel().getChocoModel().intVar("objective", 0,revenueFuel.getUB()+operationalTsCost.getUB());

			getModel().getChocoModel().arithm(objectiveRevenue, "-", objectiveFuel,"=",revenueFuel).post();
			getModel().getChocoModel().arithm(objectiveOperationalCost, "-", objectiveTsCost,"=",operationalTsCost).post();
			getModel().getChocoModel().arithm(revenueFuel, "-", operationalTsCost,"=",objective).post();
			
			
			// Set the objective to be maximized 
			getModel().getChocoModel().setObjective(getModel().getChocoModel().MAXIMIZE,objective);

			
		}
		
		
		
		



		/**
		 * @param variable the variable we want to find its Lower bound
		 * @return returns the Lower Bound of variable
		 */
		public int getLowerBound(IntVar[] variable) {
			int Lb=variable[0].getLB();
			for (int k = 1; k < variable.length; k++) {
				Lb=Lb+variable[k].getLB();
			}
			return Lb;
		}
		
		
		/**
		 * @param variable the variable we want to find its Upper bound
		 * @return returns the Upper Bound of variable
		 */
		public int getUpperBound(IntVar[] variable) {
			int Ub=variable[0].getUB();
			for (int k = 1; k < variable.length; k++) {
				Ub=Ub+variable[k].getUB();
			}
			return Ub;
		}
		
		



}
