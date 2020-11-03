package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Date;

public class DroneFlightPath {
    
    private ArrayList<Sensor> todaysSensors; 
    private ArrayList<Move> movesMade; 
    private Date flightDate; 
    private double initialLong; 
    private double initialLat; 
    
    public DroneFlightPath(Date flDate, ArrayList<Sensor> tSensors,double iLong, double iLat) {
        flightDate = flDate; 
        todaysSensors = tSensors; 
        initialLong = iLong; 
        initialLat = iLat;
    }
    
    public void calculateFlightPath() {
        
        Sensor initialSensor = getNearestSensor(initialLong,initialLat); 
        
        
    }
    
    private Sensor getNearestSensor(double cLong, double cLat) {
        //cLong,cLat are current longitude and latitudes 
        int MinIndex = 0; 
        double currentMin = 100.0; 
        for(int i =0; i < todaysSensors.size(); i ++) {
            double nLong = todaysSensors.get(i).sensorWord.coordinates.lng; 
            double nLat = todaysSensors.get(i).sensorWord.coordinates.lat; 
            double eDist = calculateEuclidDist(cLong,cLat,nLong,nLat); 
            if(eDist < currentMin) {
                currentMin = eDist; 
                MinIndex = i; 
            }
        }
        return todaysSensors.get(MinIndex); 
    }
    
    public double calculateEuclidDist(double xLong, double xLat, double yLong, double yLat) {
        //long = x direction
        //lat = y direction 
        return  Math.sqrt((xLong - yLong) * (xLong - yLong) + (xLat - yLat) * (xLat - yLat));
    }
    
    private ArrayList<Move> noOfMovesNeeded(double cLong,double cLat, Sensor nextSensor){
        return null;      
    }
    
    
    
    //this method calculates the angle of some multiple of 10 closest to the angle needed for the
    //drone on the same line as the sensor
    public Direction  calculateNewDroneAngle(double sensX, double sensY, double droneX, double droneY) {
        //the angle calculated here assumes north at 0degrees, and calculates the angle going clockwise from that 
        //this is converted to east at 0degrees later in the method 
        double ratio = calculateEuclidDist(droneX,droneY,droneX,sensY)/calculateEuclidDist(droneX,droneY,sensX,sensY); 
        double droneSensorAngle  = Math.acos(ratio); 
        droneSensorAngle = Math.toDegrees(droneSensorAngle); 
        
        if(sensX > droneX) {
            if(sensY > droneY) {
                //drone/sensor line is in top right quadrant 
                //do nothing 
            } else {
                //drone/sensor line is in bottom right quadrant
                droneSensorAngle = 180.0 - droneSensorAngle;  
            }
        } else {
            if(sensY > droneY) {
                //drone/sensor line is in top left quadrant
                droneSensorAngle = 360.0 - droneSensorAngle;  
            } else {
                //drone/sensor line is in bottom left quadrant 
                droneSensorAngle = 180.0 + droneSensorAngle;  
            }
        }
        
        //convert based on north is at 90degrees 
        droneSensorAngle = droneSensorAngle + 90.0; 
        if (droneSensorAngle >= 360.0) {
            droneSensorAngle = droneSensorAngle - 360.0; 
        }
        
        //find nearest multiple of 10 degrees 
        
        //rounds dronesensorAngle to nearest int 
        int roundedDSAngle = (int)Math.round(droneSensorAngle); 
        int remainder = roundedDSAngle  % 10; 
        int newDroneAngle; 
        
        
        if (remainder > 5) {
           newDroneAngle = roundedDSAngle + (10-remainder); 
        } else {
           newDroneAngle = roundedDSAngle - remainder; 
        }
        
        return new Direction(newDroneAngle); 
        
    }
    
    

}
