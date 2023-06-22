/**
 * This class allows representing an instance of the Liner Ship Network Design Problem
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */
 
package instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class Instance {
  private ArrayList<String> portCodes;              // the port codes
  private HashMap<String, Port> ports;              // the ports
  private ArrayList<String> canalCodes;             // the canal codes
  private HashMap<String, Canal> canals;            // the canals
  private ArrayList<String> fuelTypeLabels;         // the fuel type labels
  private HashMap<String, FuelType> fuelTypes;      // the fuel types
  private ArrayList<String> vesselTypeLabels;       // the vessel type labels
  private HashMap<String, VesselType> vesselTypes;  // the vessel types
  private ArrayList<Commodity> commodities;         // the commodities
  private int numberCommodityWithTt;                // the number of commodities with a transit time constraint
  private Distances distances;                      // the distances between pairs of infrastructures
  
  
  /**
   * creates an empty instance
   */
  public Instance () {
    this.portCodes = new ArrayList<String>();
    this.ports = new HashMap<String, Port>();
    this.canalCodes = new ArrayList<String>();
    this.canals = new HashMap<String, Canal>();
    this.fuelTypeLabels = new ArrayList<String>();
    this.fuelTypes = new HashMap<String, FuelType>();
    this.vesselTypeLabels = new ArrayList<String>();
    this.vesselTypes = new HashMap<String,VesselType>();
    this.commodities = new ArrayList<Commodity>();
    this.numberCommodityWithTt = 0;
    this.distances = new Distances();
  }
  
  
  /**
   * returns the list of ports (as strings)
   * @return the list of ports
   */
  public ArrayList<String> getPorts () {
    return this.portCodes;
  }
  
  
  /**
   * returns the list of canals (as strings)
   * @return the list of canals
   */
  public ArrayList<String> getCanals () {
    return this.canalCodes;
  }

  
  /**
   * returns the list of vessel types (as strings)
   * @return the list of vessel types
   */
  public ArrayList<String> getVesselTypes () {
    return this.vesselTypeLabels;
  }

  
  /**
   * returns the list of fuel types (as strings)
   * @return the list of fuel types
   */
  public ArrayList<String> getFuelTypes () {
    return this.fuelTypeLabels;
  }
  
  
  /**
   * returns the list of commodities
   * @return the list of commodities
   */
  public ArrayList<Commodity> getCommodities () {
    return this.commodities;
  }


  /**
   * returns the set of fuel types (as strings)
   * @return the set of fuel types
   */
  public Distances getDistances () {
    return this.distances;
  }
    

  /**
   * adds a new port to the instance
   * @param port the port to add
   * @exception the port is already defined
   */
  public void addPort (Port port) throws Exception {
    if (this.ports.containsKey(port.getCode()) || this.canals.containsKey(port.getCode())) {
      throw new Exception("The port "+port.getCode()+" is already defined in the instance");
    }
    else {
      this.portCodes.add(port.getCode());
      this.ports.put(port.getCode(),port);
    }
  }
    

  /**
   * adds a new canal to the instance
   * @param canal the canal to add
   * @exception the canal is already defined
   */
  public void addCanal (Canal canal) throws Exception {
    if (this.ports.containsKey(canal.getCode()) || this.canals.containsKey(canal.getCode())) {
      throw new Exception("The canal "+canal.getCode()+" is already defined in the instance");
    }
    else {
      this.canalCodes.add(canal.getCode());
      this.canals.put(canal.getCode(),canal);
    }
  }
    

  /**
   * adds a new vessel type to the instance
   * @param vesselType the vessel type to add
   * @exception the vessel type is already defined
   */
  public void addVesselType (VesselType vesselType) throws Exception {
    if (this.vesselTypes.containsKey(vesselType.getLabel())) {
      throw new Exception("The vessel type "+vesselType.getLabel()+" is already defined in the instance");
    }
    else {
      this.vesselTypeLabels.add(vesselType.getLabel());
      this.vesselTypes.put(vesselType.getLabel(),vesselType);
    }
  }
    

  /**
   * adds a new fuel type to the instance
   * @param fuelType the fuel type to add
   * @exception the fuel type is already defined
   */
  public void addFuelType (FuelType fuelType) throws Exception {
    if (this.fuelTypes.containsKey(fuelType.getLabel())) {
      throw new Exception("The fuel type "+fuelType.getLabel()+" is already defined in the instance");
    }
    else {
      this.fuelTypeLabels.add(fuelType.getLabel());
      this.fuelTypes.put(fuelType.getLabel(),fuelType);
    }
  }


  /**
   * adds a new commodity to the instance
   * @param commodity the commodity to add
   * @exception the commodity is not correct
   */
  public void addCommodity (Commodity commodity) throws Exception {
    if (this.ports.containsKey(commodity.getPol()) && this.ports.containsKey(commodity.getPod()))
    {
      if (commodity.getTransitTime() > 0) {
        this.commodities.add(this.numberCommodityWithTt, commodity);
        this.numberCommodityWithTt++;
      }
      else {
        this.commodities.add(commodity);
      }
    }
    else {
      throw new Exception ("The commodity is not correct");
    }
  }


  /**
   * adds a new distance to the instance
   * @param origin the origin of the distance to add
   * @param destination the destination of the distance to add
   * @param distance the distance in miles 
   * @exception the distance is not correct
   */
  public void addDistance (String origin, String destination, int distance) throws Exception {
    if ((this.ports.containsKey(origin) || this.canals.containsKey(origin)) && (this.ports.containsKey(destination) || this.canals.containsKey(destination))) {
      this.distances.addDistance(origin, destination, distance);
    }
    else {
      throw new Exception ("The distance is not correct");
    }
  }


  /**
   * returns the port corresponding to the given code
   * @param code the location code of the port
   * @return the port corresponding to the given code
   */
  public Port getPort (String code) {
    if (this.ports.containsKey(code)) {
      return this.ports.get(code);
    }
    else {
      return null;
     }
  }
   

  /**
   * returns the canal corresponding to the given code
   * @param code the location code of the canal
   * @return the canal corresponding to the given code
   */
   public Canal getCanal (String code) {
     if (this.canals.containsKey(code)) {
       return this.canals.get(code);
     }
     else {
       return null;
      }
   }
   

  /**
   * returns the vessel type corresponding to the given label
   * @param label the label
   * @return the vessel type corresponding to the given label
   */
  public VesselType getVesselType (String label) {
    if (this.vesselTypes.containsKey(label)) {
      return this.vesselTypes.get(label);
    }
    else {
      return null;
    }
  }
   

  /**
   * returns the fuel type corresponding to the given label
   * @param label the label
   * @return the fuel type corresponding to the given label
   */
  public FuelType getFuelType (String label) {
  if (this.fuelTypes.containsKey(label)) {
    return this.fuelTypes.get(label);
  }
  else {
      return null;
    }
  }
   

  /**
   * returns the number of commodities having a transit time constraint
   * @return the number of commodities having a transit time constraint
   */
  public int getNumberCommodityWithTt () {
    return this.numberCommodityWithTt;
  }
  
  
  /**
   * returns true if the instance is fully defined, false otherwise
   * @return true if the instance is fully defined, false otherwise
   */
  public boolean is_fully_defined () {
    return this.ports.size() >= 2 && this.fuelTypes.size() > 0 && this.vesselTypes.size() > 0 && this.commodities.size() > 0 && this.distances.getNumber() >= 2;
  }
}
