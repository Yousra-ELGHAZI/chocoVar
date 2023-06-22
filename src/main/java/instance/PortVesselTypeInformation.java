/**
 * This class allows representing the information related to a vessel type for a given port
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

public class PortVesselTypeInformation extends VesselTypeInformation {
  private int productivity;         // the productivity of the port for the current vessel type
  private int manoeuvringTimeIn;    // the manoeuvring time for entering the port for the current vessel type
  private int manoeuvringTimeOut;   // the manoeuvring time for leaving the port for the current vessel type
  
  /**
   * creates port information for a vessel type
   * @param productivity the productivity of the port for the current vessel type
   * @param manoeuvringTimeIn the manoeuvring time for entering the port for the current vessel type
   * @param manoeuvringTimeOut the manoeuvring time for leaving the port for the current vessel type
   * @param cost the cost of using the infrastructure for the current vessel type
   * @param waitingTime the waiting time for the vessel type
   * @exception Exception the cost is not correct
   * @exception Exception the waiting time is not correct
   */
  public PortVesselTypeInformation (int productivity, int manoeuvringTimeIn, int manoeuvringTimeOut, int cost, int waitingTime) throws Exception {
    super(cost,waitingTime);
    
    if (productivity >= 0) {
      this.productivity = productivity;
    }
    else {
      throw new Exception("The productivity "+Integer.toString(productivity)+" is not correct");
    }
    
    if (manoeuvringTimeIn >= 0) {
      this.manoeuvringTimeIn = manoeuvringTimeIn;
    }
    else {
      throw new Exception("The manoeuvring time "+Integer.toString(manoeuvringTimeIn)+" for entering the port is not correct");
    }
    
    if (manoeuvringTimeOut >= 0) {
      this.manoeuvringTimeOut = manoeuvringTimeOut;
    }
    else {
      throw new Exception("The manoeuvring time "+Integer.toString(manoeuvringTimeOut)+" for leaving the port is not correct");
    }
  }


  /**
   * returns the productivity for the vessel type
   * @return the productivity
   */ 
  public int getProductivity() {
    return this.productivity;
  }


  /**
   * returns the manoeuvring time for entering the port for the vessel type
   * @return the manoeuvring time for entering the port
   */ 
  public int getManoeuvringTimeIn() {
    return this.manoeuvringTimeIn;
  }
  

  /**
   * returns the manoeuvring time for leaving the port for the vessel type
   * @return the manoeuvring time for v the port
   */ 
  public int getManoeuvringTimeOut() {
    return this.manoeuvringTimeOut;
  }
}
