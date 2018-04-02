import control.request_response.ClientIdentifierManager;
import control.request_response.CommandFactory;
import control.request_response.RequestHandler;
import control.request_response.UndoManager;
import control.request_response.commands.InfoQueryStore;
import model.data_store.*;
import view.client.CLIClient;

/**
 * Entry point for the AFRS PTUI. Starts up the system and gives the API request handler to the client, then launches
 * the client PTUI.
 */
public class RunPTUI {

    /**
     * Command line entry point.
     *
     * @param args airport file path, weather file path, connections file path, delays file path, flights file path,
     *             reservations file path
     */
    public static void main(String[] args) throws Exception {
        ClientIdentifierManager IDmanager = new ClientIdentifierManager();

        try {
            AirportStore.getInstance(args[0], args[1], args[2], args[3], IDmanager);
            FlightStore.getInstance(args[4]);
            ReservationStore.getInstance(args[5]);
            ItineraryStore.getInstance();
        } catch (IndexOutOfBoundsException e) {
            System.err.println("INVALID ARGUMENTS! Required: airport file path, weather file path, connections file " +
                    "path, delays file path, flights file path, reservations file path");
            System.exit(1);
        }

        ServerStore.getInstance(IDmanager);
        UndoManager undoManager = new UndoManager(IDmanager);
        RequestHandler requestHandler = new RequestHandler(new CommandFactory(), IDmanager, undoManager);
        InfoQueryStore.getInstance(IDmanager);
        CLIClient CLIClient = new CLIClient(requestHandler);
        CLIClient.run();
    }

}
