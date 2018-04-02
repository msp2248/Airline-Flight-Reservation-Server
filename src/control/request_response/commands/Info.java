/*
 * File:   Info.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response.commands;

import model.components.*;
import model.data_store.AirportStore;
import model.data_store.ItineraryStore;

import java.text.SimpleDateFormat;
import java.util.List;

public class Info implements Command {

    private static String DATE_FORMAT = "h:mma";

    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    private static String INVALID_NUM_CONNECT = "error,invalid connection limit";

    private static String INVALID_SORT_ORDER = "error,invalid sort order";

    private static String FLIGHT_FORMAT = ",%d,%s,%s,%s,%s";

    private static int MAX_CONNECTIONS = 2;

    private enum SORT_ORDER {
        departure,
        arrival,
        airfare
    }

    private String request;

    private String origin;

    private String destination;

    private int connections = -1;

    private int clientID;

    private SORT_ORDER sortOrder = SORT_ORDER.departure;

    /**
     * Create the Info command, which acquires the itineraries which fit the client's parameters.
     *
     * @param request the request parameters
     *                for Info: origin,destination[,connections[,sort-order]]
     *                where origin is the three-letter code for the origin airport,
     *                destination is the three-letter code for the destination airport,
     *                connections is the maximum number of connections in the itinerary (0-2),
     *                sort-order is the method of sorting the itineraries (departure, arrival, airfare)
     * @param clientID the identification number of the client who made the request
     */
    public Info(String request, int clientID) {
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

        ItineraryStore itineraryStore = ItineraryStore.getInstance();
        List<Itinerary> itineraries;
        if (connections == -1) {
            itineraries = itineraryStore.getItineraries(origin, destination);
        } else {
            itineraries = itineraryStore.getItineraries(origin, destination, connections);
        }

        switch (sortOrder) {
            case departure:
                itineraries.sort(new DepartureComparator());
                break;
            case arrival:
                itineraries.sort(new ArrivalComparator());
                break;
            case airfare:
                itineraries.sort(new AirfareComparator());
                break;
        }

        InfoQueryStore infoQueryStore = InfoQueryStore.getInstance();
        infoQueryStore.setItineraries(clientID, itineraries);

        StringBuilder output = new StringBuilder("info," + itineraries.size());

        int itineraryID = 1;
        for (Itinerary itinerary : itineraries) {
            output.append("\n").append(itineraryID).append(",");
            List<Flight> flights = itinerary.getFlights();
            output.append(itinerary.getAirfare()).append(",").append(flights.size());

            for (TripComponent flight : flights) {
                output.append(String.format(FLIGHT_FORMAT, flight.getId(), flight.getOrigin().getName(),
                        DATE_FORMATTER.format(flight.getArrival()),
                        flight.getDestination().getName(), DATE_FORMATTER.format(flight.getDeparture())));
            }

            itineraryID++;
        }

        return output.toString();
    }

    /**
     * Parse the request string and set the fields with given values.
     *
     * @return null if parse was successful, or an error message to be sent back to the client
     */
    private String parseRequest() {
        String[] requestArr;
        if (request == null || ((requestArr = request.split(",")).length > 4) || requestArr.length < 2) {
            return INVALID_NUMBER_PARAMS;
        }

        AirportStore airportStore = AirportStore.getInstance();

        origin = requestArr[0];
        destination = requestArr[1];

        if (!airportStore.isAirport(origin)) {
            return UNKNOWN_ORIG;
        }
        if (!airportStore.isAirport(destination)) {
            return UNKNOWN_DEST;
        }

        try {  // parse optional connection limit
            if (!requestArr[2].equals("")) {
                connections = Integer.parseInt(requestArr[2]);

                if (connections > MAX_CONNECTIONS || connections < 0) {
                    return INVALID_NUM_CONNECT;
                }
            }
        } catch (IndexOutOfBoundsException ignored) {  // no connection limit specified, use default
        } catch (NumberFormatException e) {  // failure to parse string into int => invalid connection limit
            return INVALID_NUM_CONNECT;
        }

        try {  // parse optional sort order
            if (!requestArr[3].equals("")) {
                sortOrder = SORT_ORDER.valueOf(requestArr[3].toLowerCase());
            }
        } catch (IndexOutOfBoundsException e) {  // no sort order specified, use default
        } catch (IllegalArgumentException e) {  // failure to find sort order enum value
            return INVALID_SORT_ORDER;
        }

        return null;
    }

}
