/**
 * Customized Tab node that contains a premade 'Help' dialog
 * to be added to the main TabPane
 *
 * @author Tom '<3' Amaral
 * March 29th, 2018
 */


package view.client;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class HelpTab extends Tab {

    public HelpTab(){
        this.setText("Welcome");
        this.setClosable(false);



        Text helpText = new Text();

        StringBuffer hText = new StringBuffer();

        hText.append("Thank you for using Treetop Airway's AFRS!" + System.getProperty("line.separator"));
        hText.append("To get started, add a new Client tab with the button above." + System.getProperty("line.separator") + System.getProperty("line.separator"));
        hText.append("The available commands are as follows: " + System.getProperty("line.separator"));
        hText.append("info, reserve, retrieve, delete, airport, undo, redo, and server." + System.getProperty("line.separator") + System.getProperty("line.separator"));

        helpText.setText(hText.toString());

        HBox box = new HBox(helpText);

        box.setPadding(new Insets(10));

        box.setStyle("-fx-background-color: white");

        this.setContent(box);

    }

}
