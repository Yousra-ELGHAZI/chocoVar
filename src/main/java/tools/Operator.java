/**
 * This enum allows representing classical relational operator
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */


package tools;


public enum Operator {
  EQ, NE, LT, LE, GT, GE;

  /**
   * computes and returns the negation of the operator
   * @param op the operator
   * @return the negation of value of the variable
   */
  public Operator negation () {
    Operator op = this;
    switch (op) {
      case EQ:
        op = NE;
        break;
      case NE:
        op = EQ;
        break;
      case LT:
        op = GE;
        break;
      case LE:
        op = GT;
        break;
      case GT:
        op = LE;
        break;
      case GE:
        op = LT;
        break;
    }
    return op;
  }
}
