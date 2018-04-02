/*
 * File:   Airport.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response.commands;

import model.data_store.AirportStore;
import model.data_store.FAAAirport;
import model.data_store.ServerStore;

public class Airport implements Command {


    ServerStore instance = ServerStore.getInstance();
    static ServerStore.Server currServer;

    private String UNKNOWN_AIRPORT = "error,unknown airport";

    /**
     * Args: String airport-name, String (weather,temp), int delay
     */
    private String RESPONSE_FORMAT = "airport,%s,%s,%d";

    private String request;

    private String airportCode;

    private int clientID;

    /**
     * Create the Airport command, which gets the information about an Airport.
     *
     * @param request the request parameters
     *                for Airport: airport
     *                where airport is the three-letter code of the airport
     * @param clientID the identification number of the client who made the request
     */
    public Airport(String request, int clientID) {
        this.request = request;
        this.clientID = clientID;
    }

    /**
     * Complete this command's operation.
     *
     * @return the response to be given back to the client
     */
    @Override
    public String execute() {
        String parseResult = parseRequest();

        if (parseResult != null) {
            return parseResult;
        }
        getServer(clientID);
        if (currServer == ServerStore.Server.local) {
            AirportStore airportStore = AirportStore.getInstance();
            String returnString = airportStore.getAirportName(airportCode) + "," + airportStore.getAirportWeather(clientID, airportCode) + "," + airportStore.getAirportDelay(airportCode);
            return returnString;
        }
        if (currServer == ServerStore.Server.faa) {
            FAAAirport instance = FAAAirport.getInstance();
            String returnString = instance.getAirportName(airportCode) + "," + instance.getAirportWeather(clientID, airportCode) + ","
                    + instance.getAirportDelay(airportCode);
            return returnString;

        }
        return "";
    }

    /**
     * Parse the request string and set the airport field with the relevant airport object.
     *
     * @return null if parse was successful, or an error message to be sent back to the client if unknown airport
     */
    private String parseRequest() {
        if (request == null || request.split(",").length > 1) {
            return INVALID_NUMBER_PARAMS;
        }

        AirportStore airportStore = AirportStore.getInstance();
        airportCode = request;

        if (!airportStore.isAirport(airportCode)) {
            return UNKNOWN_AIRPORT;
        }

        return null;
    }
    private void getServer(int id) {
       if(instance.getServer(id) == null) {

       } else {
           currServer = instance.getServer(id);
       }
    }
}
