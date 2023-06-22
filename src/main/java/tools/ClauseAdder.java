/**
 * This class allows adding clause to a given model
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package tools;

import model_v0.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;
import org.chocosolver.util.iterators.DisposableValueIterator;


public class ClauseAdder {
  private Model model;     // the model to which clauses will be added
  
  /**
   * creates a clause adder for the given model
   * 
   * @param model the model to which clauses will be added
   */
  public ClauseAdder(Model model){
    this.model = model;
  }


  /**
   * adds the unary clause (var=op) to the model
   * @param var the name of the variable
   * @param val the value of the variable
   */
  public void addClause (BoolVar var, int val) {    
    this.addClause (var,val,var,val);
  }
  
  
  /**
   * adds the binary clause (var1=val1 or var2=val2) to the model
   * @param var1 the name of the first variable
   * @param val1 the value of the first variable
   * @param var1 the name of the second variable
   * @param val1 the value of the second variable
   */
  public void addClause (BoolVar var1, int val1, BoolVar var2, int val2) {
    IntVar [] varClause = new BoolVar [2];
    IntIterableRangeSet [] valClause = new IntIterableRangeSet[2];
    
    varClause[0] = var1;
    varClause[1] = var2;
    
    valClause[0] = new IntIterableRangeSet(val1);
    valClause[1] = new IntIterableRangeSet(val2);

    this.model.getChocoModel().getClauseConstraint().addClause(varClause, valClause);
  }

  
  /**
   * adds the binary clause (var1 op1 val1 or var2 op2 val2) to the model
   * @param var1 the name of the first variable
   * @param op1 the operator related to the first variable and its value
   * @param val1 the value of the first variable
   * @param var2 the name of the second variable
   * @param op2 the operator related to the second variable and its value
   * @param val2 the value of the second variable
   */
  public void addClause (IntVar var1, Operator op1, int val1, IntVar var2, Operator op2, int val2) {
    IntVar [] varClause = new IntVar [2];
    IntIterableRangeSet [] valClause = new IntIterableRangeSet[2];
    
    varClause[0] = var1;
    varClause[1] = var2;
    
    valClause[0] = this.getRange(var1, op1, val1);
    valClause[1] = this.getRange(var2, op2, val2);

    //System.out.println("Clause "+var1.toString()+" "+op1.toString()+" "+Integer.toString(val1)+ " v "+var2.toString()+" "+op2.toString()+" "+Integer.toString(val2) +" -> "+valClause[0].toString()+ " v " +valClause[1].toString());

    this.model.getChocoModel().getClauseConstraint().addClause(varClause, valClause);
  }

  /**
   * adds the clause (var1 op1 val1 or var2 op2 val2 or var3 op3 val3 or var4 op4 val4) to the model
   * @param var1 the name of the first variable
   * @param op1 the operator related to the first variable and its value
   * @param val1 the value of the first variable
   * @param var2 the name of the second variable
   * @param op2 the operator related to the second variable and its value
   * @param val2 the value of the second variable
   * @param var3 the name of the third variable
   * @param op3 the operator related to the third variable and its value
   * @param val3 the value of the third variable
   * @param var4 the name of the fourth variable
   * @param op4 the operator related to the fourth variable and its value
   * @param val4 the value of the fourth variable
   */
  public void addClause (IntVar var1, Operator op1, int val1, IntVar var2, Operator op2, int val2, IntVar var3, Operator op3, int val3, IntVar var4, Operator op4, int val4) {
    IntVar [] varClause = new IntVar [4];
    IntIterableRangeSet [] valClause = new IntIterableRangeSet[4];
    
    varClause[0] = var1;
    varClause[1] = var2;
    varClause[2] = var3;
    varClause[3] = var4;
    
    valClause[0] = this.getRange(var1, op1, val1);
    valClause[1] = this.getRange(var2, op2, val2);
    valClause[2] = this.getRange(var3, op3, val3);
    valClause[3] = this.getRange(var4, op4, val4);

    this.model.getChocoModel().getClauseConstraint().addClause(varClause, valClause);
  }


  /**
   * adds the clause (var1 op1 val1 or var2 op2 val2 or var3 op3 val3 or var4 op4 val4 or var5 op5 val5) to the model
   * @param var1 the name of the first variable
   * @param op1 the operator related to the first variable and its value
   * @param val1 the value of the first variable
   * @param var2 the name of the second variable
   * @param op2 the operator related to the second variable and its value
   * @param val2 the value of the second variable
   * @param var3 the name of the third variable
   * @param op3 the operator related to the third variable and its value
   * @param val3 the value of the third variable
   * @param var4 the name of the fourth variable
   * @param op4 the operator related to the fourth variable and its value
   * @param val4 the value of the fourth variable
   * @param var5 the name of the fifth variable
   * @param op5 the operator related to the fifth variable and its value
   * @param val5 the value of the fifth variable
   */
  public void addClause (IntVar var1, Operator op1, int val1, IntVar var2, Operator op2, int val2, IntVar var3, Operator op3, int val3, IntVar var4, Operator op4, int val4,
                         IntVar var5, Operator op5, int val5) {
    IntVar [] varClause = new IntVar [5];
    IntIterableRangeSet [] valClause = new IntIterableRangeSet[5];
    
    varClause[0] = var1;
    varClause[1] = var2;
    varClause[2] = var3;
    varClause[3] = var4;
    varClause[4] = var5;
    
    valClause[0] = this.getRange(var1, op1, val1);
    valClause[1] = this.getRange(var2, op2, val2);
    valClause[2] = this.getRange(var3, op3, val3);
    valClause[3] = this.getRange(var4, op4, val4);
    valClause[4] = this.getRange(var5, op5, val5);

    this.model.getChocoModel().getClauseConstraint().addClause(varClause, valClause);
  }


  /**
   * adds a binary implication (var1 op1 val1 -> var2 op2 val2) to the model
   * @param var1 the name of the first variable
   * @param op1 the operator related to the first variable and its value
   * @param val1 the value of the first variable
   * @param var2 the name of the second variable
   * @param op2 the operator related to the second variable and its value
   * @param val2 the value of the second variable
   */
  public void addImplication (IntVar var1, Operator op1, int val1, IntVar var2, Operator op2, int val2) {
    this.addClause (var1, op1.negation(), val1, var2, op2, val2);
  }

  /**
   * adds an implication (var1 op1 val1 & var2 op2 val2 & var3 op3 val3 -> var4 op4 val4) to the model
   * @param var1 the name of the first variable
   * @param op1 the operator related to the first variable and its value
   * @param val1 the value of the first variable
   * @param var2 the name of the second variable
   * @param op2 the operator related to the second variable and its value
   * @param val2 the value of the second variable
   * @param var3 the name of the third variable
   * @param op3 the operator related to the third variable and its value
   * @param val3 the value of the third variable
   * @param var4 the name of the fourth variable
   * @param op4 the operator related to the fourth variable and its value
   * @param val4 the value of the fourth variable
   */
  public void addImplication (IntVar var1, Operator op1, int val1, IntVar var2, Operator op2, int val2, IntVar var3, Operator op3, int val3, IntVar var4, Operator op4, int val4) {
    this.addClause (var1, op1.negation(), val1, var2, op2.negation(), val2, var3, op3.negation(), val3, var4, op4, val4);
  }


  /**
   * adds an implication (var1 op1 val1 & var2 op2 val2 & var3 op3 val3 & var4 op4 val4 -> var5 op5 val5) to the model
   * @param var1 the name of the first variable
   * @param op1 the operator related to the first variable and its value
   * @param val1 the value of the first variable
   * @param var2 the name of the second variable
   * @param op2 the operator related to the second variable and its value
   * @param val2 the value of the second variable
   * @param var3 the name of the third variable
   * @param op3 the operator related to the third variable and its value
   * @param val3 the value of the third variable
   * @param var4 the name of the fourth variable
   * @param op4 the operator related to the fourth variable and its value
   * @param val4 the value of the fourth variable
   * @param var5 the name of the fifth variable
   * @param op5 the operator related to the fifth variable and its value
   * @param val5 the value of the fifth variable
   */
  public void addImplication (IntVar var1, Operator op1, int val1, IntVar var2, Operator op2, int val2, IntVar var3, Operator op3, int val3, IntVar var4, Operator op4, int val4,
                              IntVar var5, Operator op5, int val5) {
    this.addClause (var1, op1.negation(), val1, var2, op2.negation(), val2, var3, op3.negation(), val3, var4, op4.negation(), val4, var5, op5, val5);
  }

  /**
   * adds a binary implication (var1 op1 val1 -> var2 op2 val2) to the model
   * @param var1 the name of the first variable
   * @param op1 the operator related to the first variable and its value
   * @param val1 the value of the first variable
   * @param var2 the name of the second variable
   * @param op2 the operator related to the second variable and its value
   * @param val2 the value of the second variable
   */
  public void addEquivalence (IntVar var1, Operator op1, int val1, IntVar var2, Operator op2, int val2) {
    this.addImplication (var1, op1, val1, var2, op2, val2);
    this.addImplication (var2, op2, val2, var1, op1, val1);
  }


  /**
   * returns the iterable range corresponding to the part of the domain of variable var satisfying the condition op w.r.t. value val
   * @param var the name of the variable
   * @param op the operator related to the variable and its value
   * @param val the value of the variable
   * @return the iterable range corresponding to the part of the domain of variable var satisfying the condition op w.r.t. value val
   */
  private IntIterableRangeSet getRange (IntVar var, Operator op, int val){
    IntIterableRangeSet range;
    
    if (op == Operator.EQ) {
      range = new IntIterableRangeSet(val);
    } 
    else {
      range = new IntIterableRangeSet(var);
      if (op == Operator.NE) {
        range.remove(val);
      }
      else {
        DisposableValueIterator iter;
        int begin, end;
        
        if (op == Operator.LT || op == Operator.LE) {
          iter = var.getValueIterator(false);
          end = iter.next();  
          
          if (op == Operator.LE) {
            begin = val+1;
          }
          else
            {
              begin = val;
            }
         // System.out.println(Integer.toString(val)+" : "+Integer.toString(begin)+"..."+Integer.toString(end));
        }
        else {
          iter = var.getValueIterator(true);
          begin = iter.next();  
          
          if (op == Operator.GE) {
            end = val-1;
          }
          else
            {
              end = val;
            }
        //  System.out.println(Integer.toString(val)+" : "+Integer.toString(begin)+"..."+Integer.toString(end));
        }
        
        if (begin <= end) {
          range.removeBetween(begin, end);
        }
        iter.dispose();
      }
    }
    
    return range;
  }
}

