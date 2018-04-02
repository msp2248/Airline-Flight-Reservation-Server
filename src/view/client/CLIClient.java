/**
 * CLIClient class. Handles the user input and output interactions,
 * and composes the PTUI.
 * Takes input from console, passes it off to the RequestHandler,
 * and outputs the returned string. Continues until terminated by user.
 *
 * @author Tom Amaral
 */

package view.client;

import control.request_response.RequestHandler;

import java.util.Scanner;

public class CLIClient {


    private final RequestHandler handler;

    public CLIClient(RequestHandler rh){
        handler = rh;
    }


    /**
     * Main outer loop for the AFRS system. Continues to take input from the user
     * until the keyword 'quit' is given. Also outputs a help screen when requested.
     */
    public void run(){

        header();

        String input;

        Scanner in = new Scanner(System.in);

        while(true) {
            System.out.print(">>:");

            input = in.nextLine();

            if(input.equals("quit")){
                System.out.println("Thank you for using AFRS. Have a nice day!");
                break;
            }
            else if(input.equals("help")){
                help();
                continue;
            }

            System.out.println(handler.makeRequest(input));


            System.out.println();
        }
    }


    private void header(){
        System.out.println("---------------------------------------------");
        System.out.println("-----         Tree Top Airways          -----");
        System.out.println("----- Airline Flight Reservation Server -----");
        System.out.println("---------------------------------------------");
        System.out.println();
        System.out.println("Thank you for using TTA's AFRS.");
        System.out.println();
        System.out.println("Please enter a command.");
        System.out.println("'help' for a list, 'quit' to terminate.");
        System.out.println();
    }

    private void help(){
        System.out.println("---------------------------------------------");
        System.out.println("--- AFRS r1 Strategic Slackers March 2018 ---");
        System.out.println("---------------------------------------------");
        System.out.println("Commands:");
        System.out.println("info, reserve, retrieve, delete, airport");
        System.out.println("format: command,paramter(s);");
        System.out.println("Please separate all command parameters with a comma,");
        System.out.println("and terminate commands with a semi-colon.");
        System.out.println("Parameters in brackets are optional.");
        System.out.println();
        System.out.println("info,origin,destination[,connections[,sort-order]];");
        System.out.println();
        System.out.println("reserve,id,passenger;");
        System.out.println();
        System.out.println("retrieve,passenger,[,origin[,destination]];");
        System.out.println();
        System.out.println("delete,passenger,origin,destination;");
        System.out.println();
        System.out.println("airport,airport");
        System.out.println("---------------------------------------------");
    }


}
