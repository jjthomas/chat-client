package client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import message.util.Util;

public class MainListenerMock implements MainListener {
    
    public static final String S = "/";
    
    private OutputStream os;
    
    public MainListenerMock(OutputStream os) {
        this.os = os;
    }

    @Override
    public ConversationListener makeConversationListener(long id) {
        try {
            os.write(("ml" + S + "makeConversationListener" + S + id + 
                    "\n").getBytes());
        } catch (IOException ioe) {}
        return new ConversationListenerMock(os, id);
    }

    @Override
    public void addOnlineUsers(Collection<String> handles) {
        try {
            os.write(("ml" + S + "addOnlineUsers" + S + 
                    Util.serializeCollection(handles) + "\n").getBytes());
        } catch (IOException ioe) {}
    }

    @Override
    public void removeOfflineUser(String handle) {
        try {
            os.write(("ml" + S + "removeOfflineUser" + S + 
                    handle + "\n").getBytes());
        } catch (IOException ioe) {}
    }

    @Override
    public void badHandle(String handle) {
        try {
            os.write(("ml" + S + "badHandle" + S + 
                    handle + "\n").getBytes());
        } catch (IOException ioe) {}
    }

    @Override
    public void handleClaimed(String handle) {
        try {
            os.write(("ml" + S + "handleClaimed" + S + 
                    handle + "\n").getBytes());
        } catch (IOException ioe) {}
    }
}
