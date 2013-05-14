package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import message.servertoclient.SMessageImpls.OnlineUserList;

import org.junit.Assert;
import org.junit.Test;


public class ClientInputProcessorTest {
    @Test
    public void testOnlineUserList() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MainListener ml = new MainListenerMock(os);
        ClientInputProcessor cip = new ClientInputProcessor(ml);
        cip.visit(new OnlineUserList(Arrays.asList("john", "james")));
        Assert.assertArrayEquals(os.toByteArray(), 
                "ml/addOnlineUsers/john,james\n".getBytes());
    }
}
