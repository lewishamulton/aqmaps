package uk.ac.ed.inf.aqmaps; 

public class Words {
    String country; 
    
    coordinates coordinates; 
    public static class coordinates{
        double lng; 
        double lat;
      
    }
    //constructor used for when assigning return location as 'final' sensor 
    public Words() {
        coordinates = new coordinates(); 
    }
    
   
}
