/**
 * @author: Shawn Struble
 */
package model.data_store;

import model.components.Airport;
import model.components.Flight;
import model.components.Itinerary;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Store most recently searched itineraries.
 *
 */
public class ItineraryStore {
    private HashMap<Integer, Itinerary> itineraryMap = new HashMap<>();
    private static ItineraryStore instance = null;

    /**
     *
     * @return singleton instance of itinerarystore
     */
    public static ItineraryStore getInstance() {
        if(instance == null) {
            try {
                instance = new ItineraryStore();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
    private ItineraryStore () throws ParseException {

    }
    /**
     *
     * @param orgCode
     * @param destCode
     * @return list of direct flight itineraries
     */
    private List<Itinerary> getDirectItineries(String orgCode, String destCode) {
        FlightStore instance = FlightStore.getInstance();
        List<Flight> allFlights = instance.getAllFlights();
        List<Flight> matchingFlights = new ArrayList<>();

        for(int i = 0; i < allFlights.size(); i++) { //Get all direct flights with matching orig and dest
            if(allFlights.get(i).getOrigin().getCode().equals(orgCode.toUpperCase()) &&
                    allFlights.get(i).getDestination().getCode().equals(destCode.toUpperCase())) {
                matchingFlights.add(allFlights.get(i));
            }
        }

        List<Itinerary> returnItineraries = new ArrayList<>();
        for(int i = 0; i < matchingFlights.size(); i++) {
            List<Flight> flightList = new ArrayList<>();
            flightList.add(matchingFlights.get(i));
            Itinerary newItinerary = new Itinerary(flightList);
            returnItineraries.add(newItinerary);
        }

        return returnItineraries;
    }

    /**
     *
     * @param orgCode - code for starting airport
     * @param destCode - code for ending airport
     * @return a list of itineraries with just one connection to reach ending airport
     */
    private List<Itinerary> getOneConnectionItinerary(String orgCode, String destCode) {
        FlightStore instance = FlightStore.getInstance();
        List<Flight> allFlights = instance.getAllFlights();
        List<Flight> matchingOrgFlights = instance.getOrgFlights(orgCode);
        List<Flight> matchingDestFlights = instance.getDestFlights(destCode);
        List<Itinerary> returnItinerary = new ArrayList<>();
        for(int i = 0; i < matchingOrgFlights.size(); i++) {
            for(int j = 0; j < matchingDestFlights.size(); j++) {
                if (matchingOrgFlights.get(i).getDestination().getCode().equals(matchingDestFlights.get(j).getOrigin().getCode())) {
                    long diffInMilli = matchingDestFlights.get(j).getArrival().getTime() - matchingOrgFlights.get(i).getDeparture().getTime();
                    int diffInMinutes = (int) TimeUnit.MINUTES.convert(diffInMilli, TimeUnit.MILLISECONDS);

                    if(diffInMinutes >= matchingDestFlights.get(j).getOrigin().getDelayTime() + matchingDestFlights.get(j).getOrigin().getConnectionTime()) {
                        List<Flight> flightList = new ArrayList<>();
                        flightList.add(matchingOrgFlights.get(i));
                        flightList.add(matchingDestFlights.get(j));
                        Itinerary newItinerary = new Itinerary(flightList);
                        returnItinerary.add(newItinerary);
                    }
                }
            }
        }
        return returnItinerary;


    }

    /**
     *
     * @param orgCode - starting airport code
     * @param destCode - ending airport code
     * @return a list of itineraries with 2 addition legs to reach destination (3 flights total)
     */
    private List<Itinerary> getTwoConnectionItinerary(String orgCode, String destCode) {
        FlightStore instance = FlightStore.getInstance();
        List<Flight> allFlights = instance.getAllFlights();
        List<Flight> matchingOrgFlights = instance.getOrgFlights(orgCode);
        List<Flight> matchingDestFlights = instance.getDestFlights(destCode);
        List<Itinerary> returnItinerary = new ArrayList<>();
        /*
            For all of the flights with matching origin codes, gets destination code and
                compares to all other flights origins.
                    when a match is found tests to make sure that connection is possible by the required time restraints
                        for the possible connecting flight compares to the list of with ending destinations.
                            same process is repeated as above except with connecting flight and possible destination flight.
                                When a match is found an itinerary is created and added to the list to be returned.
         */
        for(int i = 0; i < matchingOrgFlights.size(); i++) {
            for(int j = 0 ; j <allFlights.size(); j++) {
                if((matchingOrgFlights.get(i).getDestination().getCode().equals(allFlights.get(j).getOrigin().getCode())) && (!(allFlights.get(j).getOrigin().getCode().equals(destCode.toUpperCase())))) {
                    long diffInMilli = allFlights.get(j).getArrival().getTime() - matchingOrgFlights.get(i).getDeparture().getTime();
                    int diffInMinutes = (int) TimeUnit.MINUTES.convert(diffInMilli, TimeUnit.MILLISECONDS);
                    if(diffInMinutes >= allFlights.get(j).getOrigin().getDelayTime() + allFlights.get(j).getOrigin().getConnectionTime()) {
                        for(int z = 0; z < matchingDestFlights.size(); z++) {
                            if((allFlights.get(j).getDestination().getCode().equals(matchingDestFlights.get(z).getOrigin().getCode())) && (!(allFlights.get(j).getDestination().getCode().equals(orgCode.toUpperCase())))) {
                                diffInMilli = matchingDestFlights.get(z).getArrival().getTime() - allFlights.get(j).getDeparture().getTime();
                                diffInMinutes = (int)TimeUnit.MINUTES.convert(diffInMilli, TimeUnit.MILLISECONDS);
                                if(diffInMinutes > matchingDestFlights.get(z).getOrigin().getDelayTime() + matchingDestFlights.get(z).getOrigin().getConnectionTime()) {
                                    List<Flight> flightList = new ArrayList<>();
                                    flightList.add(matchingOrgFlights.get(i));
                                    flightList.add(allFlights.get(j));
                                    flightList.add(matchingDestFlights.get(z));
                                    Itinerary newItinerary = new Itinerary(flightList);
                                    returnItinerary.add(newItinerary);
                                }
                            }
                        }
                    }
                }

            }
        }


        return returnItinerary;
    }
    /**
     * Returns all itineraries with origin/destination combination
     * @param origin - starting airport code
     * @param destination - ending airport code
     * @return
     */
    public List<Itinerary> getItineraries(String origin,String destination) {
        List<Itinerary> returnItinerary = new ArrayList<>();
        List<Itinerary> directs = getDirectItineries(origin,destination);
        List<Itinerary> single = getOneConnectionItinerary(origin,destination);
        List<Itinerary> multi = getTwoConnectionItinerary(origin,destination);
        for (int i = 0; i < directs.size(); i++) {
            returnItinerary.add(directs.get(i));
        }
        for(int i = 0; i < single.size(); i++) {
            returnItinerary.add(single.get(i));
        }

        for(int i = 0; i < multi.size(); i++) {
            returnItinerary.add(multi.get(i));
        }

        return returnItinerary;

    }
    /**
     *
     * @param origin starting airport code
     * @param destination ending airport code
     * @param legs max amount of connections
     * @return list with corresponding origins and destination and max number of legs
     */
    public List<Itinerary> getItineraries(String origin, String destination, int legs) {
        if(legs == 0) {
            return getDirectItineries(origin,destination);
        }
        if(legs == 1) {
            return getOneConnectionItinerary(origin, destination);
        }
        else {
            return getTwoConnectionItinerary(origin,destination);
        }

    }

    public enum FilterBy {
        ORIGIN,
        DESTINATION
    }
    /**
     * Filter a list of itineraries by their origin or destination airport.
     *
     * @param itineraries original list of itineraries to be filtered
     * @param airportCode airport to use in filter, must be valid
     * @param filterBy is the airport the origin or destination
     * @return filtered list of itineraries
     */
    public static List<Itinerary> filterItins(List<Itinerary> itineraries, String airportCode, FilterBy filterBy) {
        AirportStore airportStore = AirportStore.getInstance();
        assert airportStore.isAirport(airportCode);
        Airport airport = airportStore.getAirportObject(airportCode);

        List<Itinerary> filteredItins = new ArrayList<>();

        if (filterBy == FilterBy.ORIGIN) {
            for (Itinerary itinerary : itineraries) {
                if (itinerary.getOrigin().equals(airport)) {
                    filteredItins.add(itinerary);
                }
            }
        } else {
            for (Itinerary itinerary : itineraries) {
                if (itinerary.getDestination().equals(airport)) {
                    filteredItins.add(itinerary);
                }
            }
        }

        return filteredItins;
    }
    /**
     * Creates a new itinerary
     * @param flightNums - correspond to the flight objects to put into the itinerary
     * @return a new itinerary composed of flight objects corresponding to the parameter with the flight numbers
     */
    public Itinerary createItin(List<Integer> flightNums) {
        FlightStore instance = FlightStore.getInstance();
        List<Flight> flightList = new ArrayList<>();
        for(int i = 0; i < flightNums.size(); i++) {
            flightList.add(instance.getFlight(flightNums.get(i)));
        }
        Itinerary returnItinerary = new Itinerary(flightList);
        return returnItinerary;
    }
}
