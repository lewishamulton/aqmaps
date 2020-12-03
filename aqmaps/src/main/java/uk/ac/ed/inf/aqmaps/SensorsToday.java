package uk.ac.ed.inf.aqmaps;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Date;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type; 
import com.google.gson.reflect.TypeToken; 

public class SensorsToday {
    private ArrayList<Sensor> todaysSensors = new ArrayList<Sensor>(); 
    private MapDate todaysDate; 
    private int portNo; 

    public SensorsToday(MapDate tDate,int pNo) {
        todaysDate = tDate; 
        portNo = pNo; 
        //getSensorsForToday
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder().uri(URI.create(createUrlString())).build(); 
        HttpResponse<String> response = null;
        try {
            response = client.send(request, BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String todaysSensorData = response.body(); 
        
        parseSensorData(todaysSensorData);  
        
      
    }
    
    public String createUrlString() {
        String month = "" + todaysDate.getMonth();
        String day = "" + todaysDate.getDay();
        if(todaysDate.getMonth() < 10) {
             month = "0"+ todaysDate.getMonth(); 
        }
        if(todaysDate.getDay() < 10) {
            day = "0"+ todaysDate.getDay(); 
        }
        String url = "http://localhost:" + portNo + "/maps/" + todaysDate.getYear() + "/" + month + "/" + day + "/air-quality-data.json";
        return url; 
    }
    
    public void parseSensorData(String sData) {
        Type listType =new TypeToken<ArrayList<Sensor>>(){}.getType();
        todaysSensors = new Gson().fromJson(sData, listType);
    }
    
    public ArrayList<Sensor> getTodaysSensors(){
        return todaysSensors; 
    }
    


}
