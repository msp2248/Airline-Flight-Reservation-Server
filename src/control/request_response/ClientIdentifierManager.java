/*
 * File:   ClientIdentifierManager.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Provides and keeps track of client identification numbers.
 */
public class ClientIdentifierManager implements ClientIdentifierSubject {

    private List<ClientIdentifierObserver> observers = new ArrayList<>();

    private TreeSet<Integer> activeIDs = new TreeSet<>();

    private TreeSet<Integer> availableIDs = new TreeSet<>();

    private static int MAX_EXTRA_IDS = 10;

    /**
     * Register a new client. A unique client identification number will be provided for use until the client
     * disconnects.
     *
     * @return unique client ID to represent the new client
     */
    public int registerNewClient() {
        int id;

        if (availableIDs.isEmpty()) {
            if (!activeIDs.isEmpty()) {
                id = activeIDs.last() + 1;
            } else {
                id = 1;  // => lowest and first ID is 1
            }
        } else {
            id = availableIDs.pollFirst();  // get the lowest available ID
        }

        activeIDs.add(id);

        notifyObserversNewID(id);

        return id;
    }

    /**
     * Release the identification number associated with a client. This should be done when a client disconnects from the
     * system.
     *
     * @param clientID the client ID number to be released
     */
    public void releaseClient(int clientID) {
        if (activeIDs.contains(clientID)) {
            activeIDs.remove(clientID);
            availableIDs.add(clientID);
            notifyObserversReleasedID(clientID);
        }

        while (availableIDs.size() > MAX_EXTRA_IDS) {  // control growth of IDs
            availableIDs.pollLast();
        }
    }

    /**
     * Check if a given client ID is in use by a client.
     *
     * @param clientID client identification number to check
     * @return true if the client ID is in use, false if not
     */
    public boolean isActiveClient(int clientID) {
        return activeIDs.contains(clientID);
    }

    /**
     * Attach a new observer to this subject.
     *
     * @param observer the observer to attach
     */
    @Override
    public void attachObserver(ClientIdentifierObserver observer) {
        observers.add(observer);
    }

    /**
     * Notify all observers of a new client ID being registered.
     *
     * @param newID the new client ID number
     */
    private void notifyObserversNewID(int newID) {
        for (ClientIdentifierObserver observer: observers) {
            observer.notifyNewClient(newID);
        }
    }

    /**
     * Notify all observers that a client ID was released.
     *
     * @param releasedID the released ID number
     */
    private void notifyObserversReleasedID(int releasedID) {
        for (ClientIdentifierObserver observer: observers) {
            observer.notifyClientDisconnect(releasedID);
        }
    }

}
