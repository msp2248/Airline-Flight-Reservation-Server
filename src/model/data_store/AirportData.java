package model.data_store;



public interface AirportData {
    String getAirportName(String code);
    String getAirportWeather(int clientID, String code);
    String getAirportDelay(String code);
}

