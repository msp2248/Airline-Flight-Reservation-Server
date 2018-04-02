/*
 * File:   Reserve.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response.commands;

import model.components.Flight;
import model.components.Itinerary;
import model.components.TripComponent;
import model.data_store.ReservationStore;

import java.text.SimpleDateFormat;
import java.util.List;

public class Reserve implements Command, UndoableCommand {

    private static String INVALID_ID = "error,invalid id";

    private static String DUPLICATE = "error,duplicate reservation";

    private static String RESERVATION_NOT_FOUND = "error,reservation not found";

    private static String SUCCESS = "reserve,successful";

    private static String FLIGHT_FORMAT = ",%d,%s,%s,%s,%s";

    private static String UNDO_RESP_FORMAT = "undo,reserve,%s,%s";

    private static String REDO_RESP_FORMAT = "redo,reserve,%s,%s";

    private static String DATE_FORMAT = "h:mma";

    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    private static int NUM_PARAMS = 2;

    private String request;

    private String passengerName;

    private Itinerary itinerary;

    private int clientID;

    private boolean runSuccess;

    /**
     * Create the Reserve command, which creates a reservation for a passenger.
     *
     * @param request the request parameters
     *                for Reserve: id,passenger
     *                where id is the unique ID of the itinerary to reserve,
     *                passenger is the name of the passenger creating the reservation
     * @param clientID the identification number of the client who made the request
     */
    public Reserve(String request, int clientID) {
        this.request = request;
        this.clientID = clientID;
        this.runSuccess = false;
    }

    /**
     * Complete this command's operation.
     *
     * @return the response to be given back to the client
     */
    @Override
    public String execute() {
        runSuccess = false;

        String parseResult = parseRequest();
        if (parseResult != null) {
            return parseResult;
        }

        runSuccess = false;

        ReservationStore reservationStore = ReservationStore.getInstance();
        ReservationStore.Result reserveResult = reservationStore.reserve(passengerName, itinerary);

        switch (reserveResult) {
            case ALREADY_RESERVED:
                return DUPLICATE;
        }

        runSuccess = true;
        return SUCCESS;
    }

    /**
     * Parse the request string and set the fields with given values.
     *
     * @return null if parse was successful, or an error message to be sent back to the client
     */
    private String parseRequest() {
        String[] requestArr;
        if (request == null || ((requestArr = request.split(",")).length != NUM_PARAMS)) {
            return INVALID_NUMBER_PARAMS;
        }

        int itineraryID;
        try {
            itineraryID = Integer.parseInt(requestArr[0]);
        } catch (NumberFormatException e) {  // NAN
            return INVALID_ID;
        }

        InfoQueryStore infoQueryStore = InfoQueryStore.getInstance();

        if (!infoQueryStore.isValidID(clientID, itineraryID)) {
            return INVALID_ID;
        }

        itinerary = infoQueryStore.getItinerary(clientID, itineraryID);

        passengerName = requestArr[1];

        return null;
    }

    /**
     * Undo this command.
     *
     * @return output to be directed to the client
     */
    @Override
    public String undo() {
        runSuccess = false;

        ReservationStore reservationStore = ReservationStore.getInstance();
        ReservationStore.Result reserveResult = reservationStore.delete(passengerName, itinerary);

        switch (reserveResult) {
            case NOT_FOUND:
                return RESERVATION_NOT_FOUND;
        }

        runSuccess = true;

        StringBuilder itineraryString = new StringBuilder();

        List<Flight> flights = itinerary.getFlights();
        itineraryString.append(itinerary.getAirfare()).append(",").append(flights.size());

        for (TripComponent flight : flights) {
            itineraryString.append(String.format(FLIGHT_FORMAT, flight.getId(), flight.getOrigin().getName(),
                    DATE_FORMATTER.format(flight.getArrival()),
                    flight.getDestination().getName(), DATE_FORMATTER.format(flight.getDeparture())));
        }

        return String.format(UNDO_RESP_FORMAT, passengerName, itineraryString.toString());
    }

    /**
     * Redo this command.
     *
     * @return output to be directed to the client
     */
    @Override
    public String redo() {
        runSuccess = false;

        ReservationStore reservationStore = ReservationStore.getInstance();
        ReservationStore.Result reserveResult = reservationStore.reserve(passengerName, itinerary);

        switch (reserveResult) {
            case ALREADY_RESERVED:
                return DUPLICATE;
        }

        runSuccess = true;

        StringBuilder itineraryString = new StringBuilder();

        List<Flight> flights = itinerary.getFlights();
        itineraryString.append(itinerary.getAirfare()).append(",").append(flights.size());

        for (TripComponent flight : flights) {
            itineraryString.append(String.format(FLIGHT_FORMAT, flight.getId(), flight.getOrigin().getName(),
                    DATE_FORMATTER.format(flight.getArrival()),
                    flight.getDestination().getName(), DATE_FORMATTER.format(flight.getDeparture())));
        }

        return String.format(REDO_RESP_FORMAT, passengerName, itineraryString.toString());
    }

    /**
     * Did this command run without error on last execution?
     *
     * @return true if the command ran without error, false if any error occurred
     */
    @Override
    public boolean wasSuccessful() {
        return runSuccess;
    }

    /**
     * Get the ID of the client which made this request.
     *
     * @return client ID number
     */
    @Override
    public int getClientID() {
        return clientID;
    }

}
