package model_v0;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;


public class CostSubmodel extends Submodel{
	private IntVar[][] consRp ;								//the fuel consumption for rotation r depending on the speed when going from port/canal p to port/canal s(p)
	private IntVar[][] cRp;									//the costs for sailing from port/canal p to s_rp and calling at port p for rotation r
    private IntVar[] teuTsP;								//the number of TEU that must be transshipped at port p

	
		
	int speedStep=1;										//step of speed	
	private Tuples tableConsumption ;    					//fuel consumption per rotation$
	private HashSet<Integer> consValues ;			
	
	private int hmax; 							   		  //the maximal number of hours that a rotation may last.	
	private int maxFuelCost;							 //the  maximum fuel cost
	private int sum;
	
	private  Set<Integer> allLoads;						  //all possible loads	
	private int maxKv;									  //the maximum capacity of vessels whatever the vessel type
	private int  sumK;									  //the number of TEU for all the commodities
	private List<Integer> quantity;					      //quantity of commodities		

    private ArrayList<ArrayList<Integer>> sumPcP;	// stores the waiting times of vessels

	/**
	 * Constructor for creating a CostSubmodel object
	 * @param model The Model object
	 */
	public CostSubmodel(Model model) {
		super(model);
		this.hmax=model.getHmax();
		 //compute fuel consumption per rotation
		tableConsumption = new Tuples(true);
		tableConsumption.add(0, 0, 0);
		consValues = new HashSet<>();
		consValues.add(0);
    	List<String> vesselTypes = new ArrayList<>(getModel().getInstance().getVesselTypes());  //convert a Set to a List to access the index of vessels.
		
		for (int v = 1; v <= nbVessels; v++) {
		    String vt = vesselTypes.get(v-1);

		    tableConsumption.add(v, 0, 0);

		    for (int speedV = getModel().getInstance().getVesselType(vt).getMinimalSpeed(); speedV <= getModel().getInstance().getVesselType(vt).getMaximalSpeed(); speedV += speedStep) {
		        int c = AuxiliaryFunctions.computeConsumption(getModel().getInstance(), v-1, speedV);

		        tableConsumption.add(v, speedV, c);
		        consValues.add(c);
		    }
		}
		
	  //we get the information about quantity of commodities
	    this.quantity= AuxiliaryFunctions.getQuantity(model.getInstance());

	  //the maximum capacity of vessels whatever the vessel type
	    this.maxKv=AuxiliaryFunctions.getMaxKv(model.getInstance());	 
	        
      //the number of TEU for all the commodities
	    this.sumK = AuxiliaryFunctions.getSumK(model.getInstance());
	    	
      //we compute all possible loads
	    this.allLoads = AuxiliaryFunctions.computeAllPossibleLoads(quantity);
	
        
     	 sumPcP = new ArrayList<ArrayList<Integer>>();

        for (String name:getModel().getInstance().getPorts()) {
            ArrayList<Integer> innerList = new ArrayList<Integer>();
        	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
                innerList.add(getModel().getInstance().getPort(name).getCallCost(nameVessel));

            }
        	  sumPcP.add(innerList);
        }
        for (String name:getModel().getInstance().getCanals()) {
            ArrayList<Integer> innerList = new ArrayList<Integer>();
        	  for (String nameVessel:getModel().getInstance().getVesselTypes()) {
        		  innerList.add(getModel().getInstance().getCanal(name).getCanalCost(nameVessel));

            }
        	  sumPcP.add(innerList);
        }
        ArrayList<Integer> fuelsCost = new ArrayList<>();
        fuelsCost.add(0);
        for (String name:getModel().getInstance().getFuelTypes()) {
        	fuelsCost.add(getModel().getInstance().getFuelType(name).getCost()/10);
        	
          }	

        maxFuelCost = Collections.max(fuelsCost);

