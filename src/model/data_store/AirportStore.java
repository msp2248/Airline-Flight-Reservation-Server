/**
 * @author: Shawn Struble
 */
package model.data_store;

import control.request_response.ClientIdentifierManager;
import control.request_response.ClientIdentifierSubject;
import model.components.Airport;

import java.io.*;
import java.util.*;

public class AirportStore implements AirportData {

    private ClientIdentifierSubject clientIDManager;

    private static AirportStore instance = null;
    private List<Airport> airports = new ArrayList<Airport>();
    private static String airportsPath;
    private static String airportWeatherPath;
    private static String airportConnectionsPath;
    private static String airportDelayPath;
    public AirportStore(String airportsPath,String airportWeatherPath, String airportConnectionsPath,
                        String airportDelay, ClientIdentifierSubject clientIDManager) {
        this.airportsPath = airportsPath;
        this.airportWeatherPath = airportWeatherPath;
        this.airportConnectionsPath = airportConnectionsPath;
        this.airportDelayPath = airportDelay;
        this.clientIDManager = clientIDManager;

        readAirports();
        readAirPortWeather();
        readAirportDelay();
        readAirPortConnections();
    }
    /**
     *
     * @return the singleton instance of AirportStore
     * throws an error if instance was not created with file paths
     */
    public static AirportStore getInstance() {
        if (instance == null) {
            throw new RuntimeException("AirportStore not properly instantiated!");
        }
        return instance;
    }

    /**
     * creates instance with all file paths
     * @param airportsPath
     * @param airportWeatherPath
     * @param airportConnectionsPath
     * @param airportDelay
     * @return the singleton instance of a class
     */
    public static AirportStore getInstance(String airportsPath, String airportWeatherPath, String airportConnectionsPath,
                                           String airportDelay, ClientIdentifierManager clientIDManager) {
        if (instance == null) {
            instance = new AirportStore(airportsPath,airportWeatherPath,airportConnectionsPath,airportDelay, clientIDManager);
        }
        return instance;

    }
    /**
     * Populates the airports list
     * TODO: Add check to make sure same airport doesn't get added twice?
     */
    private void readAirports() {
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(airportsPath))){
            while((line = br.readLine()) != null) {
                Airport airport = new Airport(clientIDManager);
                String[] airportInfo = line.split(",");
                airport.setCode(airportInfo[0]); // Airport Code Ex. "ATL"
                airport.setName(airportInfo[1]); // Name Ex. "Atlanta"
                airports.add(airport);

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
    /**
     * reads in airport delays from file and adds them to their respective objects
     */
    private void readAirportDelay() {
        String line = "";
        try(BufferedReader br = new BufferedReader(new FileReader(airportDelayPath))) {
            int i = 0;
            while((line = br.readLine()) != null) {
                String[] airportDelayInfo = line.split(",");
                Airport airport = airports.get(i);
                airport.setDelayTime(Integer.parseInt(airportDelayInfo[1]));
                i++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
    /**
     * reads in airport connection time from file and adds them to their respective objects
     */
    private void readAirPortConnections() {
        String line = "";
        try(BufferedReader br = new BufferedReader(new FileReader(airportConnectionsPath))) {
            int i = 0;
            while((line = br.readLine()) != null) {
                String[] connectionInfo = line.split(",");
                Airport airport = airports.get(i);
                airport.setConnectionTime(Integer.parseInt(connectionInfo[1]));
                i++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Adds weather info to airport objects
     * TODO: finish this method
     */
    private void readAirPortWeather() {
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(airportWeatherPath))) {
            int count = 0;
            while ((line = br.readLine()) != null) {
                ArrayList<String> weatherConditions= new ArrayList<String>();
                String[] weatherInfo = line.split(",");
                for (int i = 1; i < weatherInfo.length-1; i += 2) {
                    weatherConditions.add(weatherInfo[i] + "," + weatherInfo[i+1]);
                }
                airports.get(count).setWeather(weatherConditions);
                count++;
            }
            count++;
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * finds airport object in airports arraylist based on 3 digit airport code
     * @param airportCode - used to find airport object
     * @return - airport object
     * returns null on failure
     */
     Airport getAirportObject(String airportCode) {
        airportCode = airportCode.toUpperCase();
        Airport returnPort;
        for(int i = 0; i < airports.size(); i++) {
            if(airportCode.equals(airports.get(i).getCode())) {
                returnPort = airports.get(i);
                return returnPort;
            }
        }
        return null;

    }

    /**
     * Checks if the provided code is a three letter code for an airport in the system.
     *
     * @param code potential airport code to check
     * @return true if the code is valid for an airport, false if not a valid code or no airport found
     */
    public boolean isAirport(String code) {
        Airport airport = getAirportObject(code);
        return airport != null;
    }

    /**
     * Get the full name of an airport.
     *
     * @param code the three letter code of an airport
     *      Note: this code is assumed valid, check with isAirport before calling
     * @return full name of the airport
     */
    public String getAirportName(String code) {
        Airport airport = getAirportObject(code);
        return airport.getName();
    }

    /**
     * Get the current weather at an airport.
     *
     * @param code the three letter code of an airport
     *      Note: this code is assumed valid, check with isAirport before calling
     * @return current weather at airport
     */
    public String getAirportWeather(int CID, String code) {
        Airport airport = getAirportObject(code);
        return airport.getWeather(CID);
    }

    /**
     * Get the delay at an airport.
     *
     * @param code the three letter code of an airport
     *      Note: this code is assumed valid, check with isAirport before calling
     * @return delay at airport
     */
    public String getAirportDelay(String code) {
        Airport airport = getAirportObject(code);
        return "" + airport.getDelayTime();
    }
}
