/**
 * @author: Shawn Struble
 */
package model.data_store;

import model.components.Itinerary;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO: Rewrite delete command
 */
public class ReservationStore {

    public enum Result {
        SUCCESS,
        NOT_FOUND,
        ALREADY_RESERVED
    }

    private static String reservationFilePath;
    public static ReservationStore instance;
    private HashMap<String, List<Itinerary>> reservations = new HashMap<>();

    /**
     * @param filepath file to persist reservations to
     * @throws IOException
     */
    private ReservationStore(String filepath) throws IOException {
        reservationFilePath = filepath;
        readReservations();
        //readReservations();
    }

    public static ReservationStore getInstance(String filepath) throws IOException {
        if (instance == null) {
            instance = new ReservationStore(filepath);
        }
        return instance;
    }

    public static ReservationStore getInstance() {
        if (instance == null) {
            throw new RuntimeException("ReservationStore not properly instantiated!");
        }
        return instance;
    }

    /**
     * adds a reservation for a passenger with an itinerary.
     *
     * @param passengerName
     * @param passItinerary
     * @return an enum confirming or denying a reservation
     */
    public Result reserve(String passengerName, Itinerary passItinerary) {
        /*
            Check to see if passenger already has reservation(s)
            Check to ensure cannot make reservations for same origin/destination combination
         */
        ItineraryStore itineraryStore = ItineraryStore.getInstance();

        if (reservations.containsKey(passengerName)) {
            if (reservations.get(passengerName).contains(passItinerary)) {
                return Result.ALREADY_RESERVED;
            }
            reservations.get(passengerName).add(passItinerary);
            save();
            return Result.SUCCESS;

        }
        /*
            Case for new passenger
            ->Create New Reservation
            ->Create new list as value, and passenger name as key
        */
        List<Itinerary> itineraryList = new ArrayList<>();
        itineraryList.add(passItinerary);
        reservations.put(passengerName, itineraryList);
        save();
        return Result.SUCCESS;
    }

    /**
     * removes a reservation for a passenger's specific itinerary
     * checks to see if passenger has any reservations, then checks if the codes are registered with them
     * removes passenger key from list if they have no itineraries associated with them
     *
     * @param passengerName name of the passenger to delete for
     * @param originCode    the three letter code of the origin airport for the itinerary
     * @param destCode      the three letter code of the destination airport for the itinerary
     * @return null if no itinerary found or the itinerary removed
     */
    public Itinerary delete(String passengerName, String originCode, String destCode) {

        if (!(reservations.containsKey(passengerName))) {
            return null;
        }
        List<Itinerary> possibleItineraries = reservations.get(passengerName);
        for (int i = 0; i < possibleItineraries.size(); i++) {
            String origin = possibleItineraries.get(i).getOrigin().getCode();
            String destination = possibleItineraries.get(i).getDestination().getCode();
            if (originCode.toUpperCase().equals(origin) && destCode.toUpperCase().equals(destination)) { //Found the itinerary
                Itinerary removed = possibleItineraries.remove(i); // Remove the itinerary

                if (reservations.get(passengerName).size() == 0) {  //Remove passenger if they have no itineraries left
                    reservations.remove(passengerName);

                }
                save();
                return removed;
            }
        }
        return null;


    }

    /**
     * Delete reservation based on given itinerary.
     */
    public Result delete(String passengerName, Itinerary itinerary) {
        if (!(reservations.containsKey(passengerName))) {
            return Result.NOT_FOUND;
        } else if (!reservations.get(passengerName).contains(itinerary)) {
            return Result.NOT_FOUND;
        }

        reservations.get(passengerName).remove(itinerary);

        return Result.SUCCESS;
    }


    /**
     * Returns a passenger's itinerary list
     *
     * @param passengerName the name of the person whos itinerary list you want
     * @return list of Itineraries which the passenger had reserved, this will be empty if there were none
     */
    public List<Itinerary> retrieve(String passengerName) {
        if (reservations.containsKey(passengerName)) {
            return reservations.get(passengerName);
        }
        return new ArrayList<>();
    }

    /**
     * Returns a passenger's reservation list.
     *
     * @param passengerName the name of the person whos itinerary list you want
     * @param origin        origin airport, may be empty string (will ignore), must be valid airport
     * @param destination   destination airport, may be empty string (will ignore), must be valid airport
     * @return list of Itineraries which the passenger had reserved
     */
    public List<Itinerary> retrieve(String passengerName, String origin, String destination) {
        List<Itinerary> itineraries = retrieve(passengerName);

        if (!origin.equals("")) {
            itineraries = ItineraryStore.filterItins(itineraries, origin, ItineraryStore.FilterBy.ORIGIN);
        }

        if (!destination.equals("")) {
            itineraries = ItineraryStore.filterItins(itineraries, destination, ItineraryStore.FilterBy.DESTINATION);
        }

        return itineraries;
    }

    /**
     * read reservations from file into memory
     */
    private void readReservations() throws IOException {
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(reservationFilePath));
        try {
            while ((line = br.readLine()) != null) {
                List<Integer> flightIds = new ArrayList<>();
                String[] res = line.split("/");
                String passengerName = res[0];
                for (int i = 1; i < res.length; i++) {
                    String[] flightInts = res[i].split(",");
                    List<Integer> FlightNums = new ArrayList<>();

                    for(int j = 0; j < flightInts.length; j++) {
                        FlightNums.add(Integer.parseInt(flightInts[j]));

                    }
                    ItineraryStore instance = ItineraryStore.getInstance();
                    Itinerary newItinerary = instance.createItin(FlightNums);
                    reserve(res[0],newItinerary);

                }


            }
            br.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     *
     */
    private void save() {
        BufferedReader br = null;
        String[] keys = reservations.keySet().toArray(new String[reservations.size()]);

        try {
            //Set up writing to file
            File file = new File(reservationFilePath);
            FileWriter writer = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(writer);
            //Loop through each key pair
            for (int i = 0; i < reservations.size(); i++) {
                StringBuilder writeString = new StringBuilder();
                List<Itinerary> itineraries = reservations.get(keys[i]);
                writeString.append(keys[i]);

                for (int j = 0; j < itineraries.size(); j++) {
                    writeString.append("/");
                    List<Integer> flightNums = itineraries.get(j).getFlightIntegers();
                    for (int z = 0; z < flightNums.size(); z++) {
                        writeString.append(String.valueOf(flightNums.get(z)) + ",");

                    }
                }
                bw.write(String.valueOf(writeString));
                bw.newLine();

            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

