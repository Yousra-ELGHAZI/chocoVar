/**
 * This class allows representing the information related to a vessel type for a given port
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

public class FuelTypeInformation {
  private int designSpeed;          // the design speed for the considered fuel
  private float designConsumption;  // the consumption for the design speed for the considered fuel
  
  /**
   * creates the fuel information
   * @param designSpeed the design speed for the considered fuel
   * @param designConsumption the consumption for the design speed for the considered fuel
   * @exception Exception the design speed is not correct
   * @exception Exception the consumption  for the design speed is not correct
   */
  public FuelTypeInformation (int designSpeed, float designConsumption) throws Exception {
    if (designSpeed > 0) {
      this.designSpeed = designSpeed;
    }
    else {
      throw new Exception ("The design speed ("+Integer.toString(designSpeed)+") is not correct");
    }
    
    if (designConsumption > 0) {
      this.designConsumption = designConsumption;
    }
    else {
      throw new Exception ("The design consumption ("+Float.toString(designConsumption)+") is not correct");
    }
  }
  
  
  /**
   * returns the design speed
   * @return the design speed
   */ 
  public int getDesignSpeed () {
    return this.designSpeed;
  }


  /**
   * returns the consumption for the design speed
   * @return the consumption for the design speed
   */ 
  public float getDesignConsumption () {
    return this.designConsumption;
  }
}
