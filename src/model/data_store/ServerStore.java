package model.data_store;

import control.request_response.ClientIdentifierObserver;
import control.request_response.ClientIdentifierSubject;

import java.util.HashMap;


public class ServerStore implements ClientIdentifierObserver {

    private static ServerStore instance = null;

    public enum Server {
        local,faa
    }
    private HashMap<Integer,Server> clientServer = new HashMap<>();
    private ServerStore(ClientIdentifierSubject IDmanager) {
        IDmanager.attachObserver(this);
    }

    public static ServerStore getInstance(ClientIdentifierSubject IDmanager) {
        if(instance == null) {
            instance = new ServerStore(IDmanager);
        }
        return instance;
    }

    public static ServerStore getInstance() {
        if(instance == null) {
            throw new RuntimeException("Server store not properly instantiated");
        }
        return instance;
    }

    public void changeServer(int id,Server newServer) {
        if(clientServer.containsKey(id)) {
            clientServer.put(id,newServer);
        }
    }

    public ServerStore.Server getServer(int id) {
        if(clientServer.containsKey(id)) {
            return clientServer.get(id);
        }
        return null;
    }

    /**
     * Notify observer that a new client identification number has been assigned.
     *
     * @param clientID the new client identification number
     */
    @Override
    public void notifyNewClient(int clientID) {
        clientServer.put(clientID, Server.local);
    }

    /**
     * Notify observer that a client has disconnected.
     *
     * @param clientID the identification number of the client that disconnected
     */
    @Override
    public void notifyClientDisconnect(int clientID) {
        if(clientServer.containsKey(clientID)) {
            clientServer.remove(clientID);
        }
    }
}
