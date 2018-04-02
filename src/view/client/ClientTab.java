package view.client;


import control.request_response.RequestHandler;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;


/**
 * Customized Tab component. Has all the components needed for a client
 * tab interface preloaded into it's children.
 *
 *
 * @author Tom '<3' Amaral
 * March 28th, 2017
 */

public class ClientTab extends javafx.scene.control.Tab {

    private int clientId;

    private BorderPane grid;

    private TextField inputField;

    private Button submit;

    private TextArea outputArea;

    private String requestString;

    private RequestHandler requestHandler;


    public ClientTab(RequestHandler requestHandler){

        String cid = requestHandler.makeRequest("connect;");


        this.clientId = Integer.parseInt(cid.substring(8));


        this.requestHandler = requestHandler;

        this.setText("Client " + clientId);

        setup();

        this.setContent(grid);

        //notify request handler when the tab is closed
        this.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                requestHandler.makeRequest(clientId+",disconnect;");
            }
        });

    }


    private void setup(){

        //initialize base nodes

        inputField = new TextField();

        submit = new Button();

        outputArea = new TextArea();

        grid = new BorderPane();

        //customize base nodes

        inputField.setPrefColumnCount(30);

        inputField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER)){
                    requestString = inputField.getText();

                    String response = requestHandler.makeRequest(clientId+","+requestString);

                    updateOutput(response);

                    inputField.clear();
                }
            }
        });

        submit.setText("Submit");

        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                requestString = inputField.getText();

                String response = requestHandler.makeRequest(clientId+","+requestString);

                updateOutput(response);

                inputField.clear();
            }
        });

        outputArea.setEditable(false);
        outputArea.setMinWidth(740);

        //add base nodes to respective containers

        HBox requestBox = new HBox(inputField, submit);
        HBox output = new HBox(outputArea);

        //customize containers

        requestBox.setSpacing(20);
        requestBox.setPadding(new Insets(10));

        output.setPadding(new Insets(10));

        output.setMaxHeight(350);
        output.setMinHeight(350);
        output.setMaxWidth(700);
        output.setMinWidth(700);

        grid.setPadding(new Insets(10,10,10,10));
        grid.setStyle("-fx-background-color: white");
        grid.setTop(requestBox);
        grid.setBottom(output);


    }


    public void updateOutput(String output){

        outputArea.appendText(output + System.getProperty("line.separator") + System.getProperty("line.separator"));

    }



}
