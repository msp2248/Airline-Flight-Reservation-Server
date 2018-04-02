/**
 * Flight Class
 * @author Meet Patel
 */

package model.components;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Flight implements TripComponent {
    private Airport origin;
    private Airport destination;
    private Date arrival;
    private Date departure;
    private int airfare;
    private int flightNum;

    /**
     * @param origin: Airport flight starts at
     * @param destination airport flight lands at
     * @param arrivalStr time the flight starts as, turned into a date object by this constructor
     * @param departureStr time the flight ends as, turned into a date object by this constructor
     * @param airfare how much the flight costs
     * @param flightNum flights unique id
     */
    public Flight(Airport origin, Airport destination, String arrivalStr, String departureStr, int flightNum, int airfare) throws ParseException{

        this.origin = origin;
        this.destination = destination;
        this.airfare = airfare;
        this.flightNum = flightNum;
        arrivalStr += "m";
        departureStr += "m";
        DateFormat form = new SimpleDateFormat("hh:mma");
        arrival = form.parse(arrivalStr);
        departure = form.parse(departureStr);
    }

    public Airport getOrigin() {
        return origin;
    }

    public Airport getDestination() {
        return destination;
    }

    @Override
    public int getId() {
        return flightNum;
    }

    @Override
    public Date getArrival() {
        return arrival;
    }

    @Override
    public Date getDeparture(){
        return departure;
    }

    public int getAirfare() {
        return airfare;
    }

    public int getFlightNum() {
        return flightNum;
    }


    /**
     * Shows the differance between the arrival time and the departure time
     * @return The time between arrival and departure
     */
    public int getTime() throws ParseException {
        long diffInMillies = arrival.getTime() - departure.getTime();
        int diffInMinutes = (int)TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diffInMinutes;
    }


}
