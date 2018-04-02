/*
 * File:   Retrieve.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response.commands;

import model.components.Flight;
import model.components.Itinerary;
import model.components.TripComponent;
import model.data_store.AirportStore;
import model.data_store.ReservationStore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Retrieve implements Command {

    private static String DATE_FORMAT = "h:mma";

    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    private static String FLIGHT_FORMAT = ",%d,%s,%s,%s,%s";

    private static int MIN_PARAMS = 1;

    private static int MAX_PARAMS = 3;

    private static int PASS_IDX = 0;
    private String passenger;

    private static int ORIG_IDX = 1;
    private String origin = "";

    private static int DEST_IDX = 2;
    private String destination = "";

    private String request;

    private int clientID;

    /**
     * Create the Retrieve command, which acquires the reservations for a passenger.
     *
     * @param request the request parameters
     *                for Retrieve: passenger[,origin[,destination]]
     *                where passenger is the name of the passenger to retrieve reservations for,
     *                origin is the three-letter code of the reservation's origin airport,
     *                destination is the three-letter code of the reservation's destination airport
     * @param clientID the identification number of the client who made the request
     */
    public Retrieve(String request, int clientID) {
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

        ReservationStore reservationStore = ReservationStore.getInstance();
        List<Itinerary> itineraries = reservationStore.retrieve(passenger, origin, destination);

        StringBuilder output = new StringBuilder("retrieve," + itineraries.size());

        for (Itinerary itinerary : itineraries) {
            output.append("\n");
            List<Flight> flights = itinerary.getFlights();
            output.append(itinerary.getAirfare()).append(",").append(flights.size());

            for (TripComponent flight : flights) {
                output.append(String.format(FLIGHT_FORMAT, flight.getId(), flight.getOrigin().getName(),
                        DATE_FORMATTER.format(flight.getArrival()),
                        flight.getDestination().getName(), DATE_FORMATTER.format(flight.getDeparture())));
            }

        }

        return output.toString();
    }

    /**
     * Parse the request string and set the fields with given values.
     *
     * @return null if parse was successful, or an error message to be sent back to the client
     */
    private String parseRequest() {
        // check number parameters
        String[] requestArr;
        if (request == null ||
                ((requestArr = request.split(",")).length > MAX_PARAMS) || requestArr.length < MIN_PARAMS) {
            return INVALID_NUMBER_PARAMS;
        }

        passenger = requestArr[PASS_IDX];

        // check valid origin and destination
        AirportStore airportStore = AirportStore.getInstance();

        if (requestArr.length > MIN_PARAMS) {
            origin = requestArr[ORIG_IDX];

            // origin may be blank when destination is given
            if (!origin.equals("")) {
                if (!airportStore.isAirport(origin)) {
                    return UNKNOWN_ORIG;
                }
            }
        }

        if (requestArr.length > MIN_PARAMS + 1) {
            destination = requestArr[DEST_IDX];
            if (!airportStore.isAirport(destination)) {
                return UNKNOWN_DEST;
            }
        }

        return null;
    }
}
