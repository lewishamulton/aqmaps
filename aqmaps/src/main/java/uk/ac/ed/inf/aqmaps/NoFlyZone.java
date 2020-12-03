package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.MultiPolygon;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;


public class NoFlyZone {
    private ArrayList<Polygon> noFlyZones = new ArrayList<Polygon>(); 
    private ArrayList<MultiPolygon> noFlyZonesMultiPolygon = new ArrayList<MultiPolygon>(); 
    
    public NoFlyZone() {
       String url = "http://localhost:80/buildings/no-fly-zones.geojson"; 
      //get no-fly-zones polygons
       var client = HttpClient.newHttpClient();
       var request = HttpRequest.newBuilder().uri(URI.create(url)).build(); 
       HttpResponse<String> response = null;
       try {
           response = client.send(request, BodyHandlers.ofString());
           System.out.println(response); 
       } catch (IOException e) {
           e.printStackTrace();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       var geoJsonString = response.body(); 
       parseGeoJson(geoJsonString); 
       
    }
    
    public ArrayList<Polygon> getNoflyZones(){
        return noFlyZones; 
    }
    
    private void parseGeoJson(String geoString) {
        
        FeatureCollection fc = FeatureCollection.fromJson(geoString); 
        List<Feature> fList = fc.features(); 
        //goes through each feature, converting to polygon and adding to ArrayList 
        for(int i = 0; i < fList.size(); i ++) {
             var g =  fList.get(i).geometry();
             noFlyZones.add((Polygon)g); 
        }  
    }
    
    public boolean inNoFlyZone(double newLong, double newLat, double[][] lineCoords) {
        
        //creates a feature of the new drone location 
        var droneNewLoc = Point.fromLngLat(newLong, newLat); 
        var newLocFeature = Feature.fromGeometry((Geometry)droneNewLoc); 
        /*creates a feature list of points the drone will go over in the one move
         * this prevents the drone going over the NoFlyZone in one move as it is checking 
         * the overall path of the drone and not just if its final location is in the NoFlyZone*/
        Feature[] featureArray = new Feature[lineCoords.length]; 
        Point[] pointArray = new Point[lineCoords.length]; 
         
        featureArray[0] = newLocFeature; 
        pointArray[0] = droneNewLoc; 
        
        for(int i = 1; i <=(lineCoords.length -1); i ++) {
           
           //gets all the points along the line and turns them into a features
            var f = Point.fromLngLat(lineCoords[i][0], lineCoords[i][1]); 
            pointArray[i] = f; 
            featureArray[i] = Feature.fromGeometry((Geometry)f); 
        } 
        
        
        //creates a feature collection of the different flight points a drone will go through to its new location 
        var droneFlightPoints = FeatureCollection.fromFeatures(featureArray);  
        
        //creates a collection of the NoFlyObjects 
        var noFlyArray = new Feature[noFlyZones.size()]; 
        for(int i = 0; i < noFlyZones.size(); i ++) {
            var noFlyFeature = Feature.fromGeometry(((Geometry)noFlyZones.get(i))); 
            noFlyArray[i] = noFlyFeature; 
        }
        var noFlyCollection = FeatureCollection.fromFeatures(noFlyArray); 
        
        /*checks if there are any points during the drones flight from its old location to the new one 
         * within the NoFlyZone polygons and adds those to a feature collection */
        var anyPoints = TurfJoins.pointsWithinPolygon(droneFlightPoints,noFlyCollection);
        Polygon[] polyArray = new Polygon[noFlyZones.size()]; 
        for(int i =0; i < noFlyZones.size(); i++) {
            polyArray[i] = noFlyZones.get(i); 
        }
        
        Boolean result = true; 
        for(int i =0; i <polyArray.length; i++) {
            for(int j =0; j<pointArray.length; j++) {
              if(TurfJoins.inside(pointArray[j], polyArray[i])) {
                  result = result && false; 
                  System.out.println(result); 
              }
            }
        }
      
        //if feature collection is empty then drone not in No Fly Zone
        System.out.println("AnyPoints Result: " +anyPoints.features().isEmpty()); 
        System.out.println("Boolean Result: " +result); 
       if(anyPoints.features().isEmpty() || result == true) {
           return false; 
       } else {
           return true; 
       }
    }
    



}
