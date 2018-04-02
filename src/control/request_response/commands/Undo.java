package control.request_response.commands;

/**
 * The undo command allows UndoManager to know to undo a command.
 */
public class Undo implements Command {

    private boolean validUndo;

    private int clientID;

    /**
     * Create the undo command.
     *
     * @param request the request parameters, null if no params
     * @param clientID ID number of client which made request
     */
    public Undo(String request, int clientID) {
        this.clientID = clientID;
        this.validUndo = request == null;  // undo command should have no request parameters
    }

    /**
     * Was this undo request valid?
     *
     * @return true if the undo request was valid, false if not
     */
    public boolean isValid() {
        return validUndo;
    }

    /**
     * Get the ID of the client which made the undo request.
     *
     * @return client ID number
     */
    public int getClientID() {
        return clientID;
    }

    /**
     * Complete this command's operation.
     *
     * @return the response to be given back to the client
     */
    @Override
    public String execute() {
        if (!validUndo) {
            return INVALID_NUMBER_PARAMS;
        }
        return null;  // result of un-done command will be response, so this return is ignored
    }
}
