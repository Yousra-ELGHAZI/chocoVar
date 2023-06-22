package model_v0;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import tools.Operator;
import tools.ClauseAdder;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;
public class CargoFlowSubmodel extends Submodel{
	
	  /** This class allows to represent a part of model related to cargo flow   */

	
	private IntVar[] alphaK ;							//is set to 1 if commodity k is accepted in the network, 0 otherwise
	private IntVar[][] rotKi ; 							//the index of the ith rotation used to carry commodity k, -1 if the transportation of commodity k requires less than i rotations
	private IntVar[][] portKi;							//the ith port in which commodity k is loaded, unloaded or transshipped, -1 if the transportation of commodity k requires less than i rotations.
	private IntVar[] nbrK;								//the number of rotations in which commodity k is loaded, unloaded or transshipped. 
	private List<Integer> pol = new ArrayList<>();    	//origin  ports of commodity
	private List<Integer> pod = new ArrayList<>();    	//destination ports of commodity
	private BoolVar[][][]toKpr;							//true if the commodity k is unloaded at port p from rotation r
	private BoolVar[][][] fromKpr ;						//true if the commodity k is loaded at port p in rotation r
	private IntVar[] podk;        						//the destination port if the commodity is allowed, -1 otherwise
    
	/**
	 * Constructor for creating a CargoFlowSubmodel object
	 * @param model The Model object
	 */
	public CargoFlowSubmodel(Model model) {
		super(model);
		
        //origin and destination ports of commodity
        pol=AuxiliaryFunctions.getPol(model.getInstance());
        pod=AuxiliaryFunctions.getPod(model.getInstance());

	}

	
	
	
	
	@Override
	public void defineVariables() {
		
		//alpha_k is set to 1 if commodity k is accepted in the network, 0 otherwise
		alphaK = getModel().getChocoModel().intVarArray("alpha_k", nbCommodities, 0, 1);
		addVariables("alpha_k", alphaK);

        //the index of ith rotation used to carry commodity k, -1 if the transportation of commodity k requires less than i rotations
         rotKi = new IntVar[nbCommodities][tsmax+1];
        for (int i = 0; i < nbCommodities; i++) {
            for (int j = 0; j <= tsmax; j++) {
         	   rotKi[i][j] =  getModel().getChocoModel().intVar("rot_ki[" + i + "][" + j + "]", IntStream.range(-1, nbRotations).toArray());
            }
        }
        addVariables("rot_ki", rotKi);

        
        //the ith port in which commodity k is loaded, unloaded or transshipped, 0 if the transportation of commodity k requires less than i rotations
        portKi = new IntVar[nbCommodities][tsmax + 2];
        for (int k = 0; k < nbCommodities; k++) {
            for (int i = 0; i < tsmax + 2; i++) {
                if (i == 0) {
                    portKi[k][i] = getModel().getChocoModel().intVar("port_ki[" + k + "][" + i + "]",new int[] {-1,pol.get(k)});

                    
                } else if (i == tsmax + 1) {
                    portKi[k][i] = getModel().getChocoModel().intVar("port_ki[" + k + "][" + i + "]",new int[] {-1,pod.get(k)});

                } else {
                    Set<Integer> set = IntStream.rangeClosed(-1, nbPorts - 1).boxed().collect(Collectors.toSet());
                    set.remove(pol.get(k));
                    portKi[k][i] = getModel().getChocoModel().intVar("port_ki[" + k + "][" + i + "]", set.stream().mapToInt(Integer::intValue).toArray());
                }
            }
        }
        addVariables("port_ki", portKi);

        
        //the number of rotations in which commodity k is loaded, unloaded or transshipped
         nbrK = getModel().getChocoModel().intVarArray("nbr_k", nbCommodities, 0, tsmax+1);
         
         addVariables("nbr_k", nbrK);
         
         
         
         //true if the commodity k is loaded at port p in rotation r, False otherwise
         fromKpr = new BoolVar[nbCommodities][nbPorts][nbRotations];
         for(int k=0; k<nbCommodities; k++){
           for(int p=0;p <nbPorts; p++){
             for(int r=0; r<nbRotations; r++){
               fromKpr[k][p][r] = getModel().getChocoModel().boolVar("from_kpr["+k+"]["+p+"]["+r+"]");
             }
           }
         }
         addVariables("from_kpr", fromKpr);
         
         

         //true if the commodity k is unloaded at port p in rotation r, False otherwise
         toKpr = new BoolVar[nbCommodities][nbPorts][nbRotations];
         for(int k=0; k<nbCommodities; k++){
             for(int p=0;p <nbPorts; p++){
               for(int r=0; r<nbRotations; r++){
        	      toKpr[k][p][r] = getModel().getChocoModel().boolVar("to_kpr["+k+"]["+p+"]["+r+"]");
        	    }
        	  }
        	}
         addVariables("to_kpr", toKpr);

       //the destination port if the commodity is allowed, -1 otherwise
	    podk = new IntVar[nbCommodities];
	    for (int k = 0; k < nbCommodities; k++) {
	        podk[k] = getModel().getChocoModel().intVar("podk["+k+"]", new int[] { -1, pod.get(k) });
	    }
	    addVariables("podk", podk);

	}
	

