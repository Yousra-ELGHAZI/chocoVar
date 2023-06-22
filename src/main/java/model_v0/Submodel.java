package model_v0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.chocosolver.solver.variables.IntVar;

public abstract class  Submodel {
	
	 /** This class allows to represent a part of model with its variables and its constraint  */

	
    protected Model model=new Model();										//instance of Model
    protected List<String> infra = new ArrayList<String>();                 //the list of infrastructure
    protected int pcf; 												        // the number of ports + 2*canals +two factitious ports	
    protected int nbRotations;									    		//the number of retation to creat
    protected int nbPorts;													//the number of port
    protected int nbCanals;													//the number of canals
    protected int nbInfra;													// the number  of ports + 2*canals
    protected int nbCommodities;											// the number of commodities	
    protected int nbCommoditiesWithTt;										// the number of commodities with transit time	
    protected int nbVessels;												// the number of vessels 
    protected int tsmax;													//the maximal number of transshipments for a commodity that we allow	
    protected HashMap<String, Object> variables;  						    // Map of variables indexed by label 

    
    
    /**
     * Constructor for creating a Submodel object
     * @param model  The Model object
     */
    public Submodel(Model model) {
    	
        if (model instanceof Model) {
            this.model = model;
            
        } else {
            throw new IllegalArgumentException("The model is not valid");
        }
        this.variables = new java.util.HashMap<>();
        
		this.infra.addAll(model.getInstance().getPorts());
		this.infra.addAll(model.getInstance().getCanals());
		this.infra.addAll(model.getInstance().getCanals());
		this.pcf=infra.size()+2;
		this.nbRotations=model.getNumber("rotations");     		
		this.nbPorts=model.getNumber("ports");					
		this.nbCanals=model.getNumber("canals");					
		this.nbInfra=infra.size();
		this.nbCommodities=model.getNumber("commodities");
		this.nbCommoditiesWithTt=model.getNumber("commoditiesWithTt");
		this.nbVessels=model.getNumber("vessels");
		this.tsmax=model.getNumber("tsmax");
    }
    
    
	/**
	 * Abstract method for defining the variables of the submodel
	 */
	public abstract void defineVariables(); 
	
	
	
	/**
	 * Abstract method for defining the Constraints of the submodel
	 */
	public abstract void defineConstraints();

	
	
    /**
     * @param label  the label of the variable
     * @param var the variable 
     * adds the variables var to the submodel under the given label
     */
    public void addVariables(String label,Object var)  {
        if (variables.containsKey(label)) {
            throw new IllegalArgumentException("The variables " + label + " are already defined");
        } else {
            variables.put(label, var);
        }
    }
    
    
    
    
    /**
     * @param label of the variable 
     * @return  the variables corresponding to the given label
     */
    
    public Object getVariable(String label) {
        if (variables.containsKey(label)) {
            return variables.get(label);
        } else {
            return new IntVar[0];
        }
    }
    
    
    /**
     * returns the model to which the submodel is related
     * @return  the model to which the submodel is related
     */
    public Model getModel() {
        return model;
    }
    
    
    
    
    
    
	/**
	 * @return returns list of variables
	 */
	public HashMap<String, Object> getVariables() {
		return variables;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
