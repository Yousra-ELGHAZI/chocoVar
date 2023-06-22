/**
 * This class allows representing an instance of the Liner Ship Network Design Problem
 * 
 * @version 1.0
 *
 * @author TNTM 6 Team
 */

package instance;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

public class InstanceReader {
  private Instance instance;        // the read instance
  private JsonObject mainObject;    // the root of the JSON document
  
  
  /**
   * creates an instance from the data of the file whose name is filename
   * @param filename the name of the file containing the instance
   * @exception IOException the file does not exist
   * @exception Exception a port, a canal, a fuel type, a vessel type, a commodity or a distance is not correct
   */
  public InstanceReader (String filename) throws IOException, Exception {
    // we check the validity of the file
    File f = new File(filename);
    if (! f.isFile()) {
      throw new IOException ("The file "+filename+" does not exist");
    }

    // we open the file
    InputStream is =  new FileInputStream(f);
    
    // we create a JsonReader
    JsonReader reader = Json.createReader(is);
    
    // we get the main object
    this.mainObject = reader.readObject();

    // we create the instance
    this.instance = new Instance ();
    
    // we read each type of data
    this.readFuelTypes();
    this.readVesselTypes();
    this.readPorts();
    this.readCanals();
    this.readCommodities();
    this.readDistances();
  }


  /**
   * defines the ports of the instance from the ports given in the file
   * @exception Exception a port or its information is not correct
   */
  private void readPorts () throws Exception {
    // we read the basic information about ports
    JsonArray ports = (JsonArray) this.mainObject.get("ports");
    for (JsonValue value : ports) {  
      // we get the information of a new port
      JsonObject port = (JsonObject) value;
      String code = ((JsonString) port.get("code")).getString();
      int maxSize = ((JsonNumber) port.get("maximumSize")).intValue();
      int draft = ((JsonNumber) port.get("draft")).intValue();
      int tsCost = ((JsonNumber) port.get("transshipmentCost")).intValue();

      // we create the port
      Port p = new Port (code, maxSize, draft, tsCost);

      // we add it to the instance
      this.instance.addPort(p);

      // we read the port information related to vessel types
      int i = 0;
      JsonArray callCost = (JsonArray) port.get("callCost");
      JsonArray manoeuvringTimeIn = (JsonArray) port.get("manoeuvringTimeIn");
      JsonArray manoeuvringTimeOut = (JsonArray) port.get("manoeuvringTimeOut");
      JsonArray productivity = (JsonArray) port.get("productivity");
      JsonArray waitingTime = (JsonArray) port.get("waitingTime");
      
      for (String vesselType: instance.getVesselTypes()) {
        // we get the port information related to the current vessel type
        int pcc = ((JsonNumber) callCost.get(i)).intValue();
        int prod = ((JsonNumber) productivity.get(i)).intValue();
        int manIn = ((JsonNumber) manoeuvringTimeIn.get(i)).intValue();
        int manOut = ((JsonNumber) manoeuvringTimeOut.get(i)).intValue();
        int waiting = ((JsonNumber) waitingTime.get(i)).intValue();
        
        // we create the port information vessel type
        p.addVesselTypeInformation (vesselType, prod, manIn, manOut, pcc, waiting);
        
        i++;
      }
    }
  }
  
  
  /**
   * defines the canals of the instance from the ports given in the file
   * @exception Exception a canal or its information is not correct
   */
  private void readCanals () throws Exception {
    // we read the basic information about canals
    JsonArray canals = (JsonArray) this.mainObject.get("canals");
    for (JsonValue value : canals) {  
      // we get the information of a new canal
      JsonObject canal = (JsonObject) value;
      String code = ((JsonString) canal.get("code")).getString();
      int maxSize = ((JsonNumber) canal.get("maximumSize")).intValue();
      int draft = ((JsonNumber) canal.get("draft")).intValue();
      int duration = ((JsonNumber) canal.get("duration")).intValue();

      // we create the canal
      Canal c = new Canal (code, maxSize, draft, duration);

      // we add it to the instance
      this.instance.addCanal(c);

      // we read the canal information related to vessel types
      int i = 0;
      JsonArray cost = (JsonArray) canal.get("cost");
      JsonArray waitingTime = (JsonArray) canal.get("waitingTime");
      
      for (String vesselType: instance.getVesselTypes()) {
        // we get the canal information related to the current vessel type
        int canalCost = ((JsonNumber) cost.get(i)).intValue();
        int waiting = ((JsonNumber) waitingTime.get(i)).intValue();
        
        // we create the canal information vessel type
        c.addVesselTypeInformation (vesselType, canalCost, waiting);
        
        i++;
      }
    }
  }
  