	@Override
	public void defineConstraints() {
	ClauseAdder clauseAdder = new ClauseAdder(this.model);

    //if commodity k is accepted in the network, there is at least one rotation in which it is loaded, unloaded or transshipped (C.1)
    for (int k = 0; k < nbCommodities; k++) {
      clauseAdder.addEquivalence(alphaK[k], Operator.EQ, 1, nbrK[k], Operator.GT, 0);
    }

    //if commodity k is accepted in the network, the first port is necessarily pol(k) (C.2)
    for (int k = 0; k < nbCommodities; k++) {
      clauseAdder.addEquivalence(alphaK[k], Operator.EQ, 1, portKi[k][0], Operator.EQ, pol.get(k));
    }
			
    //if commodity k is not accepted, there is no first port (C.3)
    for (int k = 0; k < nbCommodities ;k++) {
      clauseAdder.addEquivalence(alphaK[k], Operator.EQ, 0, portKi[k][0], Operator.EQ, -1);
    }
    
    //if commodity k is accepted, the destination port is necessarily pod(k) (C.4)
    for (int k = 0; k < nbCommodities ;k++) {
      clauseAdder.addEquivalence(alphaK[k], Operator.EQ, 1, podk[k], Operator.EQ, pod.get(k));
    }
				
    //the last used port must be pod(k) (C.5)		
    IntVar[][] var = new IntVar[nbCommodities][tsmax+2];
    for (int k = 0; k < nbCommodities; k++) {
        var[k][0] = podk[k];
        for (int i = 1; i < tsmax+2; i++) {
            var[k][i] = portKi[k][i];
        }
    }
    
    for (int k = 0; k < nbCommodities; k++) {
      getModel().getChocoModel().element(podk[k], var[k], nbrK[k], 0).post();
    }
          
				
				
    //for all the ith rotations such that i>=nbrK, there is no rotation (C.6)

    for (int k = 0; k < nbCommodities; k++) {
      for (int i = 0; i <= tsmax; i++) {
        clauseAdder.addEquivalence(nbrK[k], Operator.LE, i, rotKi[k][i], Operator.EQ, -1);
      }
    }
			     
    //for all the ith rotations such that i>nbrK, there is no port (C.7)

    for (int k = 0; k < nbCommodities; k++) {
        for (int i = 0; i <= tsmax+1; i++) {
          clauseAdder.addImplication (nbrK[k], Operator.LT, i, portKi[k][i], Operator.EQ, -1);
        }
    }

				
    //for rotation r, if there is no port at step i, then nbrk <= i, (C.8)

    for (int k = 0; k < nbCommodities; k++) {
        for (int i = 0; i <= tsmax+1; i++) {
            clauseAdder.addImplication (portKi[k][i], Operator.EQ, -1, nbrK[k], Operator.LE, i);
        }
    }
		
    //a commodity k cannot visit several times the same port. (C.9)

    for (int k = 0; k < nbCommodities; k++) {
            IntIterableRangeSet set = new IntIterableRangeSet(getModel().getChocoModel().intVar(-1));
            getModel().getChocoModel().allDifferentUnderCondition(portKi[k],x -> !set.intersect(x), true).post();
    }	
			  
			  
	 //two rotations used consecutively for carrying commodity k must be different (C.10)
				
    /*for (int k = 0; k < nbCommodities; k++) {
				    for (int i = 0; i <tsmax; i++) {
				        getModel().getChocoModel().ifThen(
				        		getModel().getChocoModel().arithm(alphaK[k], "=", 1),
				        		getModel().getChocoModel().arithm(rotKi[k][i],"!=", rotKi[k][i+1])

				        );
				    }
				}*/

				
      //the latter can be replaced by the following ones if we do not allow a commodity to use a rotation several times.(C.11)
				
				for (int k = 0; k < nbCommodities; k++) {
		            IntIterableRangeSet set = new IntIterableRangeSet(getModel().getChocoModel().intVar(-1));
		
		            getModel().getChocoModel().allDifferentUnderCondition(rotKi[k],x -> !set.intersect(x), true).post();
				}					
				
      //link fromKpr variables to cargo flows (C.12)
				
				for (int k = 0; k < nbCommodities; k++) {
				    for (int p = 0; p < nbPorts; p++) {
				        for (int r = 0; r < nbRotations; r++) {
				            IntVar[] sum = new IntVar[tsmax + 1];
				            for (int i = 0; i <= tsmax; i++) {
				                BoolVar condition = getModel().getChocoModel().and(
				                		getModel().getChocoModel().arithm(portKi[k][i], "=", p),
				                		getModel().getChocoModel().arithm(rotKi[k][i], "=", r)
				                ).reify();
				                sum[i] = condition;
				            }
                    getModel().getChocoModel().sum(sum, "=", fromKpr[k][p][r]).post();
				        }
				    }
				}

				
	  //link toKpr variables to cargo flows (C.13)
				for (int k = 0; k < nbCommodities; k++) {
				    for (int p = 0; p < nbPorts; p++) {
				        for (int r = 0; r < nbRotations; r++) {
				            IntVar[] sum = new IntVar[tsmax];
				            for (int i = 0; i < tsmax; i++) {
				                BoolVar condition = getModel().getChocoModel().and(
				                		getModel().getChocoModel().arithm(portKi[k][i+1], "=", p),
				                		getModel().getChocoModel().arithm(rotKi[k][i], "=", r)
				                ).reify();
				                sum[i] = condition;
				            }
                    getModel().getChocoModel().sum(sum, "=", toKpr[k][p][r]).post();
				        }
				    }
				}



	}
	









}
