/**
 * Main front end class for the AFRS system.
 *
 * @author Tom '<3' Amaral
 */


package view.client;

import com.sun.xml.internal.bind.v2.model.core.ID;
import control.request_response.ClientIdentifierManager;
import control.request_response.CommandFactory;
import control.request_response.RequestHandler;
import control.request_response.UndoManager;
import control.request_response.commands.InfoQueryStore;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.data_store.*;

public class FrontEnd extends Application {



    private static RequestHandler requestHandler;

    private static ClientIdentifierManager IDmanager;

    public static void main(String[] args) throws Exception{
         IDmanager = new ClientIdentifierManager();

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
        InfoQueryStore.getInstance(IDmanager);
        UndoManager undoManager = new UndoManager(IDmanager);
        requestHandler = new RequestHandler(new CommandFactory(), IDmanager, undoManager);

        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {

        MainLayoutPane layout = new MainLayoutPane(requestHandler);

        //lights camera action

        primaryStage.setTitle("Airline Flight Reservation System");

        Scene scene = new Scene(layout, 800, 500);

        scene.setFill(Color.rgb(179,0,0, .7));

        primaryStage.setScene(scene);

        primaryStage.setResizable(false);

        primaryStage.show();
    }




}
