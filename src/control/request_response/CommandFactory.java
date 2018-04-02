/*
 * File:   CommandFactory.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response;

import control.request_response.commands.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for the creation of Commands. The type of Command created is based on a keyword contained in
 * a client request.
 */
public class CommandFactory {

    /**
     * Mapping of request keywords to their respective Command class.
     */
    static final Map<String, Class> commandMap = new HashMap<String, Class>() {
        {
            put("info", Info.class);
            put("reserve", Reserve.class);
            put("retrieve", Retrieve.class);
            put("delete", Delete.class);
            put("airport", Airport.class);
            put("server", Server.class);
            put("undo", Undo.class);
            put("redo", Redo.class);
        }
    };

    /**
     * Create a command object based on a request's keyword.
     *
     * @param keyword the request keyword, which should exist in the command map. Case does not matter.
     * @param request the full request (not including keyword)
     * @param clientID identification number of the client who sent this request
     * @return the new Command object, null if bad keyword
     */
    public Command createCommand(String keyword, String request, int clientID)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        keyword = keyword.toLowerCase();  // keywords are in map lower case

        if (!commandMap.containsKey(keyword)) {
            return null;
        }

        // use reflection to get the constructor for the command
        Class<?> commandType = commandMap.get(keyword);
        Class<?>[] paramTypes = new Class[] {String.class, int.class};
        Constructor<?> ctor;
        try {
            ctor = commandType.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            System.err.println("Command class constructor not found for keyword: " + keyword);
            throw e;
        }

        // create the new command from the constructor
        Command command;
        try {
             command = (Command)ctor.newInstance(request, clientID);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.err.println("Could not create instance of command for keyword: " + keyword);
            throw e;
        }

        return command;
    }

}
