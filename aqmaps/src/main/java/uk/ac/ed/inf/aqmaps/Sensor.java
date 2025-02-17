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
    private String reading; 
    private String location; 
    Words sensorWord; 
    
    
   
    
    public Sensor() {
       battery = 0.0; 
       reading = "0.0"; 
       location = ""; 
    }
    
    //for when Sensor is return location 
    public Sensor(double initLong, double initLat) {
        sensorWord = new Words(); 
        sensorWord.coordinates.lat = initLat; 
        sensorWord.coordinates.lng = initLong; 
    }
    
    public void ThreeWordsToLongLat(int pNo) {
        //Start Http Request to get longLat Details from words folder
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getThreeWordsUrl(pNo))).build();
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
        var s = 0.0; 
        try {
            s= Double.parseDouble(reading);
        } catch (NumberFormatException e) {
            return 0.0; 
        }
        return s; 
    }
    public double getBatteryReading() {
        return battery; 
    }
    public String getThreeWordsLoc() {
        return location; 
    }
    
    public boolean equals(Sensor otherSensor) {
        if(this.location == otherSensor.location) {
            return true; 
        } else {
            return false; 
        }
    }

    private String getThreeWordsUrl(int portNo) {
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
