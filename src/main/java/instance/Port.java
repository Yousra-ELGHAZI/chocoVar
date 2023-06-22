/**
 * This class allows representing ports
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

public class Port extends Infrastructure {
  private int tsCost;  // the transshipment cost

  
  /**
   * creates a new infrastructure
   * @param code the string defining the location code
   * @param maxSize the maximal size (in TEU) of a vessel that can use the infrastructure
   * @param maxDraft the maximal draft allowed for a vessel that uses the infrastructure
   * @param tsCost the transshipment cost
   * @exception Exception the code is not correct
   * @exception Exception the maximal size is not correct
   * @exception Exception the maximal draft is not correct
   * @exception Exception the tsCost is not correct
   */
  public Port(String code, int maxSize, float maxDraft, int tsCost) throws Exception {
    super(code, maxSize, maxDraft);
    
    if (tsCost >= 0) {
      this.tsCost = tsCost;
    }
    else {
      throw new Exception("The transshipment cost ("+Integer.toString(tsCost)+") is not correct");
    }
  }
  
  
  /**
   * adds the information for the given vessel type
   * @param vesselType the vessel type
   * @param productivity the productivity of the port for the current vessel type
   * @param manoeuvringInTime the manoeuvring time for entering the port for the current vessel type
   * @param manoeuvringOutTime the manoeuvring time for leaving the port for the current vessel type
   * @param cost the port call cost
   * @param waitingTime the waiting time
   * @exception Exception The information is already defined
   */
  public void addVesselTypeInformation (String vesselType, int productivity, int manoeuvringInTime, int manoeuvringOutTime, int cost, int waitingTime) throws Exception {
    if (this.getVesselTypeInformation().containsKey(vesselType)) {
      throw new Exception("The information for vessel type "+vesselType+" is already defined");
    }
    else {
      this.addVesselTypeInformation(vesselType, new PortVesselTypeInformation(productivity, manoeuvringInTime, manoeuvringOutTime, cost, waitingTime));
    }
  }
  
  
  /**
   * returns the port call cost depending on the given vessel type
   * @param vesselType the vessel type
   * @return the port cost depending on the given vessel type
   */
  public int getCallCost (String vesselType) {
    if (this.getVesselTypeInformation().containsKey(vesselType)) {
      return this.getVesselTypeInformation().get(vesselType).getCost();
    }
    else {
      return 0;
    }
  }


  /**
   * returns the transshipment cost of the port
   * @return the transshipment cost 
   */ 
  public int getTransshipmentCost () {
    return this.tsCost;
  }

  
  /**
   * returns the productivity of the port for the given type of vessels
   * @param vesselType the vessel type
   * @return the productivity
   */ 
  public int getProductivity (String vesselType)
  {
    if (this.getVesselTypeInformation().containsKey(vesselType)) {
      return ((PortVesselTypeInformation) this.getVesselTypeInformation().get(vesselType)).getProductivity();
    }
    else {
      return 0;
    }
  }

  
  /**
   * returns the manoeuvring time for entering the port for the given type of vessels
   * @param vesselType the vessel type
   * @return the manoeuvring time for entering the port
   */ 
  public int getManoeuvringTimeIn (String vesselType)
  {
    if (this.getVesselTypeInformation().containsKey(vesselType)) {
      return ((PortVesselTypeInformation) this.getVesselTypeInformation().get(vesselType)).getManoeuvringTimeIn();
    }
    else {
      return 0;
    }
  }


  /**
   * returns the manoeuvring time for leaving the port for the given type of vessels
   * @param vesselType the vessel type
   * @return the manoeuvring time for leaving the port
   */ 
  public int getManoeuvringTimeOut (String vesselType)
  {
    if (this.getVesselTypeInformation().containsKey(vesselType)) {
      return ((PortVesselTypeInformation) this.getVesselTypeInformation().get(vesselType)).getManoeuvringTimeOut();
    }
    else {
      return 0;
    }
  }
}
