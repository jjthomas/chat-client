package client;

import io.CSocketInputWorker;
import io.SocketOutputWorker;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import message.clienttoserver.CMessageImpls.GetId;
import message.clienttoserver.CMessageImpls.GetUsers;
import message.clienttoserver.CMessageImpls.RegisterHandle;
import message.servertoclient.SMessageImpls.AvailabilityInfo;
import message.servertoclient.SMessageImpls.BadHandle;
import message.servertoclient.SMessageImpls.HandleClaimed;
import message.servertoclient.SMessageImpls.InitialMessage;
import message.servertoclient.SMessageImpls.NormalAction;
import message.servertoclient.SMessageImpls.OnlineUserList;
import message.servertoclient.SMessageImpls.ReturnId;
import message.servertoclient.SMessageVisitor;

public class ClientInputProcessor implements SMessageVisitor<Void> {
    
    private MainListener ml;
    private Map<Long, ConversationListener> convListeners = 
            Collections.synchronizedMap(new HashMap<Long, ConversationListener>());
    private Map<Long, List<String>> unstartedConversations = 
            Collections.synchronizedMap(new HashMap<Long, List<String>>());
    private Map<Long, ConversationLog> convLogs = new HashMap<Long, ConversationLog>();
    private SocketOutputWorker sow;
    private String handle;
    
    public ClientInputProcessor(Socket s, MainListener ml) throws IOException {
        this.ml = ml;
        sow = new SocketOutputWorker(s);
        sow.start();
        new CSocketInputWorker(s, this).start();
    }
    
    private void makeConversation(long id) {
        convListeners.put(id, ml.makeConversationListener(id));
        convLogs.put(id, new ConversationLog(id));
    }
    
    private void addMessage(long id, String sender, String message) {
        convListeners.get(id).addMessage(sender, message);
        convLogs.get(id).addMessage(sender, message);
    }

    @Override
    public Void visit(ReturnId rid) {
        unstartedConversations.put(rid.getId(), Arrays.asList(handle));
        makeConversation(rid.getId());
        return null;
    }

    @Override
    public Void visit(InitialMessage im) { // TODO account for case 
        // where we sent this InitialMessage
        makeConversation(im.getId());
        addMessage(im.getId(), im.getSenderHandle(), im.getTextMessage());
        convListeners.get(im.getId()).addUsers(im.getAllCommunicants());
        return null;
    }

    @Override
    public Void visit(NormalAction na) {
        switch(na.getActionType()) {
        case ADD_USER:
            convListeners.get(na.getId()).addUsers(na.getHandles()); break;
        case EXIT_CONV:
            convListeners.get(na.getId()).removeUser(na.getSenderHandle()); break;
        case TEXT_MESSAGE:
            addMessage(na.getId(), na.getSenderHandle(), na.getTextMessage()); break;
        }
        return null;
    }

    @Override
    public Void visit(AvailabilityInfo ai) {
        if (ai.getStatus() == AvailabilityInfo.Status.ONLINE) {
            ml.addOnlineUsers(Arrays.asList(ai.getHandle()));
        } else {
            ml.removeOfflineUser(ai.getHandle());
        }
        return null;
    }

    @Override
    public Void visit(BadHandle bh) {
        ml.badHandle(bh.getHandle());
        return null;
    }

    @Override
    public Void visit(HandleClaimed hc) {
        ml.handleClaimed(hc.getHandle());
        handle = hc.getHandle();
        return null;
    }

    @Override
    public Void visit(OnlineUserList oul) {
        ml.addOnlineUsers(oul.getHandles());
        return null;
    }
    
    public void getId() {
        sow.add(new GetId(handle).toString());
    }
    
    public void registerHandle(String handle) {
        sow.add(new RegisterHandle(handle, null).toString());
    }
    
    public void getUsers() {
        sow.add(new GetUsers(handle).toString());
    }
    
    public void addUsers(long id, List<String> users) {
        if (unstartedConversations.containsKey(id)) {
            unstartedConversations.get(id).addAll(users);
        } else {
            sow.add(new NormalAction(id, handle, NormalAction.ActionType.ADD_USER, 
                    users, null).toString());
        }
    }
    
    public void exitConversation(long id) {
        if (unstartedConversations.remove(id) == null) {
            sow.add(new NormalAction(id, handle, NormalAction.ActionType.EXIT_CONV, 
                    null, null).toString());
        }
    }
    
    public void sendMessage(long id, String message) {
        List<String> users = unstartedConversations.remove(id);
        if (users == null) {
            sow.add(new NormalAction(id, handle, NormalAction.ActionType.TEXT_MESSAGE, 
                    null, message).toString());
        } else {
            sow.add(new InitialMessage(id, handle, users, message).toString());
        }
    }
}
