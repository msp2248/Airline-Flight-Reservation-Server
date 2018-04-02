/*
 * File:   InfoQueryStore.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response.commands;

import control.request_response.ClientIdentifierObserver;
import control.request_response.ClientIdentifierSubject;
import model.components.Itinerary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the most recent set of itineraries given from an Info command. This set is used by the Reserve command.
 * Reservations are made based on an itinerary ID given during output. The ID is the position within the set of
 * itineraries starting from 1.
 *
 * This is a singleton.
 */
public class InfoQueryStore implements ClientIdentifierObserver {

    private static InfoQueryStore instance;

    private Map<Integer, List<Itinerary>> latestGivenItins;

    private InfoQueryStore(ClientIdentifierSubject clientIDManager) {
        latestGivenItins = new HashMap<>();
        instance = this;
        clientIDManager.attachObserver(this);  // observe the ID manager
    }

    /**
     * Get the singleton instance of the InfoQueryStore. T
     *
     * @return instance of the store
     */
    public static InfoQueryStore getInstance(ClientIdentifierSubject clientIDManager) {
        if (instance == null) {
            return new InfoQueryStore(clientIDManager);
        }
        return instance;
    }

    /**
     * Get the singleton instance of the InfoQueryStore.
     *
     * @return instance of the store
     */
    public static InfoQueryStore getInstance() {
        if (instance == null) {
            throw new RuntimeException("InfoQueryStore was not properly instantiated before a call to getInstance");
        }
        return instance;
    }

    /**
     * Set the most recent list of Itineraries given to a client in the Info command.
     *
     * @param clientID the client identification number to set given itineraries for
     * @param itineraries list of Itineraries given to client
     */
    void setItineraries(int clientID, List<Itinerary> itineraries) {
        latestGivenItins.put(clientID, itineraries);
    }

    /**
     * Get an itinerary from its position in the output.
     *
     * @param clientID the client identification number to get the itinerary for
     * @param itineraryID ID of the itinerary to get (position in output)
     * @return Itinerary object for that ID, or null if the ID is invalid
     */
    Itinerary getItinerary(int clientID, int itineraryID) {
        if (isValidID(clientID, itineraryID)) {
            return latestGivenItins.get(clientID).get(itineraryID - 1);
        }
        return null;
    }

    /**
     * Check if the ID for an itinerary is valid based on the last given list of Itineraries. (checks if in bounds)
     *
     * @param clientID the client identification number to check the itinerary ID for
     * @param itineraryID ID of the itinerary (position in output)
     * @return true if ID is valid, false otherwise
     */
    boolean isValidID(int clientID, int itineraryID) {
        return latestGivenItins.containsKey(clientID) &&
                (itineraryID >= 1) && (itineraryID <= latestGivenItins.get(clientID).size());
    }

    /**
     * Notify observer that a new client identification number has been assigned.
     *
     * @param clientID the new client identification number
     */
    @Override
    public void notifyNewClient(int clientID) {
        latestGivenItins.put(clientID, new ArrayList<>());
    }

    /**
     * Notify observer that a client has disconnected.
     *
     * @param clientID the identification number of the client that disconnected
     */
    @Override
    public void notifyClientDisconnect(int clientID) {
        latestGivenItins.remove(clientID);
    }
}
