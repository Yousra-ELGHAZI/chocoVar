package test;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.ParallelPortfolio;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

public class Port {

    private final static int TIME_LIMIT = 30;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        int n = 10;
        int m = 5;
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("Solving GridColouring "+n+"_"+m);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("Single thread using model search (min domain / min value)");
        solve(n,m,0);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("Single thread using Activity Based Search");
        solve(n,m,1);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("Portfolio using both model search and Activity Based Search");
        solve(n,m,2);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%");
	}
	
    private static void solve(int n, int m, int conf){
        // solving
        long time = System.currentTimeMillis();
        if (conf<2) { // single thread solving (usual method)
            Model ca = buildModel(n,m,conf==0);
            while (ca.getSolver().solve()){
                System.out.println("Solution found (objective = "+ca.getSolver().getBestSolutionValue()+")");
            }
        }else{ // portfolio optimization (uses bound sharing)
            ParallelPortfolio portfolio = new ParallelPortfolio();
            portfolio.addModel(buildModel(n,m,true));
            portfolio.addModel(buildModel(n,m,false));
            while (portfolio.solve()){
                System.out.println("Solution found (objective = "+portfolio.getBestModel().getSolver().getBestSolutionValue()+")");
            }
        }
        // print solver runtime
        int runtime = (int)((System.currentTimeMillis()-time)/1000);
        if(runtime < TIME_LIMIT) {
            System.out.println("Optimality proved in " + runtime + "s");
        }else{
            System.out.println(TIME_LIMIT+"s timeout reached");
        }
    }
    
    private static Model buildModel(int n, int m, boolean mznSearch) {
        Model model = new Model("GridColouring "+n+"_"+m);
        // decision variables x[i][j] = k means cell (i,j) takes color k
        IntVar[][] x = model.intVarMatrix("x", n, m, 1, Math.min(n,m));
        // flat representation of x
        IntVar[] vars = ArrayUtils.flatten(x);
        // objective variable
        IntVar objective = model.intVar(1,Math.min(n,m));
        // minimize the objective variable
        model.setObjective(Model.MINIMIZE, objective);

        // objective function : number of colors that are used (colors are symmetrical)
        model.max(objective, vars).post();

        // grid constraints
        for(int i=0;i<n;i++){
            for(int j=i+1;j<n;j++){
                for(int k=0;k<m;k++){
                    for(int l=k+1;l<m;l++){
                        model.or(// at least one of these constraints must be satisfied
                                model.arithm(x[i][k],"!=",x[i][l]),
                                model.arithm(x[i][l],"!=",x[j][l]),
                                model.arithm(x[j][k],"!=",x[j][l]),
                                model.arithm(x[i][k],"!=",x[j][k])
                        ).post();
                    }
                }
            }
        }

        // tuning search strategy
        Solver s = model.getSolver();
        s.limitTime(TIME_LIMIT+"s");
        if(mznSearch) {
            // use search strategy given in the minizinc model (first fail)
            s.setSearch(Search.minDomLBSearch(vars));
        }else{
            // use activity-based search (classical black box search)
            s.setSearch(Search.activityBasedSearch(vars));
        }
        return model;
    }

}
