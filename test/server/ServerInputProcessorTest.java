package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import message.servertoclient.SMessageImpls.NormalAction;
import message.servertoclient.SMessageImpls.NormalAction.ActionType;

import org.junit.Assert;
import org.junit.Test;

import client.ClientInputProcessor;
import client.MainListener;
import client.MainListenerMock;

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
