package control.request_response.commands;

/**
 * The redo command allows UndoManager to know to redo a command.
 */
public class Redo implements Command {

    private boolean validRedo;

    private int clientID;

    /**
     * Create the redo command.
     *
     * @param request the request parameters, null if no params
     * @param clientID IDF number of client which made request
     */
    public Redo(String request, int clientID) {
        this.clientID = clientID;
        this.validRedo = request == null;  // redo command should have no request parameters
    }

    /**
     * Was this redo request valid?
     *
     * @return true if the redo request was valid, false otherwise
     */
    public boolean isValid() {
        return validRedo;
    }

    /**
     * Get the ID of the client which made the redo request.
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
        if (!validRedo) {
            return INVALID_NUMBER_PARAMS;
        }
        return null;  // result of re-done command will be response, so this return is ignored
    }
}
