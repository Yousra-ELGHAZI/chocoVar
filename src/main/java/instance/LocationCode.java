/**
 * This class allows representing a location code
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

public class LocationCode {
  private String code;   // the code of the infrastructure
 
  /**
   * creates a location code from a string
   * @param code the string defining the location code
   * @exception Exception the code is not correct
   */
  public LocationCode(String code) throws Exception {
    // we check the validity of the code
    int i = 0;
    while (i < code.length() && (Character.isUpperCase(code.charAt(i)) || (i > 1 && Character.isDigit(code.charAt(i))))){
      i++;
    }
    if (i == code.length() && code.length() == 5) {
      this.code = code;
    }
    else {
      throw new Exception("The code ("+code+") is not correct");
    }
  }
  
  
  /**
   * converts location code to a string
   * @return the location code as a string 
   */
   public String toString(){
     return this.code;
   }
}
