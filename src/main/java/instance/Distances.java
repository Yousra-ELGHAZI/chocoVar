/**
 * This class allows representing distances between ports and/or canals
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;

import java.util.HashMap;

public class Distances {
  private HashMap<String, HashMap<String, Integer>> distances;   // the distances between pairs of infrastructures
  private int distanceNumber;     // the number of distances
  private int minimalDistance;    // the smallest distance
  private int maximalDistance;    // the largest distance
  
  /**
   * creates an empty set of distances
   * 
   */
  public Distances() {
    this.distances = new HashMap<String, HashMap<String, Integer>>();
    this.distanceNumber = 0;
    this.minimalDistance = 40000;
    this.maximalDistance = -1;
  }
  
  
  /**
   * adds a new distance between two infrastructures
   * 
   * @param origin the origin
   * @param destination the destination
   * @param distance the distance between the origin and the destination
   * @exception the distance is not correct
   */
  public void addDistance (String origin, String destination, int distance) throws Exception {
    if (origin == destination) {
      throw new Exception("When defining a distance the origin ("+origin+") and destination ("+destination+") are the same");
    }
    if (distance > 0) {
      if (! this.distances.containsKey(origin)) {
        // we create an empty map of distances for origin
        this.distances.put(origin, new HashMap<String, Integer>());
      }

      if (this.distances.get(origin).containsKey(destination)) {
        throw new Exception ("The distance ("+Integer.toString(distance)+") from "+origin+" to "+destination+" is already defined");
      }
      else {
        this.distances.get(origin).put(destination, distance);
        this.distanceNumber++;
        if (distance < this.minimalDistance) {
          this.minimalDistance = distance;
        }
        
        if (distance > this.maximalDistance) {
          this.maximalDistance = distance;
        }
      }
    }
    else {
      throw new Exception ("The distance ("+Integer.toString(distance)+") from "+origin+" to "+destination+" is not correct");
    }
  }


  /**
   * returns the distance between two infrastructures
   * 
   * @param origin the origin
   * @param destination the destination
   * @return the distance between the origin and the destination
   */
  public int getDistance (String origin, String destination) {
    if (origin == destination) {
      return 0;
    }
    else {
      if ((! this.distances.containsKey(origin)) || (! this.distances.get(origin).containsKey(destination))) {
        return 0;
      }
      else {
        return this.distances.get(origin).get(destination);
      }
    }
  }


  /**
   * returns the number of distances
   * 
   * @return the number of distances
   */
  public int getNumber () {
    return this.distanceNumber;
  }


  /**
   * returns the minimal distance
   * 
   * @return the minimal distance
   */
  public int getMinimalDistance () {
    return this.minimalDistance;
  }


  /**
   * returns the maximal distance
   * 
   * @return the maximal distance
   */
  public int getMaximalDistance () {
    return this.maximalDistance;
  }
}