		int[] sumPcPArr = sumPcP.stream().flatMap(innerList -> innerList.stream()).mapToInt(Integer::intValue).toArray();
        sum =0;
        for(int i=0;i<sumPcPArr.length;i++) {
        	sum+=sumPcPArr[i];
        	
        }
	}

	@Override
	public void defineVariables() {
	    //the fuel consumption for rotation r depending on the speed when going from port/canal p to port/canal s(p)
		 consRp= getModel().getChocoModel().intVarMatrix("cons_rp", nbRotations, nbInfra,consValues.stream().mapToInt(Integer::intValue).toArray());
	     addVariables("cons_rp", consRp);
	     
	     
	     //the costs for sailing from port/canal p to s_rp and calling at port p for rotation r
	     cRp = getModel().getChocoModel().intVarMatrix("c_rp", nbRotations, nbInfra, 0,(hmax*Collections.max(consValues)*maxFuelCost+sum)+1);

	     addVariables("c_rp", cRp);
	     
	      //the number of TEU that must be transshipped at port p
	      if (model.isWithRefinedDomains()) {
	          teuTsP = getModel().getChocoModel().intVarArray("teuTs_p", nbPorts,allLoads.stream().mapToInt(i -> i).toArray());
	      } else {

	          teuTsP = getModel().getChocoModel().intVarArray("teuTs_p", nbPorts, 0,  Math.min(sumK, maxKv) + 1);
	      }
	      addVariables("teuTs_p", teuTsP);


		
	}

	@Override
	public void defineConstraints() {
		
		
        //the fuel consumption for sailing from port/canal p to s_rp depending on the speed speed_rp (O.1)
		for (int r = 0; r < nbRotations; r++) {
		    for (int p = 0; p <nbInfra; p++) {
		    	getModel().getChocoModel().table(new IntVar[]{((IntVar[]) model.getVariable("v_r"))[r], ((IntVar[][]) model.getVariable("speed_rp"))[r][p], consRp[r][p]}, tableConsumption).post();
		    }
		}
		
		
		// the costs for sailing from port/canal p to s_rp and calling at port p for any rotation r (O.2)

		for (int r = 0; r < nbRotations; r++) {
		    for (int p = 0; p <nbInfra; p++) {
   			    //prd=pc_pr[p,r]*(s_rp[r,p]!=p)
		    	IntVar prd = getModel().getChocoModel().intVar(((IntVar[][])model.getVariable("pc_pr"))[p][r].getLB(),((IntVar[][])model.getVariable("pc_pr"))[p][r].getUB());   			   
				getModel().getChocoModel().times(((IntVar[][])model.getVariable("pc_pr"))[p][r],getModel().getChocoModel().arithm(((IntVar[][])model.getVariable("s_rp"))[r][p], "!=", p).reify(),prd).post();
		    	//time1=fp_r[r]*cons_rp[r,p]
   			    IntVar times1 = getModel().getChocoModel().intVar(((IntVar[])model.getVariable("fp_r"))[r].getLB()*consRp[r][p].getLB(),((IntVar[])model.getVariable("fp_r"))[r].getUB()*consRp[r][p].getUB());
   			    getModel().getChocoModel().times(((IntVar[])model.getVariable("fp_r"))[r], consRp[r][p],times1).post();
   			   //time2=time1*st_rp[r,p]=fp_r[r]*cons_rp[r,p]*st_rp[r,p])
		    	IntVar times2 = getModel().getChocoModel().intVar(((IntVar[][])model.getVariable("st_rp"))[r][p].getLB()*times1.getLB(),((IntVar[][])model.getVariable("st_rp"))[r][p].getUB()*times1.getUB());
	   			getModel().getChocoModel().times(times1,((IntVar[][])model.getVariable("st_rp"))[r][p],times2).post();
			    getModel().getChocoModel().arithm(prd,"+",times2,"=",cRp[r][p]).post();


		    }
		}
		

		
        // the number of TEU that must be transshipped at port p (O.3)
        for (int p = 0; p < nbPorts; p++) {
            ArrayList<IntVar> boolVarsList = new ArrayList<IntVar>();
            for (int k = 0; k < nbCommodities; k++) {
                for (int r = 0; r < nbRotations; r++) {
    	                if (p != AuxiliaryFunctions.getPol(model.getInstance()).get(k) && p != AuxiliaryFunctions.getPod(model.getInstance()).get(k)) {
    	                IntVar bol=getModel().getChocoModel().intVar(teuTsP[p].getLB(),teuTsP[p].getUB());
	                	getModel().getChocoModel().times(((BoolVar[][][])model.getVariable("to_kpr"))[k][p][r], getModel().getChocoModel().intVar(quantity.get(k)), bol).post();
                        boolVarsList.add(bol);

                    }
                }
            }
            IntVar[] boolVarsArray = boolVarsList.toArray(new IntVar[boolVarsList.size()]);
            getModel().getChocoModel().sum(boolVarsArray, "=", teuTsP[p]).post();
        }
	
	}


}

