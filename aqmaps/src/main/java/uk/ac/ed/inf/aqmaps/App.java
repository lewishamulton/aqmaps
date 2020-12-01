package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Date;

public class App 
{

    public static void main(String[] args) {
        //assign command line args to vars  
        Date todaysDate = new Date(Integer.parseInt(args[2]), Integer.parseInt(args[1]), Integer.parseInt(args[0])); 
        double initialLat = Double.parseDouble(args[3]); 
        double initialLong = Double.parseDouble(args[4]); 
        System.out.println(initialLat); 
        System.out.println(initialLong);
        int randomSeed = Integer.parseInt(args[5]); 
        int portNo = Integer.parseInt(args[6]); 
        
        //get sensors for today
        SensorsToday tSensors = new SensorsToday(todaysDate,portNo); 
        ArrayList<Sensor> sensors = tSensors.getTodaysSensors(); 
        
        for(int i=0; i < sensors.size(); i ++) {
            sensors.get(i).ThreeWordsToLongLat(portNo);
        }
       
        
        DroneFlightPath dPath = new DroneFlightPath(todaysDate,sensors,initialLong,initialLat,portNo); 
        double testLat = dPath.getNearestSensor(initialLong, initialLat, sensors).sensorWord.coordinates.lat; 
        double testLong = dPath.getNearestSensor(initialLong, initialLat, sensors).sensorWord.coordinates.lng; 
        dPath.calculateFlightPath();
        System.out.println(dPath.getMovesMade().size());
        for(int i =0; i <dPath.getMovesMade().size(); i++) {
               System.out.println(dPath.getMovesMade().get(i).getSensorLocation()); 
               System.out.println(dPath.getMovesMade().get(i).getCurrLongLat()[0]); 
               System.out.println(dPath.getMovesMade().get(i).getCurrLongLat()[1]); 
               System.out.println(dPath.getMovesMade().get(i).getDirection().directionDegree); 
               System.out.println("*************"); 
        }
        
        var fOutput = new FlightPathOutput(dPath); 
        fOutput.generateTxtFlightPath();
        fOutput.generateGeoJsonFile();
        
       
       

        
    }
}
