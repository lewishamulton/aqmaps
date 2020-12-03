package uk.ac.ed.inf.aqmaps;

public class MapDate {
    private int year;
    private int month;
    private int day; 
    
    public MapDate(String y, String m, String d) {
        year = Integer.parseInt(y); 
        month = Integer.parseInt(m); 
        day = Integer.parseInt(d); 
    }
    public int getMonth(){
        return month; 
    }
    public int getDay() {
        return day; 
    }
    public int getYear() {
        return year; 
    }
    

}
