/*
 * File:   ClientIdentifierManagerTest.java
 * Author: Adam Del Rosso
 * Email:  avd5772@rit.edu
 * GitHub: AdamVD
 */
package control.request_response;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientIdentifierManagerTest {

    private ClientIdentifierManager CuT;

    @Before
    public void setUp() throws Exception {
        CuT = new ClientIdentifierManager();
    }

    /**
     * Test registering a client to receive a unique client ID.
     */
    @Test
    public void testClientRegistration() {
        int client1 = CuT.registerNewClient();
        int client2 = CuT.registerNewClient();

        assertNotEquals("clients do not have unique IDs!", client1, client2);
    }

    @Test
    public void testIsActiveClient() {
        int client1 = CuT.registerNewClient();
        int notAClient = Integer.MIN_VALUE;

        assertTrue(CuT.isActiveClient(client1));
        assertFalse(CuT.isActiveClient(notAClient));
    }

    @Test
    public void testReleaseClientID() {
        int client1 = CuT.registerNewClient();
        assertTrue("client registration not working", CuT.isActiveClient(client1));

        CuT.releaseClient(client1);

        assertFalse(CuT.isActiveClient(client1));
    }

    @Test
    public void stressIDGenerator() {
        int numGet1 = 15;
        int[] activeIds = new int[numGet1];

        for (int i=0; i<numGet1; i++) {
            activeIds[i] = CuT.registerNewClient();
        }

        for (int i=0; i<numGet1; i++) {
            for (int k=i+1; k<numGet1; k++) {
                assertNotEquals("duplicate IDS", activeIds[i], activeIds[k]);
            }
        }

        int numToPutBack = 5;

        for (int i=numGet1-1; i>=numGet1-numToPutBack; i--) {
            CuT.releaseClient(activeIds[i]);
        }

        for (int i=numGet1-numToPutBack; i<numGet1; i++) {
            activeIds[i] = CuT.registerNewClient();
        }

        for (int i=0; i<numGet1; i++) {
            for (int k=i+1; k<numGet1; k++) {
                assertNotEquals("duplicate IDS", activeIds[i], activeIds[k]);
            }
        }
    }

}