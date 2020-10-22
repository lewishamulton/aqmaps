package uk.ac.ed.inf.aqmaps;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type; 
import com.google.gson.reflect.TypeToken; 

public class SensorsToday {
    private ArrayList<Sensor> todaysSensors = new ArrayList<Sensor>(); 
    private Date todaysDate; 
    private double[] sensorReadings; 
    
    public SensorsToday(Date tDate) {
        todaysDate = tDate; 
        
        //getSensorsForToday
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(createUrlString())).build(); 
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
        sensorReadings = getSensorReadings(); 
    }
    
    public String createUrlString() {
        String url = "http://localhost:80/maps/" + todaysDate.getYear() + "/" + todaysDate.getMonth() + "/" + todaysDate.getDay() + "/air-quality-data.json";
        return url; 
    }
    
    public void parseSensorData(String sData) {
        Type listType =new TypeToken<ArrayList<Sensor>>(){}.getType();
        todaysSensors = new Gson().fromJson(sData, listType);
    }
    
    public double[] getSensorReadings() {
        double[] returnval = new double[2]; 
        return returnval; 
    }

}
