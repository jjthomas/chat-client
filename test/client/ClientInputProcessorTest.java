package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import message.clienttoserver.CMessage;
import message.clienttoserver.CMessageImpls;
import message.servertoclient.SMessageImpls.AvailabilityInfo;
import message.servertoclient.SMessageImpls.AvailabilityInfo.Status;
import message.servertoclient.SMessageImpls.BadHandle;
import message.servertoclient.SMessageImpls.HandleClaimed;
import message.servertoclient.SMessageImpls.NormalAction;
import message.servertoclient.SMessageImpls.NormalAction.ActionType;
import message.servertoclient.SMessageImpls.OnlineUserList;
import message.servertoclient.SMessageImpls.ReturnId;

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
    @Test
    public void testReturnId() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MainListener ml = new MainListenerMock(os);
        ClientInputProcessor cip = new ClientInputProcessor(ml);
        cip.visit(new ReturnId(12));
        Assert.assertArrayEquals(os.toByteArray(), 
                "ml/newId/12\n".getBytes());
    }
    @Test
    public void testNormalActionAddUser() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MainListener ml = new MainListenerMock(os);
        ClientInputProcessor cip = new ClientInputProcessor(ml);
        cip.visit(new NormalAction(12, "james", ActionType.ADD_USER, Arrays.asList("john", "james"), Arrays.asList("john", "james"), ""));
        Assert.assertEquals(os.toString(), 
        		"ml/makeConversationListener/12\ncl/addUsers/12/john,james\ncl/addUsers/12/john,james\n");
    }
    @Test
    public void testNormalActionExitConv() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MainListener ml = new MainListenerMock(os);
        ClientInputProcessor cip = new ClientInputProcessor(ml);
        cip.visit(new NormalAction(12, "james", ActionType.EXIT_CONV, Arrays.asList("john", "james"), Arrays.asList("john", "james"), ""));
        Assert.assertEquals(os.toString(), 
        		"ml/makeConversationListener/12\ncl/addUsers/12/john,james\ncl/removeUser/12/james\n");
    }
    @Test
    public void testNormalActionTextMessage() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MainListener ml = new MainListenerMock(os);
        ClientInputProcessor cip = new ClientInputProcessor(ml);
        cip.visit(new NormalAction(12, "james", ActionType.TEXT_MESSAGE, Arrays.asList("john", "james"), Arrays.asList("john", "james"), "qwertyuiopasdfghjklzxcvbnm1234567890[]\\;',./"));
        Assert.assertEquals(os.toString(), 
                "ml/makeConversationListener/12\ncl/addUsers/12/john,james\ncl/addMessage/12/james/qwertyuiopasdfghjklzxcvbnm1234567890[]\\;',./\n");
    }
    @Test
    public void testAvailabilityInfo() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MainListener ml = new MainListenerMock(os);
        ClientInputProcessor cip = new ClientInputProcessor(ml);
        cip.visit(new AvailabilityInfo("james", Status.ONLINE));
        Assert.assertEquals(os.toString(), 
                "ml/addOnlineUsers/james\n");
    }
    @Test
    public void testBadHandle() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MainListener ml = new MainListenerMock(os);
        ClientInputProcessor cip = new ClientInputProcessor(ml);
        cip.visit(new BadHandle("james"));
        Assert.assertEquals(os.toString(), 
                "ml/badHandle/james\n");
    }
    @Test
    public void testHandleClaimed() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MainListener ml = new MainListenerMock(os);
        ClientInputProcessor cip = new ClientInputProcessor(ml);
        cip.visit(new HandleClaimed("james"));
        Assert.assertEquals(os.toString(), 
                "ml/handleClaimed/james\n");
    }
    
    
}


