/**
 * This class allows representing an infrastructure
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

import java.util.HashMap;

public abstract class Infrastructure{
  private LocationCode code;      // the location code of the infrastructure
  private int maxSize;            // the maximal size (in TEU) of a vessel that can use the infrastructure
  private float maxDraft;         // the maximal draft allowed for a vessel that uses the infrastructure
  private HashMap<String,VesselTypeInformation> vesselTypeInformation;   // the information for each allowed vessel type
  
  /**
   * creates a new infrastructure
   * @param code the string defining the location code
   * @param maxSize the maximal size (in TEU) of a vessel that can use the infrastructure
   * @param maxDraft the maximal draft allowed for a vessel that uses the infrastructure
   * @exception Exception the code is not correct
   * @exception Exception the maximal size is not correct
   * @exception Exception the maximal draft is not correct
   */
  public Infrastructure(String code, int maxSize, float maxDraft) throws Exception {
    // we check the validity of parameter
    this.code = new LocationCode(code);
    
    if (maxSize >= 0) {
      this.maxSize = maxSize;
    }
    else {
      throw new Exception("The maximal size "+Integer.toString(maxSize)+" is not correct");
    }
    
    if (maxDraft >= 0){
      this.maxDraft = maxDraft;
    }
    else {
      throw new Exception("The maximal draft "+Float.toString(maxDraft)+" is not correct");
    }
      
    vesselTypeInformation = new HashMap<String,VesselTypeInformation>();
  }

  
  /**
   * returns the location code of the infrastructure
   * @return the location code of the infrastructure as a string
   */  
  public String getCode(){
    return this.code.toString();
  }


  /**
   * returns the maximal size of vessels that can use the instrastructure
   * @return the maximal size
   */  
  public int getMaximalSize(){
    return this.maxSize;
  }
  

  /**
   * returns the maximal draft of vessels that can use the instrastructure
   * @return the maximal draft
   */  
  public float getMaximalDraft (){
    return this.maxDraft;
  }


  /**
   * adds the given information for the given vessel type
   * @param vesselType the vessel type
   * @param information the vessel type information to add
   */  
  public void addVesselTypeInformation(String vesselType, VesselTypeInformation information){
    this.vesselTypeInformation.put(vesselType, information);
  }


  /**
   * returns the information for each allowed vessel type
   * @return the information for each allowed vessel type
   */  
  public HashMap<String,VesselTypeInformation> getVesselTypeInformation(){
    return this.vesselTypeInformation;
  }


  /**
   * returns the waiting time for the given vessel type
   * @param vesselType the type of vessel
   * @return the waiting time for the given vessel type
   */ 
  public int getWaitingTime (String vesselType){
    if (this.vesselTypeInformation.containsKey(vesselType)) {
      return this.vesselTypeInformation.get(vesselType).getWaitingTime();
    }
    else {
      return 0;
    }
  }
}
