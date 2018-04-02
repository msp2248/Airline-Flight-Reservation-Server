/*
 * File:   Delete.java
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

public class Delete implements UndoableCommand {

    private String request;

    private String passenger;

    private String origin;

    private String destination;

    private boolean runSuccess;

    private Itinerary deletedItin;

    private int clientID;

    private static String DUPLICATE = "error,duplicate reservation";

    static String SUCCESS = "delete,successful";

    static String RESERVATION_NOT_FOUND = "error,reservation not found";

    private static String REDO_RESP_FORMAT = "redo,delete,%s,%s";

    private static String UNDO_RESP_FORMAT = "undo,delete,%s,%s";

    private static String FLIGHT_FORMAT = ",%d,%s,%s,%s,%s";

    private static String DATE_FORMAT = "h:mma";

    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    /**
     * Create the delete command, which deletes a reservation for a given passenger.
     *
     * @param request the request parameters
     *                for Delete: passenger,origin,destination
     *                where passenger is the name of the passenger holding the reservation,
     *                origin is the three-letter code for the reservation's origin airport,
     *                destination is the three-letter code for the reservation's destination airport
     * @param clientID the identification number of the client who made the request
     */
    public Delete(String request, int clientID) {
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

        ReservationStore reservationStore = ReservationStore.getInstance();

        deletedItin = reservationStore.delete(passenger, origin, destination);

        if (deletedItin == null) {
            return RESERVATION_NOT_FOUND;
        }

        runSuccess = true;
        return Delete.SUCCESS;
    }

    /**
     * Parse the request string and set the passenger, origin, and destination fields.
     *
     * @return null if parse was successful, or an error message to be sent back to the client
     */
    private String parseRequest() {
        String[] requestArr;
        if (request == null || !((requestArr = request.split(",")).length == 3)) {
            return INVALID_NUMBER_PARAMS;
        }

        passenger = requestArr[0];
        origin = requestArr[1];
        destination = requestArr[2];

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

        ReservationStore.Result result = reservationStore.reserve(passenger, deletedItin);

        switch (result) {
            case ALREADY_RESERVED:
                return DUPLICATE;
        }

        runSuccess = true;

        StringBuilder itineraryString = new StringBuilder();

        List<Flight> flights = deletedItin.getFlights();
        itineraryString.append(deletedItin.getAirfare()).append(",").append(flights.size());

        for (TripComponent flight : flights) {
            itineraryString.append(String.format(FLIGHT_FORMAT, flight.getId(), flight.getOrigin().getName(),
                    DATE_FORMATTER.format(flight.getArrival()),
                    flight.getDestination().getName(), DATE_FORMATTER.format(flight.getDeparture())));
        }

        return String.format(UNDO_RESP_FORMAT, passenger, itineraryString.toString());
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

        ReservationStore.Result result = reservationStore.delete(passenger, deletedItin);

        if (result.equals(ReservationStore.Result.NOT_FOUND)) {
            return RESERVATION_NOT_FOUND;
        }

        runSuccess = true;

        StringBuilder itineraryString = new StringBuilder();

        List<Flight> flights = deletedItin.getFlights();
        itineraryString.append(deletedItin.getAirfare()).append(",").append(flights.size());

        for (TripComponent flight : flights) {
            itineraryString.append(String.format(FLIGHT_FORMAT, flight.getId(), flight.getOrigin().getName(),
                    DATE_FORMATTER.format(flight.getArrival()),
                    flight.getDestination().getName(), DATE_FORMATTER.format(flight.getDeparture())));
        }

        return String.format(REDO_RESP_FORMAT, passenger, itineraryString.toString());
    }

    /**
     * Did this command run without error on last execution.
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
