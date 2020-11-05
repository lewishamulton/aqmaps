package uk.ac.ed.inf.aqmaps;

public class Move {
    
    private double prevLong; 
    private double prevLat; 
    private double currLong; 
    private double currLat; 
    
    private Direction currentDirection; 
    
    private String sensorLocation; 
    
    public double getPrevLong() {
        return prevLong; 
    }
    
    public String getSensorLocation() {
        return sensorLocation; 
    }
    public Move(double pLong,double pLat,double cLong,double cLat,Direction cDirection,String sLocation) {
        prevLong = pLong; 
        prevLat = pLat; 
        currLong = cLong; 
        currLat = cLat; 
        currentDirection = cDirection; 
        sensorLocation = sLocation; 
         
    }
    

}
