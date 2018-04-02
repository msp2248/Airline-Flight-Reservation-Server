/*
 * File:   ClientIdentifierObserver.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response;

/**
 * Interface defining methods required by observers of the ClientIDAssigner.
 */
public interface ClientIdentifierObserver {

    /**
     * Notify observer that a new client identification number has been assigned.
     *
     * @param clientID the new client identification number
     */
    void notifyNewClient(int clientID);

    /**
     * Notify observer that a client has disconnected.
     *
     * @param clientID the identification number of the client that disconnected
     */
    void notifyClientDisconnect(int clientID);

}
