/**
 * This class allows representing canals
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

public class Canal extends Infrastructure {
  private int duration;       // the duration of the canal traversal (in hours)

  
  /**
   * creates a new infrastructure
   * @param code the string defining the location code
   * @param maxSize the maximal size (in TEU) of a vessel that can use the infrastructure
   * @param maxDraft the maximal draft allowed for a vessel that uses the infrastructure
   * @param duration the duration of the canal traversal
   * @exception Exception the code is not correct
   * @exception Exception the maximal size is not correct
   * @exception Exception the maximal draft is not correct
   * @exception Exception the duration is not correct
   */
  public Canal(String code, int maxSize, float maxDraft, int duration) throws Exception {
    super(code, maxSize, maxDraft);
    
    if (duration > 0) {
      this.duration = duration;
    }
    else {
      throw new Exception("The traversal duration ("+Integer.toString(duration)+") is not correct");
    }
  }
  
  
  /**
   * adds the information for the given vessel type
   * @param vesselType the vessel type
   * @param cost the canal cost
   * @param waitingTime the waiting time
   * @exception Exception The information is already defined
   */
  public void addVesselTypeInformation (String vesselType, int cost, int waitingTime) throws Exception {
    if (this.getVesselTypeInformation().containsKey(vesselType)) {
      throw new Exception("The information for vessel type "+vesselType+" is already defined");
    }
    else {
      this.addVesselTypeInformation(vesselType, new CanalVesselTypeInformation(cost,waitingTime));
    }
  }
  
  
  /**
   * returns the canal cost depending on the given vessel type
   * @param vesselType the vessel type
   * @return the canal cost depending on the given vessel type
   */
  public int getCanalCost (String vesselType) {
    if (this.getVesselTypeInformation().containsKey(vesselType)) {
      return this.getVesselTypeInformation().get(vesselType).getCost();
    }
    else {
      return 0;
    }
  }


  /**
   * returns the duration of the canal traversal
   * @return the duration of the canal traversal
   */ 
  public int getDuration () {
    return this.duration;
  }
}