  /**
   * defines the vessel types of the instance from the vessel types given in the file
   * @exception Exception a vessel type is not correct
   */
  private void readVesselTypes () throws Exception {
    // we read the basic information about vessel types
    JsonArray vessels = (JsonArray) this.mainObject.get("vessels");
    for (JsonValue value : vessels) {
      // we get the information of a new vessel type
      JsonObject vesselType = (JsonObject) value;
      String label = ((JsonString) vesselType.get("label")).getString();
      int maxCapacity = ((JsonNumber) vesselType.get("capacity")).intValue();
      int realCapacity = ((JsonNumber) vesselType.get("capacity")).intValue();
      int nb = ((JsonNumber) vesselType.get("number")).intValue();
      int tcc = ((JsonNumber) vesselType.get("timeCharterRate")).intValue();
      int minSpeed = ((JsonNumber) vesselType.get("minSpeed")).intValue();
      int maxSpeed = ((JsonNumber) vesselType.get("maxSpeed")).intValue();

      // we create the vessel type
      VesselType vt = new VesselType (label, maxCapacity, realCapacity, nb, tcc, minSpeed, maxSpeed);

      // we add it to the instance
      this.instance.addVesselType(vt);

      // we read the vessel information per fuel type
      JsonValue fuelInformation = vesselType.get("fuelType");
      int speed;
      float consumption;
      
      switch (fuelInformation.getValueType()) {
        case STRING : 
          // useful only for the first (basic) json format
          String fuelType = ((JsonString) fuelInformation).getString();
          speed = ((JsonNumber) vesselType.get("designSpeed")).intValue();
          consumption = (float) ((JsonNumber) vesselType.get("designConsumption")).doubleValue();

          // we create the fuel information for the current vessel type
          vt.addFuelInformation (fuelType, speed, consumption);
          break;
        case ARRAY:
          int i = 0;
          JsonArray designSpeed = (JsonArray) vesselType.get("designSpeed");
          JsonArray designConsumption = (JsonArray) vesselType.get("designConsumption");
          for (String ft: instance.getFuelTypes()) {
            // we get the vessel information related to a fuel type
            speed = ((JsonNumber) designSpeed.get(i)).intValue();
            consumption = (float) ((JsonNumber) designConsumption.get(i)).doubleValue();
        
            // we create the fuel information for the current vessel type
            vt.addFuelInformation (ft, speed, consumption);
            
            i++;
          }
      }
    }
  }


  /**
   * defines the fuel types of the instance from the fuel types given in the file
   * @exception Exception a fuel type is not correct
   */
  private void readFuelTypes () throws Exception {
    JsonArray fuels = (JsonArray) this.mainObject.get("fuels");
    for (JsonValue value : fuels) {
      // we get the information of a new fuel type
      JsonObject fuelType = (JsonObject) value;
      String label = ((JsonString) fuelType.get("label")).getString();
      int cost = ((JsonNumber) fuelType.get("cost")).intValue();

      // we create the fuel type
      FuelType ft = new FuelType (label,cost);

      // we add it to the instance
      this.instance.addFuelType(ft);
    }
  }


  /**
   * defines the commodities of the instance from the commodities given in the file
   * @exception Exception a commodity is not correct
   */
  private void readCommodities () throws Exception {
    JsonArray commodities = (JsonArray) this.mainObject.get("cargos");
    for (JsonValue value : commodities) {
      // we get the information of a new commodity
      JsonObject commodity = (JsonObject) value;
      String pol = ((JsonString) commodity.get("pol")).getString();
      String pod = ((JsonString) commodity.get("pod")).getString();
      String nature = ((JsonString) commodity.get("nature")).getString();
      int teu = ((JsonNumber) commodity.get("teu")).intValue();
      int revenue = ((JsonNumber) commodity.get("revenue")).intValue();
      int tt = ((JsonNumber) commodity.get("transitTime")).intValue() * 24;

      // we create the commodity
      Commodity c = new Commodity(pol, pod, teu, revenue, nature, tt);

      // we add it to the instance
      this.instance.addCommodity (c);
    }
  }

  
  /**
   * defines the distances of the instance from the distances given in the file
   * @exception Exception a distance is not correct
   */
  private void readDistances () throws Exception {
    JsonArray distances = (JsonArray) this.mainObject.get("distances");
    for (JsonValue value : distances) {
      // we get the information of a new distance
      JsonObject distance = (JsonObject) value;
      String origin = ((JsonString) distance.get("from")).getString();
      String destination = ((JsonString) distance.get("to")).getString();
      int dist = ((JsonNumber) distance.get("miles")).intValue();
      
      // we create the distance
      this.instance.addDistance (origin, destination, dist);
    }
  }
  

  /**
   * returns the read instance
   * @return the read instance
   */
  public Instance getInstance () {
    return this.instance;
  }
}
