package uk.ac.ed.inf.aqmaps;

import java.math.BigDecimal;
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
        
        //new longitude and latitudes of drone that will be continuosly calculated
        //within the for loop
        double newLong = 0; 
        double newLat = 0;
        

        for(int i =0; i < 150; i ++) {
            //check nearestSensor is still nearest and that there are sensors Remaining 
            if(sensorsRemaining.size() <1) {
                break; 
            }
            Sensor nearestSensor = getNearestSensor(currLong,currLat,sensorsRemaining); 

           
            double sLong = nearestSensor.sensorWord.coordinates.lng; 
            double sLat = nearestSensor.sensorWord.coordinates.lat; 
            Direction d = calculateNewDroneAngle(sLong,sLat,currLong,currLat); 
            double[] newCoords = calculateNewDronePosition(d,nearestSensor,currLong,currLat); 
            newLong = newCoords[0]; 
            newLat = newCoords[1]; 
            
            if(inRangeSensor(newLong,newLat,nearestSensor) == true) {
                Move nextMove = new Move(currLong,currLat,newLong,newLat,d,nearestSensor.getThreeWordsLoc()); 
                movesMade.add(nextMove); 
                sensorsRemaining.remove(nearestSensor); 

                
            } else {
                Move nextMove = new Move(currLong,currLat,newLong,newLat,d,null);
                System.out.println("Sensor long:" + nearestSensor.sensorWord.coordinates.lng + " Lat:" + nearestSensor.sensorWord.coordinates.lat); 
                System.out.println("Drone long:"+ currLong + " Lat:"+currLat); 
                movesMade.add(nextMove); 
            }
            currLong = newLong; 
            currLat= newLat; 
            
            
        }
        
        
        
    }
    
    public Sensor getNearestSensor(double cLong, double cLat,ArrayList<Sensor> sRemaining) {
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
        double ratio = calculateEuclidDist(droneX,droneY,sensX,droneY)/calculateEuclidDist(droneX,droneY,sensX,sensY); 
        double droneSensorAngle  = Math.acos(ratio); 
        droneSensorAngle = Math.toDegrees(droneSensorAngle); 
        
        if(sensY > droneY) {
            if(sensX > droneX) {
                //drone/sensor line is in top right quadrant 
                //do nothing 
            } else {
                //drone/sensor line is in top left quadrant
                droneSensorAngle = 180.0 - droneSensorAngle;  
            }
        } else {
            if(sensX > droneX) {
                //drone/sensor line is in bottom right quadrant
                droneSensorAngle = 360.0 - droneSensorAngle;  
            } else {
                //drone/sensor line is in bottom left quadrant 
                droneSensorAngle = 180.0 + droneSensorAngle;  
            }
        }
        
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
        if(sY > cY) {
            //top left quadrant
      
            if(sX < cX) {
                //do nothing
                angle = 180.0 - angle;   
                angle = Math.toRadians(angle); 
                newLong = cX -r*Math.cos(angle);
                newLat = r*Math.sin(angle) + cY; 

            }
            //top right quadrant
            else {
                angle = Math.toRadians(angle); 
                newLong = r*Math.cos(angle) + cX;
                newLat = r*Math.sin(angle) + cY; 
            }
            
        } else {
            //bottom left quadrant
            if(sX < cX) {
                angle = angle-180.0; 
                angle = Math.toRadians(angle); 
                newLong = cX- r*Math.cos(angle) ;
                newLat = cY - r*Math.sin(angle); 
            }
            //bottom right quadrant
            else {
                angle = 360.0 - angle;  
                angle = Math.toRadians(angle); 
                newLong = cX + r*Math.cos(angle);
                newLat = cY - r*Math.sin(angle); 
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
        String str = String.format("%.25f",range); 
        System.out.println("Range: " + str + "\n"); 
        if(range <= 0.0002) {
            return true; 
        } else {
            return false; 
        }
        
        
        
    }
    
    

}
