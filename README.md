# aqmaps
This is the main part of the project for Informatics Large Practical Semester 1- 2020.  

This project is an API with an algorithm that takes in an initial long/lat location in Edinburgh and a date and some other input via command line. It then connects to a web server to find the air quality sensors in Edinburgh active on that day and creates an efficent path to allow the drone to fly to all of those sensors and return to the intial location while avoiding certain 'No Fly Zones' i.e certain buildings around Edinburgh

This project makes use of Java, Java 11 Http Client, JSON and GeoJSON Parsing. 

-App.java sets up necessary objects and launches the command line procedure. It is 'main()' effectively

-DroneFlightPath.java contains the implementation of the algorithm and as such returns the drone's flightpath 

-The report contains documentation on all classes within the project and my design decisions.

Any questions please email s1807298@ed.ac.uk 
