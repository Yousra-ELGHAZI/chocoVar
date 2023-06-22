/**
 * This class allows representing the information related to a vessel type for a given infrastructure
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

public abstract class VesselTypeInformation {
  private int cost;           // the cost of using the infrastructure for the current vessel type
  private int waitingTime;    // the waiting time (in hours) for the current vessel type
  
  /**
   * creates information for a vessel type
   * 
   * @param cost the cost of using the infrastructure for the current vessel type
   * @param waitingTime the waiting time for the vessel type
   * @exception Exception the cost is not correct
   * @exception Exception the waiting time is not correct
   */
  public VesselTypeInformation (int cost, int waitingTime) throws Exception {
    if (cost >= 0) {
      this.cost = cost;
    }
    else {
      throw new Exception("The cost "+Integer.toString(cost)+" is not correct");
    }
    
    if (waitingTime >= 0) {
      this.waitingTime = waitingTime;
    }
    else {
      throw new Exception("The waiting time "+Integer.toString(waitingTime)+" is not correct");
    }
  }
  
  
  /**
   * returns the cost of using the infrastructure for the current vessel type
   * @return the cost of using the infrastructure
   */ 
  public int getCost() {
    return this.cost;
  }
  
  
  /**
   * returns the waiting time for the vessel type
   * @return the waiting time
   */ 
  public int getWaitingTime() {
    return this.waitingTime;
  }
}
