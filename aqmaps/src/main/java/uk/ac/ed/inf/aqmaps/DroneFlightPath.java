package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Date;

public class DroneFlightPath {
    
    private ArrayList<Sensor> todaysSensors; 
    private ArrayList<Move> movesMade; 
    private Date flightDate; 
    private double initialLong; 
    private double initialLat; 
    
    public final double r = 0.0003; 
    
    public DroneFlightPath(Date flDate, ArrayList<Sensor> tSensors,double iLong, double iLat) {
        flightDate = flDate; 
        todaysSensors = tSensors; 
        initialLong = iLong; 
        initialLat = iLat;
        movesMade = new ArrayList<Move>(); 
    }
    
    public ArrayList<Move> getMovesMade(){
        return movesMade; 
    }
    
    public void calculateFlightPath() {
        
        int dronesVisited = todaysSensors.size(); 
        ArrayList<Sensor> sensorsRemaining = todaysSensors; 
        int noMovesMade = 0; 
        
        double currLong = initialLong; 
        double currLat = initialLat; 
        
        while(noMovesMade <= 150 || dronesVisited > 0) {
            Sensor nextSensor = getNearestSensor(initialLong,initialLat,sensorsRemaining);
            int sensorIndex = sensorsRemaining.indexOf(nextSensor);
            boolean inRange = false; 
            
            double newLong; 
            double newLat; 
            
            //checks that number of moves made is not more than number of sensors remaining 
            while(noMovesMade <= (150-sensorsRemaining.size()) && inRange == false) {
                
                //calculates new direction and position of drone 
                Direction ang = calculateNewDroneAngle(nextSensor.sensorWord.coordinates.lng, nextSensor.sensorWord.coordinates.lat,currLong,currLat);
                double[] newPosition = calculateNewDronePosition(ang,nextSensor,currLong,currLat); 
                newLong = newPosition[0]; 
                newLat = newPosition[1]; 
                
                //checks if in range sensor, if so make a move, take reading 
                if(inRangeSensor(newLong,newLat,nextSensor) == true) {
                    
                    inRange = true; 
                    Move nMove = new Move(currLong, currLat, newLong,newLat,ang,nextSensor.getThreeWordsLoc()); 
                    movesMade.add(nMove);
                    sensorsRemaining.remove(sensorIndex); 
                    dronesVisited --; 
                    
                } else { 
                    //checks to see if nextSensor still nearest sensor and then makes a move
                    Sensor newNearestSensor = getNearestSensor(newLong,newLat,sensorsRemaining);
                    if(newNearestSensor.equals(nextSensor)) {
                        nextSensor = newNearestSensor; 
                    }
                    
                    //makes move, null is that we haven't taken a reading from any sensor 
                    Move nMove = new Move(currLong, currLat, newLong,newLat,ang,null);
                    movesMade.add(nMove); 

                }
                
                noMovesMade ++; 
                currLong = newLong; 
                currLat = newLat; 
               
            }
        }
        
        
    }
    
    private Sensor getNearestSensor(double cLong, double cLat,ArrayList<Sensor> sRemaining) {
        //cLong,cLat are current longitude and latitudes 
        int MinIndex = 0; 
        double currentMin = 100.0; 
        for(int i =0; i < sRemaining.size(); i ++) {
            double nLong = sRemaining.get(i).sensorWord.coordinates.lng; 
            double nLat = sRemaining.get(i).sensorWord.coordinates.lat; 
            double eDist = calculateEuclidDist(cLong,cLat,nLong,nLat); 
            if(eDist < currentMin) {
                currentMin = eDist; 
                MinIndex = i; 
            }
        }
        return sRemaining.get(MinIndex); 
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
    
    
    public double[] calculateNewDronePosition(Direction dDirect,Sensor sClose, double cX, double cY) {
        
        //x coord or longitude of closest sensor
        double sX = sClose.sensorWord.coordinates.lng;
        //y coord or latitude of closest sensor 
        double sY = sClose.sensorWord.coordinates.lat; 
        
        double angle = dDirect.directionDegree; 
        
        double newLong;  
        double newLat; 
        
        
   
        //since angle is measured from east going clockwise need to adjust to get angle depending on which quadrant 
        //the straight line between the drone's current position and closest sensor points to 
        //this includes whether it is a positive or negative change in long/lat
        if(sX > cX) {
            //top right quadrant
      
            if(sY > cY) {
                //do nothing 
                newLong = r*Math.sin(angle) + cX;
                newLat = r*Math.cos(angle) + cY; 

            }
            //bottom right quadrant
            else {
                angle = 180 - angle;   
                newLong = r*Math.sin(angle) + cX;
                newLat = cY - r*Math.cos(angle); 
            }
            
        } else {
            //top left quadrant
            if(sY > cY) {
                angle = 360-angle; 
                newLong = cX - r*Math.sin(angle);
                newLat = r*Math.cos(angle) + cY; 
            }
            //bottom left quadrant
            else {
                angle = 180 + angle;   
                newLong = cX - r*Math.sin(angle);
                newLat = cY - r*Math.cos(angle); 
            } 
        }
        
        //calculate changes in drone x,y coords 
        double [] results = {newLong,newLat}; 
        return results; 
        
        
    }
    
    public boolean inRangeSensor(double newLong, double newLat, Sensor cSensor) {
        
        double sensorLong = cSensor.sensorWord.coordinates.lng; 
        double sensorLat = cSensor.sensorWord.coordinates.lat; 
        
        double range = calculateEuclidDist(newLong,newLat,sensorLong,sensorLat); 
        
        if(range <= 0.0002) {
            return true; 
        } else {
            return false; 
        }
        
        
        
    }
    
    

}
