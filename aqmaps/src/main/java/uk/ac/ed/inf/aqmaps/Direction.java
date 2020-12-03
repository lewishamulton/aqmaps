package uk.ac.ed.inf.aqmaps;

public class Direction {
    
    //constant fields to remind user where north/south is related to degrees
    final int northIsAt = 90; 
    final int southIsAt = 270;
    int directionDegree; 
    boolean noMoreMoves = false; 
    
    public Direction(int dir) {
        
        if((dir % 10) == 0 && dir <=350 && dir >=0) {
            this.directionDegree = dir; 
        } else {
            throw new IllegalArgumentException("Direction " + dir  +" is not valid. Directions must be between 0/350 in multiples of 10"); 
        }
    }
    

    
    public void setDirection(int dir) {
        //direction must be between 0 & 350 in multiples of 10
        if((dir % 10) == 0 && dir <=350 && dir >=0) {
            this.directionDegree = dir; 
        } else {
            throw new IllegalArgumentException("Direction " + dir  +" is not valid. Directions must be between 0/350 in multiples of 10"); 
        }
    }

}
