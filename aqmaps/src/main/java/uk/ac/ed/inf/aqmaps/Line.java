package uk.ac.ed.inf.aqmaps;

public class Line {
    
    
    //array of tuples making up coordinates of ends of line 
    //array index 0 for inner array always x coord 
    private double[][] coords;
    //gradient
    private double M; 
    //y-intercept 
    private double c; 
    
    
    public Line(double x1, double y1, double x2, double y2) {
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
    
    public Boolean intersectWithOtherLine(Line otherL) {
        var x1 = coords[0][0]; 
        var y1 = coords[0][1]; 
        var x2 = coords[1][0]; 
        var y2 = coords[1][1];
               
        var coords2 = otherL.getCoords(); 
        var x3 = coords2[0][0]; 
        var y3 = coords2[0][1]; 
        var x4 = coords2[1][0]; 
        var y4 = coords2[1][1]; 
        var M2 = otherL.getGradient(); 
        var c2 = otherL.getYIntercept(); 
       
        if(Math.max(x1, x2) < Math.min(x3,x4)) {
            //there are no mutual abcisses between lines 
            //hence no intersection 
            return false; 
        }
        if(M == M2) {
            //no intersection if lines parallel 
            return false; 
        }
        
        //calculates common coord x between 2 lines
        var Xa = (c2 - c)/(M - M2); 
        
        //checks common coord in bounds of lines 
        var lessThanBound = Math.max(Math.min(x1, x2), Math.min(x3, x4)); 
        var moreThanBound = Math.min(Math.max(x1,x2),Math.max(x3, x4)); 
        if(Xa < lessThanBound || Xa > moreThanBound) {
            return false; 
        } else {
            return true; 
        }
        
    }
    
    
    
    
    
    }


