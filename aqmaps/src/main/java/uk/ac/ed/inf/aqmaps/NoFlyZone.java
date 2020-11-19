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

public class NoFlyZone {
    ArrayList<Polygon> noFlyZones; 
    
    public NoFlyZone(int portNo) {
       String url = "http://localhost:"+ portNo +"/buildings/no-fly-zones.geojson"; 
       
      //get noflyzones polygons
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
             Geometry g=  fList.get(i).geometry(); 
            if(g instanceof Polygon) {
                noFlyZones.add((Polygon)g); 
            }
        }  
    }
    



}
