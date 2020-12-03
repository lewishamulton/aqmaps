package uk.ac.ed.inf.aqmaps;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class DroneFlightPath {
    
    private ArrayList<Sensor> todaysSensors; 
    private ArrayList<Move> movesMade; 
    private ArrayList<Sensor> sensorsVisited = new ArrayList<Sensor>(); 
    private MapDate flightDate; 
    private double initialLong; 
    private double initialLat; 
    private int portNo; 
    private NoFlyZone nFZ = new NoFlyZone(); 
    
    private final double[] longitudeBounds =  new double[] {-3.192473,-3.184319}; 
    private final double[] latitudeBounds = new double[] {55.942617,55.946233}; 
    
    public final double r = 0.0003; 
    
    public DroneFlightPath(MapDate flDate, ArrayList<Sensor> tSensors,double iLong, double iLat, int portN) {
        flightDate = flDate; 
        todaysSensors = tSensors; 
        initialLong = iLong; 
        initialLat = iLat;
        portNo = portN; 
        movesMade = new ArrayList<Move>(); 
    }
    
    public ArrayList<Move> getMovesMade(){
        return movesMade; 
    }
    public MapDate getDate() {
        return flightDate; 
    }
    public ArrayList<Sensor> getSensorsVisited(){
        return sensorsVisited; 
    }
    public int getPortNo() {
        return portNo; 
    }
    
    
    public void calculateFlightPath() {
        
        //gets the number of sensors TO visit 
        int noSensorsToVisit = todaysSensors.size(); 
        ArrayList<Sensor> sensorsRemaining = todaysSensors; 
        int noMovesMade = 0; 
        
        double currLong = initialLong; 
        double currLat = initialLat; 
        
        //new longitude and latitudes of drone that will be continously calculated
        //within the for loop
        double newLong = 0; 
        double newLat = 0;
        

        for(int i =0; i < 150; i ++) {
            //check nearestSensor is still nearest and that there are sensors Remaining 
            if(sensorsRemaining.size() <1) {
                break; 
            }
            Sensor nearestSensor = getNearestSensor(currLong,currLat,sensorsRemaining); 

           
            //gets sensor Longitude and Latitude
            var sLong = nearestSensor.sensorWord.coordinates.lng; 
            var sLat = nearestSensor.sensorWord.coordinates.lat; 
            
            //calculates angle drone will fly in 
            Direction d = calculateNewDroneAngle(sLong,sLat,currLong,currLat); 
            
 
            double[] newCoords = calculateNewDronePosition(d,nearestSensor,currLong,currLat); 
            
            double[][] lineCoords = getCoordsOfLine(currLong,currLat,newCoords[0],newCoords[1]); 
          
            
            //checks if drone move is legal and if not returns new direction of a legal move 
            d = isALegalDroneMove(d,newCoords[0],newCoords[1],currLong,currLat,lineCoords,nearestSensor); 
           
            newCoords = calculateNewDronePosition(d,nearestSensor,currLong,currLat);
            
            //once correct coords have been found they become new long/lat of drone 
            newLong = newCoords[0]; 
            newLat = newCoords[1]; 
            
            //if drone is in range of sensor make a move then take reading
            if(inRangeSensor(newLong,newLat,nearestSensor) == true) {
                var nextMove = new Move(currLong,currLat,newLong,newLat,d,nearestSensor.getThreeWordsLoc()); 
                movesMade.add(nextMove); 
                //removes sensor from list of which sensors remaining to visit  
                sensorsRemaining.remove(nearestSensor); 
                //add this sensor to list of sensors that have been visited
                sensorsVisited.add(nearestSensor); 
                
                noMovesMade ++; 

             //else make a move with no sensor    
            } else {
                Move nextMove = new Move(currLong,currLat,newLong,newLat,d,null);
                movesMade.add(nextMove); 
                noMovesMade ++; 
            }
            
            currLong = newLong; 
            currLat= newLat; 
           
        }
        
        //to return drone to original location, treats original location 
        //as one last sensor to fly to 
        if(sensorsRemaining.size() <1 && noMovesMade <150) {
            
            var returnLocation = new Sensor(initialLong,initialLat); 
                
            while(inRangeSensor(currLong,currLat,returnLocation) == false) {
                
                Direction d = calculateNewDroneAngle(initialLong,initialLat,currLong,currLat); 
                double [] newCoords = calculateNewDronePosition(d,returnLocation,currLong,currLat); 
                
                double[][] lineCoords = getCoordsOfLine(currLong,currLat,newCoords[0],newCoords[1]);  
      
                d = isALegalDroneMove(d,newCoords[0],newCoords[1],currLong,currLat,lineCoords,returnLocation); 

                var nextMove = new Move(currLong,currLat,newCoords[0],newCoords[1],d,null); 
                movesMade.add(nextMove); 
                
                currLong = newCoords[0]; 
                currLat = newCoords[1]; 
            }
        } 
        
        
        
    }
    
    
    public Sensor getNearestSensor(double cLong, double cLat,ArrayList<Sensor> sRemaining) {
        //cLong,cLat are current longitude and latitudes 
        var MinIndex = 0; 
        var currentMin = 100.0; 
        for(int i =0; i < sRemaining.size(); i ++) {
            var nLong = sRemaining.get(i).sensorWord.coordinates.lng; 
            var nLat = sRemaining.get(i).sensorWord.coordinates.lat; 
            var eDist = calculateEuclidDist(cLong,cLat,nLong,nLat); 
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
    
    /*gets the coordinates along the line a drone travels from its old position
      to its new one*/
    public double[][] getCoordsOfLine(double currLong, double currLat,double newLong, double newLat) {
        
        StraightLine lineOfTravelDrone = new StraightLine(currLong,currLat,newLong,newLat); 
        double[][] lineCoords = new double[40][2]; 

        if(newLong > currLong) {
            for(int i = 0; i < 40; i++) {
                lineCoords[i][0] =  currLong + i*((newLong - currLong)/40); 
                lineCoords[i][1] =  lineOfTravelDrone.getYCoord(lineCoords[i][0]);         
            }
            
        } else {
            for(int i = 0; i < 10; i++) {
                lineCoords[i][0] =  currLong - i*((newLong - currLong)/40); 
                lineCoords[i][1] =  lineOfTravelDrone.getYCoord(lineCoords[i][0]);
            }        
            
        }
        
        return lineCoords;
    }
    //this method calculates the angle of some multiple of 10 closest to the angle needed for the
    //drone on the same line as the sensor
    public Direction  calculateNewDroneAngle(double sensX, double sensY, double droneX, double droneY) {
      
        double ratio = calculateEuclidDist(droneX,droneY,sensX,droneY)/calculateEuclidDist(droneX,droneY,sensX,sensY); 
        var droneSensorAngle  = Math.acos(ratio); 
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
        System.out.println(droneSensorAngle); 
        //find nearest multiple of 10 degrees 
        //rounds dronesensorAngle to nearest int 
        var roundedDSAngle = (int)Math.round(droneSensorAngle); 
        var remainder = roundedDSAngle  % 10; 
        int newDroneAngle; 
        System.out.println(roundedDSAngle); 
        
        if (remainder > 5) {
           newDroneAngle = roundedDSAngle + (10-remainder); 
        } else {
           newDroneAngle = roundedDSAngle - remainder; 
        }
        //final check to ensure angle is between 0-350 with modulus 
        newDroneAngle = newDroneAngle %360; 
        return new Direction(newDroneAngle); 
        
    }
    
    
    public double[] calculateNewDronePosition(Direction dDirect,Sensor sClose, double cX, double cY) {
        
        //x coord or longitude of closest sensor
        var sX = sClose.sensorWord.coordinates.lng;
        //y coord or latitude of closest sensor 
        var sY = sClose.sensorWord.coordinates.lat; 
        
        double angle = dDirect.directionDegree; 

        double newLong;  
        double newLat; 
        
        /*since angle is measured from east going clockwise need to adjust to get angle depending on which quadrant 
        the straight line between the drone's current position and closest sensor points to 
        this includes whether it is a positive or negative change in long/lat */ 
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
        
        var sensorLong = cSensor.sensorWord.coordinates.lng; 
        var  sensorLat = cSensor.sensorWord.coordinates.lat; 
        
        double range = calculateEuclidDist(newLong,newLat,sensorLong,sensorLat); 
        if(range < 0.0002) {
            return true; 
        } else {
            return false; 
        }      
    }
    
    /*checks if next drone move is legal (doesn't go over any noFlyZones/boundaries) and finds 
     * closest drone move (in terms of direction) if move isn't legal 
     */
    private Direction isALegalDroneMove(Direction d, double newLong, double newLat, double currLong, double currLat, double[][] lineCoords, Sensor nearestSensor) {
        
      //checks if new position is in a NoFlyZone
        boolean inNFZ = nFZ.inNoFlyZone(newLong, newLat,lineCoords); 
        
        //sets up a counter to check each possible direction has been counted 
        var i = 0; 
        //if so changes direction until it is not flying in a NoFlyZone
       
        while(inNFZ == true) {
            //increments direction by 10 
            d.setDirection((d.directionDegree+10)%360);
            System.out.println(d.directionDegree); 
            i ++; 
            if(i > 36) {
                System.out.println(nearestSensor.getThreeWordsLoc()); 
                System.out.println(currLong); 
                System.out.println(currLat); 
                throw new IllegalArgumentException("No more moves"); 
            }
            var newCoords = calculateNewDronePosition(d,nearestSensor,currLong,currLat);
            var inBounds = newCoords[0] < longitudeBounds[1] && newCoords[0] > longitudeBounds[0] && newCoords[1] < latitudeBounds[1] && newCoords[1] > latitudeBounds[0];
            //if its in bounds check its not in a no fly zone otherwise try another direction 
            
            if(inBounds) {
                inNFZ = nFZ.inNoFlyZone(newCoords[0], newCoords[1],lineCoords); 
 
            }
            
        }
        
        return d; 
        
    }
    
    

}
