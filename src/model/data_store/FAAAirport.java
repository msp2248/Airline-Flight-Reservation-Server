package model.data_store;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.xml.ws.ProtocolException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FAAAirport implements AirportData {
    private static FAAAirport instance = null;
    private static AirportStore localInstance = null;
    private JsonParser parser;
    private String urlPreface = "https://soa.smext.faa.gov/asws/api/airport/status/";
    public static FAAAirport getInstance() {
        if(instance == null) {
            instance = new FAAAirport();
        }
        return instance;
    }
    private FAAAirport() {
        localInstance = AirportStore.getInstance();
       parser = new JsonParser();
    }
    @Override
    public String getAirportName(String code) {

        return localInstance.getAirportName(code);
     }

    @Override
    public String getAirportWeather(int ClientID, String code) {
        try {
            String response = responseGen(code);

            JsonObject rootObj = parser.parse(response).getAsJsonObject();

            JsonObject weather1 = rootObj.getAsJsonObject("Weather");
            JsonArray weather2 = weather1.getAsJsonArray("Weather");

            JsonObject temperatureObj = weather2.get(0).getAsJsonObject();



            String actualTemp = weather1.get("Temp").getAsString();


            String tempDesc = temperatureObj.get("Temp").getAsString();

            String returnString = tempDesc + "," + actualTemp;

            return returnString;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getAirportDelay(String code) {
        try {
            String response = responseGen(code);
            JsonObject rootObj = parser.parse(response).getAsJsonObject();

            JsonArray status = rootObj.get("Status").getAsJsonArray();

            String avgDelay = "0";
            JsonObject delay = status.get(0).getAsJsonObject();

            if(delay.has("Reason") == false) {
                if(delay.has("AvgDelay" )) {
                    avgDelay = delay.get("AvgDelay").getAsString();
                }
                //Just In Case
                else if (delay.has("avgDelay" )) {
                    avgDelay = delay.get("avgDelay").getAsString();
                }
                else if(delay.has("MaxDelay")){
                    avgDelay = delay.get("MaxDelay").getAsString();
                }
                else if(delay.has("MinDelay")){
                    avgDelay = delay.get("MinDelay").getAsString();
                }
                else {
                    avgDelay = "Error,FAA found no delay time";
                }
            }
            return avgDelay;


        } catch(Exception e){
            e.printStackTrace();

        }
        return "0";
    }
    private String responseGen(String airport) throws Exception{
        String url;

        StringBuilder response = new StringBuilder();


        try {        // Create a URL and open a connection
            url = urlPreface + airport;
            URL FAAURL = new URL(url);
            HttpURLConnection urlConnection =
                    (HttpURLConnection) FAAURL.openConnection();

            // Set the paramters for the HTTP Request
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Accept", "application/json");



            // Create an input stream and wrap in a BufferedReader to read the
            // response.
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }



            // MAKE SURE TO CLOSE YOUR CONNECTION!
            in.close();



        }
        catch (FileNotFoundException e) {
            System.out.print("URL not found: ");
            System.out.println(e.getMessage());
        }
        catch (MalformedURLException e) {
            System.out.print("Malformed URL: ");
            System.out.println(e.getMessage());
        }
        catch (ProtocolException e) {
            System.out.print("Unsupported protocol: ");
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return response.toString();
    }
}
