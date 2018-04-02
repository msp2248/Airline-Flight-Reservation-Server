package control.request_response.commands;

import model.data_store.ServerStore;

public class Server implements Command {
    private int cid;
    private String request;
    private String server;
    private String local = "local";
    private String faa = "faa";
    private String UNKNOWN_SERVER = "error,unknown request";
    public Server(String request, int clientID) {
        this.request = request;
        this.cid = clientID;
    }
    @Override
    public String execute() {
        String parseResult = parseRequest();
        ServerStore instance = ServerStore.getInstance();
        if(parseResult != null) {
            return parseResult;
        }
        if(server.toLowerCase().equals(local)) {

            instance.changeServer(cid, ServerStore.Server.local);
            //control.request_response.commands.Airport.setServer(control.request_response.commands.Airport.Server.LOCAL);
            return String.format("changed server to " + local);

        }
        if(server.toLowerCase().equals(faa)) {
            instance.changeServer(cid, ServerStore.Server.faa);
            //control.request_response.commands.Airport.setServer(control.request_response.commands.Airport.Server.FAA);
            return String.format("changed server to " + faa);
        }

        return null;
    }
    private String parseRequest() {
        if(request == null || request.split(",").length > 1) {
            return INVALID_NUMBER_PARAMS;
        }

        server = request;

        if(!((server.toLowerCase().equals(local)) || ((server.toLowerCase().equals(faa))))) {
            return UNKNOWN_SERVER;
        }

        return null;


    }

}
