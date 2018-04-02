/*
 * File:   Command.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response.commands;

public interface Command {

    String INVALID_NUMBER_PARAMS = "error,invalid number of parameters";

    String UNKNOWN_ORIG = "error,unknown origin";

    String UNKNOWN_DEST = "error,unknown destination";

    /**
     * Complete this command's operation.
     *
     * @return the response to be given back to the client
     */
    String execute();

}
