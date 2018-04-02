/**
 * Precustomized BorderPane representing the top level of the nodes
 * of the main scene. Maintains the latest count of clients.
 * @author Tom '<3' Amaral
 */

package view.client;

import control.request_response.RequestHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class MainLayoutPane extends BorderPane {



    public MainLayoutPane(RequestHandler requestHandler){


        TabPane clientTabs = new TabPane();

        Button newClient = new Button("New Client");

        clientTabs.setMinWidth(780);
        clientTabs.setMaxWidth(780);
        clientTabs.setPadding(new Insets(10,0,0,0));
        clientTabs.setStyle("-fx-alignment: center");
        clientTabs.getTabs().add(new HelpTab());

        newClient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ClientTab clientTab = new ClientTab(requestHandler);

                clientTabs.getTabs().add(clientTab);
            }
        });

        this.setTop(newClient);
        this.setCenter(clientTabs);

        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: grey");

    }



}
