package control.request_response;

import model.data_store.AirportStore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestHandlerTest {

    private RequestHandler CuT;

    @Before
    public void setUp() throws Exception {
        ClientIdentifierManager clientIdentifierManager = new ClientIdentifierManager();
        CuT = new RequestHandler(new CommandFactory(), clientIdentifierManager, new UndoManager(clientIdentifierManager));  // should be mocks
        AirportStore.getInstance("airports.txt", "weather.txt",
                "connections.txt", "delays.txt", clientIdentifierManager);
    }

    @Test
    public void testPartialRequest() throws Exception {
        String incompleteRequest = "airport,";
        String incompleteRequestPt2 = "EEE";

        String result = CuT.makeRequest(incompleteRequest);
        assertEquals(RequestHandler.PARTIAL_REQUEST_MSG, result);

        result = CuT.makeRequest(incompleteRequestPt2);
        assertEquals(RequestHandler.PARTIAL_REQUEST_MSG, result);

        // finalize request, should get some message that is not the partial request message
        result = CuT.makeRequest(";");
        assertNotEquals(RequestHandler.PARTIAL_REQUEST_MSG, result);
    }

    @Test
    public void testInvalidKeyword() throws Exception {
        String notAKeyword = "thisisandwillneverbeakeyword";
        String request = notAKeyword + ",hi;";

        String result = CuT.makeRequest(request);

        assertEquals(RequestHandler.UNKNOWN_KEYWORD, result);
    }

}