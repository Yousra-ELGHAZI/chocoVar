/**
 * This class allows representing the information related to a vessel type for a given canal
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

public class CanalVesselTypeInformation extends VesselTypeInformation {
  private int cost;    // the traversal cost for the current vessel type
  
  /**
   * creates canal information for a vessel type
   * @param cost the traversal cost for the current vessel type
   * @param waitingTime the waiting time for the vessel type
   * @exception Exception the traversal cost is not correct
   * @exception Exception the waiting time is not correct
   */
  public CanalVesselTypeInformation (int cost, int waitingTime) throws Exception {
    super(cost, waitingTime);
  }  
}
