/*
 * File:   RequestHandler.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response;

import control.request_response.commands.Command;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * The RequestHandler is the entry point for the AFRS system, receiving and responding to client's text-based API calls.
 */
public class RequestHandler implements ClientIdentifierObserver {

    private static String RESPONSE_FORMAT = "%d,%s";  // CID,response text

    static String PARTIAL_REQUEST_MSG = "partial-request";

    static String INTERNAL_ERROR = "error,internal error occurred";

    static String UNKNOWN_KEYWORD = "error,unknown request";

    private static String BAD_CID = "error,invalid connection";

    private static String CONNECT_RESPONSE = "connect,%d";

    private static String CONNECT_RQST_KW = "connect";

    private static String DISCONNECT_RESPONSE = "%d,disconnect";

    private static String DISCONNECT_KW = "disconnect";

    private CommandFactory commandFactory;

    private Map<Integer, String> incompleteRequestMap;

    private ClientIdentifierManager clientIdentifierManager;

    private UndoManager undoManager;

    /**
     * Create a request handler.
     */
    public RequestHandler(CommandFactory commandFactory, ClientIdentifierManager clientIdentifierManager,
                          UndoManager undoManager) {
        this.commandFactory = commandFactory;
        this.incompleteRequestMap = new HashMap<>();
        this.clientIdentifierManager = clientIdentifierManager;
        this.undoManager = undoManager;
    }

    /**
     * Make a request to the AFRS system.
     *
     * @param request the request, which will only be fully processed once closed with a semi-colin
     * @return response to the request
     */
    public String makeRequest(String request) {
        if (!request.contains(";")) {  // complete requests end in a semi-colin
            int cid;
            try {
                String[] cidSplit = request.split(",",2);  // breaks into [CID, partial-request]
                cid = Integer.parseInt(cidSplit[0]);  // CID should be first item in CSV

                if (clientIdentifierManager.isActiveClient(cid)) {
                    incompleteRequestMap.put(cid, incompleteRequestMap.get(cid) + cidSplit[1]);
                    return PARTIAL_REQUEST_MSG;
                }
            } catch (NumberFormatException e) {  // NAN
                return BAD_CID;
            }

            return BAD_CID;
        }

        request = request.replace(";", "");  // we don't care about the ending semi-colin

        if (request.trim().equalsIgnoreCase(CONNECT_RQST_KW)) {  // new client
            int id = clientIdentifierManager.registerNewClient();
            incompleteRequestMap.put(id, "");
            return String.format(CONNECT_RESPONSE, id);
        }

        int cid;
        String[] cidSplit;
        try {
            cidSplit = request.split(",",2);  // breaks into [CID, partial-request]
            cid = Integer.parseInt(cidSplit[0]);  // CID should be first item in CSV

            if (!clientIdentifierManager.isActiveClient(cid)) {
                return BAD_CID;
            }
        } catch (NumberFormatException e) {  // NAN
            return BAD_CID;
        }

        request = incompleteRequestMap.get(cid) + cidSplit[1];

        // reset incomplete for the client
        incompleteRequestMap.remove(cid);
        incompleteRequestMap.put(cid, "");

        return processCompleteRequest(cid, request);
    }

    /**
     * Process a complete request.
     *
     * @param CID the client identification number of the client which sent the request
     * @param request a full request (not necessarily valid)
     * @return the response for the client
     */
    private String processCompleteRequest(int CID, String request) {
        String[] kwParamsSplit = request.split(",", 2);  // splits into [kw, params]
        String keyword = kwParamsSplit[0];

        if (kwParamsSplit.length > 1) {
            request = kwParamsSplit[1];  // commands want only the request parameters
        } else {
            request = null;  // null => no parameters
        }

        if (keyword.equalsIgnoreCase(DISCONNECT_KW) && request == null) {
            return handleDisconnectRequest(CID);
        }

        Command command;
        try {
            command = commandFactory.createCommand(keyword, request, CID);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            // these errors come from the reflection code within command factory
            e.printStackTrace();
            System.err.println("If a new command was added, check that it follows the same constructor constraints as" +
                    "other command classes. Failed with keyword: " + keyword);
            return INTERNAL_ERROR;
        }

        if (command == null) {  // command == null => keyword is unknown by the command factory
            return UNKNOWN_KEYWORD;
        }

        try {
            String response = undoManager.executeCommand(command);
            return String.format(RESPONSE_FORMAT, CID, response);
        } catch (Exception e) {  // catch any error that made it all the way out so that the system does not stop
            e.printStackTrace(System.err);
            return INTERNAL_ERROR;
        }
    }

    /**
     * Handle a request to disconnect.
     *
     * @param CID client identification number of the client wishing to disconnect
     */
    private String handleDisconnectRequest(int CID) {
        clientIdentifierManager.releaseClient(CID);
        return String.format(DISCONNECT_RESPONSE, CID);
    }

    /**
     * Notify observer that a new client identification number has been assigned.
     *
     * @param clientID the new client identification number
     */
    @Override
    public void notifyNewClient(int clientID) {
        incompleteRequestMap.put(clientID, "");
    }

    /**
     * Notify observer that a client has disconnected.
     *
     * @param clientID the identification number of the client that disconnected
     */
    @Override
    public void notifyClientDisconnect(int clientID) {
        incompleteRequestMap.remove(clientID);
    }
}
