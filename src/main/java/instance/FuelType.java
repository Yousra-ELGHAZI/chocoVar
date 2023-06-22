/**
 * This class allows representing a type of fuel
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

public class FuelType {
  private String label; // the name of the fuel
  private int cost;     // the cost of the ton of fuel
  
  /**
   * creates a fuel type
   * 
   * @param label the label of the fuel type
   * @param cost the cost of the ton of fuel
   * @exception Exception the label is not correct
   * @exception Exception the cost is not correct
   */
  public FuelType(String label, int cost) throws Exception {
    if (label.length() > 0) {
      this.label = label;
    }
    else {
      throw new Exception ("The label of the fuel type ("+label+") is not correct");
    }
    
    if (cost >= 0) {
      this.cost = cost;
    }
    else {
      throw new Exception ("The cost ("+Integer.toString(cost)+") of the fuel "+label+" is not correct");
    }
  }


  /**
   * returns the label of the fuel type as a string
   * 
   * @return the label of the fuel type
   */
  public String getLabel () {
    return this.label;
  }
    

  /**
   * returns the cost of the fuel
   * 
   * @return the cost of the fuel
   */    
  public int getCost () {
    return this.cost;
  }
}
