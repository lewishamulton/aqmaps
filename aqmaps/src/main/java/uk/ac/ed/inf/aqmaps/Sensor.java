package uk.ac.ed.inf.aqmaps;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Sensor {
    private double battery; 
    private double reading; 
    private String location; 
    private Square longLatSquare; 
    
    public static class Square{
        double LongSW; 
        double LatSW; 
        double LongNE; 
        double LatNE; 
    }
    
    public Sensor() {
       battery = 0.0; 
       reading = 0.0; 
       location = ""; 
         
        
    
    }
    
    public void ThreeWordsToLongLat() {
        //Start Http Reqest to get longLat Details from words folder
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getThreeWordsUrl(location))).build();
        //initialise response String
        HttpResponse<String> response = null;
        try {
            response = client.send(request, BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       

        
    }    
    
    public double getSensorReading() {
        return reading; 
    }
    public double getBatteryReading() {
        return battery; 
    }
    public String getThreeWordsLoc() {
        return location; 
    }
    
    public String getThreeWordsUrl(String threeWords) {
        //Java regex to break up 3words location 
        String delims = "[.]"; 
        String[] threeArray = threeWords.split(delims); 
        String Url = "words/"+threeArray[0]+"/"+threeArray[1]+"/"+threeArray[2]+"/details.json"; 
        return Url; 
    }


}
