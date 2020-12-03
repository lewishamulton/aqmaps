package uk.ac.ed.inf.aqmaps;

public class StraightLine {
    
    
    //array of tuples making up coordinates of ends of line 
    //array index 0 for inner array always x coord 
    private double[][] coords = new double[2][2];
    //gradient
    private double M; 
    //y-intercept 
    private double c; 
    
    
    public StraightLine(double x1, double y1, double x2, double y2) {
        coords[0][0] = x1; 
        coords[0][1] = y1; 
        coords[1][0] = x2; 
        coords[1][1] = y2; 
        
        
        M = (y2-y1)/(x2-x1); 
        
        c = y1 - M*x1; 
        }
        
    public double[][] getCoords(){
        return coords; 
    }
    public double getGradient() {
        return M; 
    }
    public double getYIntercept() {
        return c; 
    }
    
    //given an x coord along the straight line, finds its y-coord 
    public double getYCoord(double newX) {
        return (c + M*newX); 
    }

    
    
     
    
    }


