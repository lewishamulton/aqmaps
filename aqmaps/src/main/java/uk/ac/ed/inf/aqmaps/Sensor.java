package uk.ac.ed.inf.aqmaps;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Sensor {
    private double battery; 
    private double reading; 
    private String location; 
    Words sensorWord; 
    
    
   
    
    public Sensor() {
       battery = 0.0; 
       reading = 0.0; 
       location = ""; 
         
        
    
    }
    
    public void ThreeWordsToLongLat() {
        //Start Http Request to get longLat Details from words folder
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getThreeWordsUrl())).build();
        //initialise response String
        HttpResponse<String> response = null;
        try {
            response = client.send(request, BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        String wordData = response.body(); 
        parseThreeWords(wordData); 
       

        
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

    private String getThreeWordsUrl() {
        //Java regex to break up 3words location 
        String delims = "[.]"; 
        String[] threeArray = location.split(delims); 
        String Url = "http://localhost:80/words/"+threeArray[0]+"/"+threeArray[1]+"/"+threeArray[2]+"/details.json"; 
        return Url; 
    }
    
    private void parseThreeWords(String tWords) {
         sensorWord =new Gson().fromJson(tWords, Words.class);
    }


}
