/*
 * File:   UndoableCommand.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response.commands;

/**
 * Interface defining functionality for a command which is undoable and redoable.
 * Commands which make a state change to the system are good candidates to be undoable and redoable.
 */
public interface UndoableCommand extends Command {

    /**
     * Did this command run without error on last execution.
     *
     * @return true if the command ran without error, false if any error occurred
     */
    boolean wasSuccessful();

    /**
     * Get the ID of the client which made this request.
     *
     * @return client ID number
     */
    int getClientID();

    /**
     * Undo this command.
     *
     * @return output to be directed to the client
     */
    String undo();

    /**
     * Redo this command.
     *
     * @return output to be directed to the client
     */
    String redo();

}
