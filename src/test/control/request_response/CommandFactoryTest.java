package control.request_response;

import control.request_response.commands.Command;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class CommandFactoryTest {

    private CommandFactory CuT;

    @Before
    public void setUp() throws Exception {
        CuT = new CommandFactory();
    }

    /**
     * Test creating every type of command defined in the CommandFactory commandMap.
     */
    @Test
    public void testCreateAllCommands() throws Exception {
        Set<String> keyWords = CommandFactory.commandMap.keySet();

        for (String kw : keyWords) {
            Command command = CuT.createCommand(kw, "", 0);
            assertNotNull(command);
        }
    }

    /**
     * Test command creation with an invalid keyword.
     */
    @Test
    public void testCreateCommandInvalidKey() throws Exception {
        String notAKey = "NotACommandKeyWord";
        Command command = CuT.createCommand(notAKey, "", 0);

        assertNull(command);
    }

}