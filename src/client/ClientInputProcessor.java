package client;

import io.CSocketInputWorker;
import io.SocketOutputWorker;

import java.io.IOException;
import java.net.Socket;
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
import message.servertoclient.SMessageImpls.NormalAction;
import message.servertoclient.SMessageImpls.OnlineUserList;
import message.servertoclient.SMessageImpls.ReturnId;
import message.servertoclient.SMessageVisitor;

public class ClientInputProcessor implements SMessageVisitor<Void>, Controller {
    
    private MainListener ml;
    private Map<Long, ConversationListener> convListeners = 
            Collections.synchronizedMap(new HashMap<Long, ConversationListener>());
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
        makeConversation(rid.getId());
        return null;
    }

    @Override
    public Void visit(NormalAction na) {
        if (!convListeners.containsKey(na.getId())) {
            makeConversation(na.getId());
        }
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
        handle = hc.getHandle();
        ml.handleClaimed(hc.getHandle());
        return null;
    }

    @Override
    public Void visit(OnlineUserList oul) {
        ml.addOnlineUsers(oul.getHandles());
        return null;
    }
    
    @Override
    public void getId() {
        sow.add(new GetId(handle).toString());
    }
    
    @Override
    public void registerHandle(String handle) {
        sow.add(new RegisterHandle(handle, null).toString());
    }
    
    @Override
    public void getUsers() {
        sow.add(new GetUsers(handle).toString());
    }
    
    @Override
    public void addUsers(long id, List<String> users) {
        sow.add(new NormalAction(id, handle, NormalAction.ActionType.ADD_USER, 
                users, null).toString());
    }
    
    @Override
    public void exitConversation(long id) {
        sow.add(new NormalAction(id, handle, NormalAction.ActionType.EXIT_CONV, 
                null, null).toString());
    }
    
    @Override
    public void sendMessage(long id, String message) {
        sow.add(new NormalAction(id, handle, NormalAction.ActionType.TEXT_MESSAGE, 
                null, message).toString());
    }
}
