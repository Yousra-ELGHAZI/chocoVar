/**
 * This class allows representing a vessel type
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

import java.util.HashMap;
import java.util.Set;

public class VesselType {
  private String label;           // the label of the vessel type
  private int maximalCapacity;    // the maximal capacity of vessels
  private int realCapacity;       // the real capacity of vessels
  private int number;             // the number of available vessels
  private int rate;               // the time charter rate
  private int minimalSpeed;       // the minimal speed
  private int maximalSpeed;       // the maximal speed
  private HashMap<String, FuelTypeInformation> fuelInformation;  // the information about fuel
  
  
  /**
   * creates a vessel type
   * @param label the label of the vessel type
   * @param maximalCapacity the maximal capacity of vessels
   * @param realCapacity the real capacity of vessels
   * @param number the number of available vessels
   * @param rate the time charter rate
   * @param minimalSpeed the minimal speed
   * @param maximalSpeed the maximal speed
   * @exception Exception the label is not correct
   * @exception Exception the maximal capacity is not correct
   * @exception Exception the real capacity is not correct
   * @exception Exception the number is not correct
   * @exception Exception the rate is not correct
   * @exception Exception the minimal speed is not correct
   * @exception Exception the maximal speed is not correct
   */
  public VesselType (String label, int maximalCapacity, int realCapacity, int number, int rate, int minimalSpeed, int maximalSpeed) throws Exception {
    if (label.length() > 0) {
      this.label = label;
    }
    else {
      throw new Exception ("The label ("+label+") is not correct");
    }
    
    if (maximalCapacity > 0) {
      this.maximalCapacity = maximalCapacity;
    }
    else {
      throw new Exception ("The maximal capacity ("+Integer.toString(maximalCapacity)+") of the vessel type "+label+" is not correct");
    }
    
    if (realCapacity > 0) {
      this.realCapacity = realCapacity;
    }
    else {
      throw new Exception ("The real capacity ("+Integer.toString(realCapacity)+") of the vessel type "+label+" is not correct");
    }
    
    if (number > 0) {
      this.number = number;
    }
    else {
      throw new Exception ("The number ("+Integer.toString(number)+") of the vessel type "+label+" is not correct");
    }
    
    if (rate > 0) {
      this.rate = rate;
    }
    else {
      throw new Exception ("The rate ("+Integer.toString(rate)+") of the vessel type "+label+" is not correct");
    }
    
    if (minimalSpeed > 0) {
      this.minimalSpeed = minimalSpeed;
    }
    else {
      throw new Exception ("The minimal speed ("+Integer.toString(minimalSpeed)+") of the vessel type "+label+" is not correct");
    }
    
    if (maximalSpeed > 0) {
      this.maximalSpeed = maximalSpeed;
    }
    else {
      throw new Exception ("The maximal speed ("+Integer.toString(maximalSpeed)+") of the vessel type "+label+" is not correct");
    }
    
    this.fuelInformation = new HashMap<String, FuelTypeInformation>();
  }

  
  /**
   * returns the label of the vessel type as a string
   * @return the label of the vessel type
   */
  public String getLabel () {
    return this.label;
  }
  
  
  /**
   * @param fuelType the fuel type
   * @param designSpeed the design speed
   * @param designConsumption the consumption for the design speed
   * @exception the information already exists
   * @exception the design speed is not correct
   */
  public void addFuelInformation (String fuelType, int designSpeed, float designConsumption) throws Exception{
    if (this.fuelInformation.containsKey(fuelType)) {
      throw new Exception ("The information for fuel "+fuelType+") is already defined");
    }
    else {
      if (this.minimalSpeed <= designSpeed && designSpeed <= this.maximalSpeed) {
        this.fuelInformation.put(fuelType, new FuelTypeInformation(designSpeed, designConsumption));
      }
      else {
        throw new Exception ("The design speed ("+Integer.toString(designSpeed)+") is not correct");
      }
    } 
  }


  /**
   * returns the maximal capacity of the vessel type
   * @return the maximal capacity
   */
  public int getMaximalCapacity () {
    return this.maximalCapacity;
  }

  
  /**
   * returns the real capacity of the vessel type
   * @return the real capacity
   */
  public int getRealCapacity () {
    return this.realCapacity;
  }


  /**
   * returns the number of available vessels
   * @return the number of available vessels
   */
  public int getVesselNumber () {
    return this.number;
  }
  
  
  /**
   * returns the rate of vessels
   * @return the rate of vessels
   */
  public int getRate () {
    return this.rate;
  }
  
  
  /**
   * returns the minimal speed of vessels
   * @return the minimal speed of vessels
   */
  public int getMinimalSpeed () {
    return this.minimalSpeed;
  }


  /**
   * returns the maximal speed of vessels
   * @return the maximal speed of vessels
   */
  public int getMaximalSpeed () {
    return this.maximalSpeed;
  }


  /**
   * returns the design speed for the given fuel type
   * @param fuelType the fuel type
   * @return the design speed for the given fuel type
   */ 
  public int getDesignSpeed (String fuelType) {
    if (this.fuelInformation.containsKey(fuelType)) {
      return this.fuelInformation.get(fuelType).getDesignSpeed();
    }
    else {
      return 0;
    }
  }

  /**
   * returns the consumption for the design speed for the given fuel type
   * @param fuelType the fuel type
   * @return the consumption for the design speed for the given fuel type
   */ 
  public float getDesignConsumption (String fuelType) {
    if (this.fuelInformation.containsKey(fuelType)) {
      return this.fuelInformation.get(fuelType).getDesignConsumption();
    }
    else {
      return 0;
    }
  }

  
  /**
   * returns the set of possible fuel types
   * @return the set of possible fuel types
   */   
   public Set<String> getFuelType () {
    return this.fuelInformation.keySet();
  }
}
