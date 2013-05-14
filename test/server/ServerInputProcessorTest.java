package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import testutil.SocketMock;

public class ServerInputProcessorTest {
    
    @Test
    public void testGetId() throws InterruptedException {
        ServerInputProcessor sip = new ServerInputProcessor();
        ByteArrayInputStream is = new ByteArrayInputStream(
                "handle: james\ngetid/james".getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        List<Thread> threads = sip.addClient(new SocketMock(is, os));
        for (Thread t : threads) {
            t.join();
        }
        Assert.assertTrue(os.toString().startsWith("claimed: james\nid: "));
    }
}
