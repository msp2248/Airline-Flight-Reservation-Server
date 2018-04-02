package control.request_response;

import control.request_response.commands.Command;
import control.request_response.commands.Redo;
import control.request_response.commands.Undo;
import control.request_response.commands.UndoableCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * UndoManager is responsible for storing all UndoableCommands which were successfully executed.
 * All commands come through the UndoManager, which executes them and returns the response to the RequestHandler for
 * client output.
 */
public class UndoManager implements ClientIdentifierObserver {

    private static String NONE_TO_UNDO = "error,no request available";

    private Map<Integer, Stack<UndoableCommand>> undoMap;

    private Map<Integer, Stack<UndoableCommand>> redoMap;

    /**
     * Create an UndoManager. There should only be one in the system.
     *
     * @param IDmanager the client ID manager
     */
    public UndoManager(ClientIdentifierSubject IDmanager) {
        IDmanager.attachObserver(this);
        this.undoMap = new HashMap<>();
        this.redoMap = new HashMap<>();
    }

    /**
     * Execute a command and store it for undo/redo is a working UndoableCommand.
     *
     * @param command the command to execute
     * @return output of the command to be returned to the client
     */
    public String executeCommand(Command command) {
        String response = command.execute();

        if (command instanceof Undo && ((Undo) command).isValid()) {  // valid undo given, undo last for client
            int clientID = ((Undo) command).getClientID();

            if (undoMap.get(clientID).isEmpty()) {
                response = NONE_TO_UNDO;
            } else {
                response = undoLast(clientID);
            }
        } else if (command instanceof Redo && ((Redo) command).isValid()) {  // valid redo given, redo last
            int clientID = ((Redo) command).getClientID();

            if (redoMap.get(clientID).isEmpty()) {
                response = NONE_TO_UNDO;
            } else {
                response = redoLast(clientID);
            }
        }

        // this command should be stored if it is an UndoableCommand and executed successfully
        if (command instanceof UndoableCommand && ((UndoableCommand) command).wasSuccessful()) {
            UndoableCommand undoableCommand = (UndoableCommand) command;
            int clientID = undoableCommand.getClientID();
            undoMap.get(clientID).push(undoableCommand);
        }

        return response;
    }

    private String undoLast(int clientID) {
        UndoableCommand toUndo = undoMap.get(clientID).pop();
        String response = toUndo.undo();
        if (toUndo.wasSuccessful()) {  // only keep if successful
            redoMap.get(clientID).push(toUndo);
        }
        return response;
    }

    private String redoLast(int clientID) {
        UndoableCommand toRedo = redoMap.get(clientID).pop();
        String response = toRedo.redo();
        if (toRedo.wasSuccessful()) {  // only keep if successful
            undoMap.get(clientID).push(toRedo);
        }
        return response;
    }

    /**
     * Notify observer that a new client identification number has been assigned.
     *
     * @param clientID the new client identification number
     */
    @Override
    public void notifyNewClient(int clientID) {
        undoMap.put(clientID, new Stack<>());
        redoMap.put(clientID, new Stack<>());
    }

    /**
     * Notify observer that a client has disconnected.
     *
     * @param clientID the identification number of the client that disconnected
     */
    @Override
    public void notifyClientDisconnect(int clientID) {
        undoMap.remove(clientID);
        undoMap.remove(clientID);
    }
}
