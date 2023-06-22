package model_v0;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import instance.Instance;

public class Model {
	
    /** This class allows to represent a model for the Liner Ship Network Design Problem */

    private  Instance instance;                                             //the instance to which the model is related
    private  org.chocosolver.solver.Model chocoModel;						//the CHOCO model
    private ArrayList<Submodel> submodels= new ArrayList<>();;				//the list of sub-models that compose the model
    private Map<String, Integer> numbers = new HashMap<String, Integer>();	//the number of elements for ports, canal
    
	HashMap<String, Object> Variables=new HashMap<String, Object>();
	private boolean  withRefinedDomains ;                    	//true if the model used refined domains, False otherwise
	private boolean allowedIfConstraint ;          				//true if the function if is allowed in intention constraints, False otherwise
	private double ratioMu;							 	     	//the ratio which allows us to convert the number of TEU to the number of containers we have to handle,	
	private int hmax;							   		     	//the maximal number of hours that a rotation may last.	
	private int speedStep;										//the step between two speeds	
    private int symmetryBreakingPolicy;    				 		//0 if we add no breaking symmetries constraints to the model, 1 if we add B.1, 2 if we add B.2 or 3 if we add B.1 and B.2
	private int  tNbv;								   		    //the version of table NbVessel used	

    
    
    /**
     * empty constructor
     */
    public Model() {
    	
	}
    

	/**
	 *  Constructor for creating a Model object
     * @param instance  the instance to which the model is related
     * @param rotationNumber  Number of rotations
     * @param transshipmentNumber Number of  transshipment
     */
    public Model(Instance instance, int rotationNumber, int transshipmentNumber,int hmax,double ratioMu,int speedStep,int tNbv,int symmetryBreakingPolicy,boolean withRefinedDomains,boolean allowedIfConstraint) {
        if (instance instanceof Instance) {
            this.instance = instance;
            this.withRefinedDomains=withRefinedDomains;
            this.allowedIfConstraint=allowedIfConstraint;
            this.hmax=hmax;
            this.ratioMu=ratioMu;
            this.speedStep=speedStep;
            this.tNbv=tNbv;
            this.symmetryBreakingPolicy=symmetryBreakingPolicy;
            
        } else {
            throw new IllegalArgumentException("The instance is not valid");
        }        this.chocoModel = new org.chocosolver.solver.Model();
        

        this.numbers.put("ports",instance.getPorts().size());
        this.numbers.put("canals",instance.getCanals().size());
        this.numbers.put("vessels", instance.getVesselTypes().size());
        this.numbers.put("commodities", instance.getCommodities().size());
        this.numbers.put("commoditiesWithTt", instance.getNumberCommodityWithTt());

        
        if (rotationNumber <= 0) {
            throw new IllegalArgumentException("The number of rotations is not valid");
        }else {
            this.numbers.put("rotations", rotationNumber);

        }
        if (transshipmentNumber <= 0) {
            throw new IllegalArgumentException("The number of transshipments is not valid");
        }else {
            this.numbers.put("tsmax", transshipmentNumber);

        }
    }

    
    /**
     *  adds the variables of each submodel to the model
     */
    public void addVariables() {
        for (Submodel sm : submodels) {
            sm.defineVariables();
        }
    }
    
    
    
    /**
     * adds the constraints of each submodel to the model
     */
    public void addConstraints() {
        for (Submodel sm : submodels) {
             sm.defineConstraints();
        }
    }
    
 
    
    
	/**
	 * returns the instance to which the model is relate
	 * @return the instance to which the model is related
	 */
	public Instance getInstance() {
		return this.instance;
	}
	
	
	
	/**
	 * returns the Choco Solver model
	 * @return  the Choco Solver model
	 */
	public org.chocosolver.solver.Model getChocoModel() {
		return this.chocoModel;
	}
	
	
	
	/**
	 * @param label of the element to search
	 * @return returns the number of elements for the given label
	 */
	public int getNumber(String label) {
	    if (numbers.containsKey(label)) {
	       return numbers.get(label);
	    } else { 
           throw new IllegalArgumentException("This element is not exist");
	       
	    }
	}

	


	 /**
	 * @param submodel
	 * adds a submodel to the model
	 */
	public void addSubmodel(Submodel submodel) {
	      /* Adds a submodel to the model */
	    this.submodels.add(submodel);
	}
	
	
	/**
	 * @param label of the element to search
	 * @return returns the variable that has the given label
	 */
	public Object getVariable(String label) {

		for(Submodel sub:submodels) {
			Variables.putAll(sub.getVariables());
		}
	    return Variables.get(label);
	}



	/**
	 * @return returns true  If allowedIfConstraint is true, otherwise it  returns false.
	 */
	public boolean isWithRefinedDomains() {
		return withRefinedDomains;
	}


	/**
	 * @return returns true  If allowedIfConstraint is true, otherwise it  returns false. 
	 */
	public boolean isAllowedIfConstraint() {
		return allowedIfConstraint;
	}


	/**
	 * @return returns the ratio
	 */
	public double getRatioMu() {
		return ratioMu;
	}


	/**
	 * @return returns the maximal number of hours that a rotation may last.
	 */
	public int getHmax() {
		return hmax;
	}

	/**
	 * @return returns the step between two speeds.
	 */
	public int getSpeedStep() {
		return speedStep;
	}

	/**
	 * @return returns the number of symmetry constraints to add to the model.
	 */
	public int getSymmetryBreakingPolicy() {
		return symmetryBreakingPolicy;
	}

	/**
	 * @return returns the version of table NbVessel used.	
	 */
	public int gettNbv() {
		return tNbv;
	}
	
	

}