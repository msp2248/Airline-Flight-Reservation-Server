/**
 * Airport class
 * @author Meet Patel
 */
package model.components;

import control.request_response.ClientIdentifierObserver;
import control.request_response.ClientIdentifierSubject;

import java.util.*;

public class Airport implements ClientIdentifierObserver {


    String name;
    String code;
    List<String> weather;
    int delayTime;
    int connectionTime;

    Map<Integer, Iterator<String>> clientWeather;

    public Airport (ClientIdentifierSubject clientIDManager) {
        this.clientWeather = new HashMap<>();
        clientIDManager.attachObserver(this);
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get the current weather at this airport.
     *
     * @return weather in the format "condition,temperature"
     */
    public String getWeather(int CID) {
        if (!(clientWeather.containsKey(CID) && clientWeather.get(CID).hasNext())) {
            clientWeather.put(CID, weather.iterator());
        }
        return clientWeather.get(CID).next();
    }

    public int getDelayTime() {
        return delayTime;
    }

    public int getConnectionTime() {
        return connectionTime;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setWeather(List<String> weather) {
        this.weather = weather;
    }
    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
    public void setConnectionTime(int connectionTime) {
        this.connectionTime = connectionTime;
    }



    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Notify observer that a new client identification number has been assigned.
     *
     * @param clientID the new client identification number
     */
    @Override
    public void notifyNewClient(int clientID) {
        // do nothing... putting every client and new weather iterator into the map at each airport is a waste of memory
        // in this case memory cost > time savings
    }

    /**
     * Notify observer that a client has disconnected.
     *
     * @param clientID the identification number of the client that disconnected
     */
    @Override
    public void notifyClientDisconnect(int clientID) {
        clientWeather.remove(clientID);
    }
}
