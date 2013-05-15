package client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import message.util.Util;

public class ConversationListenerMock implements ConversationListener {
    public static final String S = "/";
    
    private OutputStream os;
    private long id;
    
    
    public ConversationListenerMock(OutputStream os, long id) {
        this.os = os;
        this.id = id;
    }
    
    @Override
    public void addMessage(String senderHandle, String message) {
        try {
            os.write(("cl" + S + "addMessage" + S + id + S + senderHandle 
                    + S + message + "\n").getBytes());
        } catch (IOException ioe) {}  
    }

    @Override
    public void removeUser(String handle) {
        try {
            os.write(("cl" + S + "removeUser" + S + id + S + handle + "\n")
                    .getBytes());
        } catch (IOException ioe) {}      
    }

    @Override
    public void addUsers(List<String> handles) {
        try {
            os.write(("cl" + S + "addUsers" + S + id + S + 
                    Util.serializeCollection(handles) + "\n").getBytes());            
        } catch (IOException ioe) {}
    }

    @Override
    public void setController(Controller c) {
        return; // no controller
    }
}
