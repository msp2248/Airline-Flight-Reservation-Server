/**
 * Itinerary Class
 * @author Meet Patel
 */
package model.components;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Itinerary implements TripComponent {

    List<Flight> flights;
    int id;
    int connections;

    public Itinerary(List<Flight> flights) {
        this.flights = flights;
        connections = flights.size();
    }

    /**
     * Calls getTime on all of the child classes of the Itinerary
     * @return sum of all of the child class' time.
     * @throws ParseException
     */
    @Override
    public int getTime() throws ParseException {
        int res = 0;
        for (TripComponent f : flights) {
            res += f.getTime();
        }
        return res;
    }

    /**
     * Calls getAirfare on all of the child classes of the Itinerary
     * @return sum of all of the child class' airfare.
     * @throws ParseException
     */
    @Override
    public int getAirfare() {
        int res = 0;
        for (TripComponent f : flights) {
            res += f.getAirfare();
        }
        return res;
    }

    /**
     * Remove a flight from an itinerary
     * @throws Exception
     */
    public void remove(TripComponent f) throws Exception {
        if (flights.contains(f)) {
            flights.remove(f);
        } else {
            throw new Exception("This flight is not in the itinerary");
        }
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public int getId() {
        return id;
    }

    @Override
    public Airport getOrigin() {
        return flights.get(0).getOrigin();
    }

    @Override
    public Airport getDestination() {
        return flights.get(flights.size() - 1).getDestination();
    }

    @Override
    public Date getArrival() {
        return flights.get(flights.size()-1).getArrival();
    }

    @Override
    public Date getDeparture() {
        return flights.get(0).getDeparture();
    }
    public List<Integer> getFlightIntegers() {
        List<Integer> flightNums = new ArrayList<Integer>();
        for(int i = 0; i < flights.size(); i++) {
            flightNums.add(flights.get(i).getId());
        }
        return flightNums;
    }

}

