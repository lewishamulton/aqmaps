package uk.ac.ed.inf.aqmaps;
import java.util.Date;
import java.util.HashMap;

public class Sensor {
    private double battery; 
    private double reading; 
    private String location; 
    private double LongLatSW; 
    private double LongLatNE; 
    private Date dateSensorVisited; 
    
    public Sensor(Date dateGiven) {
        
        dateSensorVisited = dateGiven; 
        
        
    }
    
    public double[] ThreeWordsToLongLat() {
        return null; 
    }
    public double[] getReadings() {
        return null; 
    }
    
    
    public double getSensorReading() {
        return reading; 
    }
    public double getBatteryReading() {
        return battery; 
    }
    public String getThreeWordsLoc() {
        return location; 
    }


}
