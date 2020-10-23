package uk.ac.ed.inf.aqmaps;

import java.util.Date;

public class App 
{

    public static void main( String[] args )
    {
        //assign command line args to vars  
        Date todaysDate = new Date(Integer.parseInt(args[2]), Integer.parseInt(args[1]), Integer.parseInt(args[0])); 
        double initialLat = Double.parseDouble(args[3]); 
        double initialLong = Double.parseDouble(args[4]); 
        int randomSeed = Integer.parseInt(args[5]); 
        int portNo = Integer.parseInt(args[6]); 
        
        //get sensors for today
        SensorsToday tSensors = new SensorsToday(todaysDate); 
        
    }
}
