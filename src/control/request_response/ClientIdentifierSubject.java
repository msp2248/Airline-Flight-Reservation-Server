/*
 * File:   ClientIdentifierSubject.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response;

/**
 * Interface defining the methods to attach a ClientIdentifierObserver to the ClientIdentifierManager.
 */
public interface ClientIdentifierSubject {

    /**
     * Attach a new observer to this subject.
     *
     * @param observer the observer to attach
     */
    void attachObserver(ClientIdentifierObserver observer);

}
