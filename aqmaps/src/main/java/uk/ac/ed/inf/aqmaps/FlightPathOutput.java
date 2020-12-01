package uk.ac.ed.inf.aqmaps;

import java.awt.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class FlightPathOutput {
    
    private DroneFlightPath droneFPath; 
    
    public FlightPathOutput(DroneFlightPath dFp) {
        droneFPath = dFp; 
    }
    
    public void generateTxtFlightPath() {
        
        //creates new file
        var flightDate = droneFPath.getDate(); 
        var day  = flightDate.getDay(); 
        var month = flightDate.getMonth();
        var year = flightDate.getYear();  
        var filename = "flightpath-"+day+"-"+month+"-"+year+".txt"; 
        try {
            File outputTxt = new File(filename);
            if (outputTxt.createNewFile()) {
              System.out.println("File created: " + outputTxt.getName());
            } else {
              System.out.println("File already exists.");
            }
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
 
        
        //writes to new file 
        FileWriter outputWriter = null; 
        try {
            outputWriter = new FileWriter(filename);
        } catch (IOException e) {
            // throws error if no file created previously 
            e.printStackTrace();
        }
        
        
        //checks flightpath has been calculated 
        ArrayList<Move> flightPath = new ArrayList<Move>(); 
        try {
          flightPath = droneFPath.getMovesMade(); 
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        
        for(int i =0; i <flightPath.size(); i++) {
           var currentMove = flightPath.get(i); 
           var PrevLongLat = currentMove.getPrevLongLat(); 
           var CurrLongLat = currentMove.getCurrLongLat(); 
           
           try {
            if(currentMove.getSensorLocation() == null) {
               outputWriter.write(i+","+PrevLongLat[1]+","+PrevLongLat[0]+","+CurrLongLat[1]+","+CurrLongLat[0]);
            } else {
               outputWriter.write(i+","+PrevLongLat[1]+","+PrevLongLat[0]+","+CurrLongLat[1]+","+CurrLongLat[0]+","+currentMove.getSensorLocation());
            }
        } catch (IOException e) {
            //error writing to file 
            e.printStackTrace();
        } 
       }
       try {
        outputWriter.close();
    } catch (IOException e) {
        //error closing file 
        e.printStackTrace();
    }    
   }
    
   public void generateGeoJsonFile() {
       
       //list of features of markers and move 'lines' 
       var fList = new ArrayList<Feature>(); 
       
       //gets arrayList of all moves made 
       var flightPath = droneFPath.getMovesMade(); 
       //get arrayList of sensors the drone has visited
       var sensorsVisited = droneFPath.getSensorsVisited(); 
       
       for(int i= 0; i <sensorsVisited.size(); i++) {
           var s = sensorsVisited.get(i); 
           //creates marker for all the sensors for that day
           fList.add(createMarker(s)); 
       }
       
       for(int i= 0; i<flightPath.size(); i++) {
           //gets move 
           var m = flightPath.get(i); 
           //creates linestring feature based on move of drone from 
           //one point to another 
           fList.add(createLine(m)); 
           
       }
       
       //creates geoJsonString of markers and linestring 
       String geoJsonString = FeatureCollection.fromFeatures(fList).toJson(); 
       var flightDate = droneFPath.getDate(); 
       var day  = flightDate.getDay(); 
       var month = flightDate.getMonth();
       var year = flightDate.getYear();  
       var filename = "readings-"+day+"-"+month+"-"+year+".geojson"; 
       
       try {
           File outputJson = new File(filename);
           if (outputJson.createNewFile()) {
             System.out.println("File created: " + outputJson.getName());
           } else {
             System.out.println("File already exists.");
           }
         } catch (IOException e) {
           System.out.println("An error occurred.");
           e.printStackTrace();
         } 
       
       
           
       try {
           FileWriter writeGeoJson = new FileWriter(filename); 
           writeGeoJson.write(geoJsonString);
           writeGeoJson.close();
       } catch (IOException e) {
           // if writing fails
           System.out.println("An error occurred.");
           e.printStackTrace(); 
       }
            
   }
   
   public Feature createLine(Move m) {
       
       var currLngLat = m.getCurrLongLat(); 
       var prevLngLat = m.getPrevLongLat(); 
       
       var pointList = new ArrayList<Point>();
       var currP = Point.fromLngLat(currLngLat[1], currLngLat[0]); 
       var prevP = Point.fromLngLat(prevLngLat[1], prevLngLat[0]); 
       pointList.add(prevP); 
       pointList.add(currP); 
       
       var lineString = LineString.fromLngLats(pointList); 
       var f = Feature.fromGeometry((Geometry)lineString); 
       
       return f; 
           
   }
   
   public Feature createMarker(Sensor s) {
       
       //gets sensorLocation and generates it in terms of lnglat 
       //if it hasn't done so already in program 
       var latit = 0.0; 
       var longit = 0.0; 
       try {
           latit = s.sensorWord.coordinates.lat; 
           longit = s.sensorWord.coordinates.lng; 
       } catch (NullPointerException e) {
           //if sensorLocation hasn't been converted to lng/lat does so now
           var pNo = droneFPath.getPortNo(); 
           s.ThreeWordsToLongLat(pNo);
           latit = s.sensorWord.coordinates.lat; 
           longit = s.sensorWord.coordinates.lng; 
       }
       var p = Point.fromLngLat(longit, latit);  
       //creates feature and its properties from the point and sensor info 
       Feature f = Feature.fromGeometry(((Geometry)p));
       f.addNumberProperty("fill-opacity", 0.75);
       f.addStringProperty("location", s.getThreeWordsLoc());
       var hexColour = getHexColour(s.getBatteryReading(),s.getSensorReading()); 
       var markerType = getMarker(s.getBatteryReading(),s.getSensorReading()); 
       f.addStringProperty("rgb-string", hexColour);
       f.addStringProperty("marker-colour", hexColour);
       f.addStringProperty("marker-symbol", markerType);
       
       
       return f; 
       
   }
   
  
   public String getMarker(double batteryReading, double sReading) {
       //if battery less than 10% then marker type will indicate battery
       //needs changing 
       if(batteryReading < 10.0) {
           return "cross";
       } else {
           if(sReading < 0) {
               throw new IllegalArgumentException(); 
           }
           if(sReading < 128) {
               return "lighthouse"; 
           }
           if(sReading < 256) {
               return "danger"; 
           }
           else {
               //returns no marker if any other type of reading 
               return ""; 
           }
       }   
   }
   
   public String getHexColour(double batteryReading,double sReading) {  
       //checks at first that battery reading is enough to give accurate sensor reading 
       System.out.println(sReading); 
       if(batteryReading < 10.0) {
           //reading is not enough set HexColour to black to indicate needs new battery
           return "#000000"; 
           
       } else {
           if(sReading < 0) {
               throw new IllegalArgumentException(); 
           }
           
          else if(sReading < 32) {
               return "#00ff00"; 
           } 
           else if (sReading < 64) {
              return "#40ff00"; 
           }
           else if (sReading < 96) {
               return "#40ff00"; 
           }
           else if (sReading < 128) {
               return "#c0ff00"; 
           }
           else if (sReading < 160) {
               return "#ffc000"; 
           }
           else if (sReading < 192) {
               return "#ff8000";
           }
           else if (sReading < 224) {
               return "#ff4000"; 
           }
           else {
               //any value 256 and above is capped at the max red 
               return "#ff0000"; 
           }      
       }
   }

}
