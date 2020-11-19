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
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;


public class NoFlyZone {
    ArrayList<Polygon> noFlyZones; 
    
    public NoFlyZone() {
       String url = "http://localhost:80/buildings/no-fly-zones.geojson"; 
       System.out.println(url); 
      //get no-fly-zones polygons
       HttpClient client = HttpClient.newHttpClient();
       HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build(); 
       HttpResponse<String> response = null;
       try {
           response = client.send(request, BodyHandlers.ofString());
       } catch (IOException e) {
           e.printStackTrace();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       String geoJsonString = response.body(); 
       parseGeoJson(geoJsonString); 
       
    }
    
    private void parseGeoJson(String geoString) {
        
        FeatureCollection fc = FeatureCollection.fromJson(geoString); 
        List<Feature> fList = fc.features(); 
        //goes through each feature to see if polygon
        for(int i = 0; i < fList.size(); i ++) {
             Geometry g =  fList.get(i).geometry(); 
            if(g instanceof Polygon) {
                noFlyZones.add((Polygon)g); 
            }
        }  
    }
    
    public boolean inNoFlyZone(double newLong, double newLat) {
        
        var droneLoc = Point.fromLngLat(newLong, newLat); 
        var droneFeature = Feature.fromGeometry((Geometry)droneLoc); 
        var droneSingleton = FeatureCollection.fromFeature(droneFeature);
        
        //creates a collection of the NoFlyObjects 
        var noFlyArray = new Feature[noFlyZones.size()]; 
        for(int i = 0; i < noFlyZones.size(); i ++) {
            var noFlyFeature = Feature.fromGeometry(((Geometry)noFlyZones.get(i))); 
            noFlyArray[i] = noFlyFeature; 
        }
        var noFlyCollection = FeatureCollection.fromFeatures(noFlyArray); 
        
        //checks if there are any points within the polygons and adds those to a feature collection
        var anyPoints = TurfJoins.pointsWithinPolygon(droneSingleton,noFlyCollection); 
        
        //if feature collection is empty then drone not in No Fly Zone
       if(anyPoints.features().isEmpty()) {
           return false; 
       } else {
           return true; 
       }
    }
    



}
