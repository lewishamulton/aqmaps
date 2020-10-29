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
        return  Math.sqrt((xLong - yLong) * (xLong - yLong) + (xLat - yLat) * (xLat - yLat));
    }
    
    private ArrayList<Move> noOfMovesNeeded(double cLong,double cLat, Sensor nextSensor){
        return null;      
    }
    
    

}
