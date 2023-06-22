/**
 * This class allows test instance package
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package test;


import instance.InstanceReader;

public class TestInstance{
  public static void main(String args[]) throws Exception { 

    if (args.length == 1) {
      InstanceReader instanceReader;
      instanceReader = new InstanceReader (args[0]);
    }
  }

}
