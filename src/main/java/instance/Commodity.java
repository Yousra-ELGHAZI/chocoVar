/**
 * This class allows representing a commodity
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

public class Commodity {
  private LocationCode pol; // the origin port
  private LocationCode pod; // the destination port
  private int number;       // the number of TEUs
  private int revenue;      // the revenue per TEU
  private String nature;    // the nature (full, empty, ...)
  private int transitTime;  // the transit time
  
  
  /**
   * creates a commodity
   * 
   * @param pol the origin port
   * @param pod the destination port
   * @param number the number of TEUs
   * @param revenue the revenue per TEU
   * @exception nature the nature
   * @exception transitTime the transit time
   * @exception Exception the pol is not correct
   * @exception Exception the pod is not correct
   * @exception Exception the nb is not correct
   * @exception Exception the revenue is not correct
   * @exception Exception the nature is not correct
   * @exception Exception the transitTime is not correct
   */
  public Commodity(String pol, String pod, int number, int revenue, String nature, int transitTime) throws Exception {
    this.pol = new LocationCode(pol);
    this.pod = new LocationCode(pod);
      
    if (number > 0) {
      this.number = number;
    }
    else {
      throw new Exception("The number of TEU ("+Integer.toString(number)+") of the commodity is not correct");
    }

    if (revenue >= 0) {
      this.revenue = revenue;
    }
    else {
      throw new Exception("The revenue ("+Integer.toString(revenue)+") of the commodity is not correct");
    }
      
    if (nature.length() > 0) {
      this.nature = nature;
    }
    else {
      throw new Exception("The nature ("+nature+") of the commodity is not correct");
    }
      
    if (transitTime >= 0) {
      this.transitTime = transitTime;
    }
    else {
      throw new Exception("The revenue ("+Integer.toString(transitTime)+") of the commodity is not correct");
    }
  }


  /**
   * returns the origin port as a string
   * 
   * @return the origin port
   */
  public String getPol () {
    return this.pol.toString();
  }


  /**
   * returns the destination port as a string
   * 
   * @return the destination port
   */
  public String getPod () {
    return this.pod.toString();
  }


  /**
   * returns the number of teu of the commodity
   * 
   * @return the number of teu
   */
  public int getNumber () {
    return this.number;
  }


  /**
   * returns the nature
   * 
   * @return the nature
   */
  public String getNature () {
    return this.nature;
  }
  

  /**
   * returns the revenue per TEU
   * 
   * @return the revenue per TEU
   */
  public int getRevenue () {
    return this.revenue;
  }


  /**
   * returns the transit time
   * 
   * @return the transit time
   */
  public int getTransitTime () {
    return this.transitTime;
  }  
}
