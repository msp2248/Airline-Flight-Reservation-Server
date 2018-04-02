/**
 * @author: Shawn Struble
 */
package model.data_store;

import model.components.Airport;
import model.components.Flight;
import sun.awt.SunHints;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlightStore {
    private static String flightsFilePath;
    private static FlightStore instance;
    private Map<Integer,Flight> flights = new HashMap();
    private FlightStore(String filePath) throws IOException, ParseException {
        flightsFilePath = filePath;
        readFlights();

    }
    /**
     * Throws exception if called before FlightStore singleton instance is instantiated
     * @return singleton instance of FlightStore
     */
    public static FlightStore getInstance() {
        if (instance == null) {
            throw new RuntimeException("FlightStore not properly instantiated!");
        }
        return instance;

    }
    /**
     *
     * @param filePath
     * @return the singleton instance of FlightStore
     */
    public static FlightStore getInstance(String filePath) throws Exception {
        if(instance == null) {
            instance = new FlightStore(filePath);
        }
        return instance;
    }
    public Flight getFlight(Integer flightNum) {
        return flights.get(flightNum);
    }
    /**
     * populate the flights list from file
     *
     * The list of TTA flights between airports in its route network is in comma-separated-value format with six fields per line
     * as origin-airport-code,destination-airport-code,departure-time, arrival-time,flight-number,airfare.
     * The departure and arrival times are in the local time for each airport.
     * Some flights arrive on the day after departure, i.e. the departure time is pm and the arrival time is am in the next day.
     * The AFRS will read this file on startup.
     */
    private void readFlights() throws IOException, ParseException {
        String line;
        AirportStore store = AirportStore.getInstance();
        try(BufferedReader br = new BufferedReader(new FileReader(flightsFilePath))) {
            while((line = br.readLine()) != null) {
                //Variables to make flight object
                Airport originAP,destinationAP;
                String arrivalTime,departTime;
                int flightNum, flightAirfare;
                String[] flightsInfo = line.split(",");
                //Parse the info
                originAP = store.getAirportObject(flightsInfo[0]);
                destinationAP = store.getAirportObject(flightsInfo[1]);
                departTime = flightsInfo[2];
                arrivalTime = flightsInfo[3];
                flightNum = Integer.parseInt(flightsInfo[4]);
                flightAirfare = Integer.parseInt(flightsInfo[5]);
                //Create the object and add it to flight list
                Flight flightObj = new Flight(originAP,destinationAP,departTime,arrivalTime,flightNum,flightAirfare);
                flights.put(flightNum,flightObj);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     *
     * @return list containing all flights
     */
    public List<Flight> getAllFlights() {
        List<Flight> returnList = new ArrayList<Flight>(flights.values());
        return returnList;
    }

    /**
     *@param orgCode code to match to all flights origin codes
     * @return a list of flights with common origin.
     */
    public List<Flight> getOrgFlights(String orgCode) {
        List<Flight> returnList = new ArrayList<>();
        List<Flight> allFlights = getAllFlights();
        for(int i = 0; i < allFlights.size(); i++ ) {
            if(allFlights.get(i).getOrigin().getCode().equals(orgCode.toUpperCase())) {
                returnList.add(allFlights.get(i));
            }
        }
        return returnList;
    }
    /**
     *
     * @param destCode code to match to all flights destination code
     * @return a list of flights with common destination
     */
    public List<Flight> getDestFlights(String destCode) {
        List<Flight> returnList = new ArrayList<>();
        List<Flight> allFlights = getAllFlights();
        for(int i = 0; i < allFlights.size(); i++ ) {
            if(allFlights.get(i).getDestination().getCode().equals(destCode.toUpperCase())) {
                returnList.add(allFlights.get(i));
            }
        }
        return returnList;
    }
}
