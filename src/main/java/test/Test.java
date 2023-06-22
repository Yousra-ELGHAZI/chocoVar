package test;

import java.io.IOException;

import org.chocosolver.solver.Solution;
import org.chocosolver.solver.search.strategy.Search;

import model_v0.CargoFlowSubmodel;
import model_v0.FeatureSubmodel;
import model_v0.LoadSubmodel;
import model_v0.Model;
import model_v0.ObjectiveFunction;
import model_v0.AuxiliaryFunctions;
import model_v0.CargoFlowRotationSubmodel;
import model_v0.CostSubmodel;
import model_v0.RotationSubmodel;
import model_v0.SymmetryBreakingSubmodel;
import model_v0.TimeSubmodel;
import model_v0.TransitTimeSubmodel;
import model_v0.VesselAvailabilitySubmodel;
import instance.Instance;
import instance.InstanceReader;

public class Test {

	private static boolean  withRefinedDomains = false ;	//true if the model used refined domains, False otherwise
	private static boolean  allowedIfConstraint = false;  	//true if the function if is allowed in intention constraints, False otherwise
	private static double  ratioMu=0.54 ;					//the ratio which allows us to convert the number of TEU to the number of containers we have to handle,	
	private static int  hmax;								//the maximal number of hours that a rotation may last.	
	private static int nbRotations;							//the number of rotations to create	
	private static int tsmax;								//the number of transshipments
	private static String instanceName;						//the instance filename
	private static int speedStep=1;							//the step between two speeds
	private static int symmetryBreakingPolicy=3;			//the number of symmetry constraints to add to the model
	private static boolean saveResult=false;				//save the result to a file if true 
	private static int  tNbv=0;								//the version of table NbVessel used
	private static int t=7200;							    //lLimited runtime		


	public static void main(String[] args) throws IOException, Exception {
	   String usage = "-i <instance filename> -h<hmax> -r <mu parameter> -rot <# rotations> -s <symmetry breaking policy> [-ss <speed step>] -ts <# transshipment> -tNbv<tableNbVessel> ";
        for (int i = 0; i < args.length; i++) {
        	
            if(args[i].equals("-i")){
            	instanceName = args[i + 1];
            	
	            }else if(args[i].equals("-h")){
	                hmax = Integer.parseInt(args[i + 1]);
	                
	            	}else if (args[i].equals("-rot")) {
	            		nbRotations = Integer.parseInt(args[i + 1]);
	            		
	            		}else if (args[i].equals("-r")) {
	            			ratioMu =Double.parseDouble(args[i + 1]);
	            			
	            			}else if(args[i].equals("-ss")) {
	            				speedStep = Integer.parseInt(args[i + 1]);
	
	            				}else if(args[i].equals("-ts")) {
	            					tsmax = Integer.parseInt(args[i + 1]);
	
	            					}else if(args[i].equals("-S")) {
	            	                	saveResult= true;
	            					}else if(args[i].equals("-tNbv")) {
	            	                	tNbv= Integer.parseInt(args[i + 1]);
	            					}else if(args[i].equals("-t")) {
	            	                	t= Integer.parseInt(args[i + 1]);
	            					}

        }

		
		 if (args.length >= 4) {
		    InstanceReader instanceReader;
		    instanceReader = new InstanceReader (instanceName);
		    Instance instance=instanceReader.getInstance();
		    Model model=new Model(instance,nbRotations, tsmax,hmax, ratioMu,speedStep,tNbv, symmetryBreakingPolicy,withRefinedDomains, allowedIfConstraint);
		    RotationSubmodel rotationSubmodel = new RotationSubmodel(model);
		    CargoFlowSubmodel cargoFlowSubmodel= new CargoFlowSubmodel(model);
		    CargoFlowRotationSubmodel cargoFlowRotationSubmodel= new CargoFlowRotationSubmodel(model);
		    FeatureSubmodel featureSubmodel= new FeatureSubmodel(model);
		    LoadSubmodel loadSubmodel= new LoadSubmodel(model);
		    TimeSubmodel timeSubmodel= new TimeSubmodel(model);
		    TransitTimeSubmodel transitTimeSubmodel= new TransitTimeSubmodel(model);
		    VesselAvailabilitySubmodel vesselAvailabilitySubmodel=new VesselAvailabilitySubmodel(model);
		    CostSubmodel costSubmodel= new CostSubmodel(model);
	        ObjectiveFunction objectiveFunction=new ObjectiveFunction(model);
		    SymmetryBreakingSubmodel symmetryBreakingSubmodel= new SymmetryBreakingSubmodel(model);

		    model.addSubmodel(rotationSubmodel);
		    model.addSubmodel(cargoFlowSubmodel);
		    model.addSubmodel(cargoFlowRotationSubmodel);
		    model.addSubmodel(featureSubmodel);
		    model.addSubmodel(loadSubmodel);
		    model.addSubmodel(timeSubmodel);
		    model.addSubmodel(transitTimeSubmodel);
		    model.addSubmodel(vesselAvailabilitySubmodel);
		    model.addSubmodel(costSubmodel);
			model.addSubmodel(objectiveFunction);
		    model.addSubmodel(symmetryBreakingSubmodel);

		    model.addVariables();
		    model.addConstraints();
		    
		    //limited runtime
		    model.getChocoModel().getSolver().limitTime(t*1000);
	        long time = System.currentTimeMillis();

			  while (model.getChocoModel().getSolver().solve()) {
					 model.getChocoModel().getSolver().printStatistics();

		            System.out.println("Solution found:");
		            System.out.println("Opltimal Solution :"+model.getChocoModel().getSolver().getObjectiveManager().getObjective());
					  if(saveResult) {
							String path="src/main/java/test/solution.sol";
							AuxiliaryFunctions.save(model, path);
							}

			  /*}else {
		            System.out.println("No solution found.");*/

		        }
			  
		        // print solver runtime
		        int runtime = (int)((System.currentTimeMillis()-time)/1000);
		        if(runtime < t) {
		            System.out.println("Optimality proved in " + runtime + "s");
		        }else{
		            System.out.println(t+"s timeout reached");
		        }
		 }else {
			 System.out.println("Bad command line");
			 System.out.println(usage);
		 }
}
	
}