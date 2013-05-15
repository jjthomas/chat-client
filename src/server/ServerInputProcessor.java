package server;

import io.SSocketInputWorker;
import io.SocketOutputWorker;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import message.clienttoserver.CMessageImpls.Disconnect;
import message.clienttoserver.CMessageImpls.GetId;
import message.clienttoserver.CMessageImpls.GetUsers;
import message.clienttoserver.CMessageImpls.RegisterHandle;
import message.clienttoserver.CMessageVisitor;
import message.servertoclient.SMessage;
import message.servertoclient.SMessageImpls.AvailabilityInfo;
import message.servertoclient.SMessageImpls.BadHandle;
import message.servertoclient.SMessageImpls.HandleClaimed;
import message.servertoclient.SMessageImpls.NormalAction;
import message.servertoclient.SMessageImpls.OnlineUserList;
import message.servertoclient.SMessageImpls.ReturnId;

public class ServerInputProcessor implements CMessageVisitor<Void> {
    
    private static Pattern handlePattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");
    
    private long nextTempId = 0;
    private long nextConversationId = 0;
    private ServerMessageDispatcher smd;
    
    private Map<Long, Conversation> conversations = 
            new HashMap<Long, Conversation>();
    private Map<String, SocketOutputWorker> outputWorkers = 
            new HashMap<String, SocketOutputWorker>();
    private Map<String, SSocketInputWorker> handlelessInputWorkers = 
            new HashMap<String, SSocketInputWorker>();
    private Map<String, Set<Conversation>> conversationsByHandle = 
            new HashMap<String, Set<Conversation>>();
    
    
    public ServerInputProcessor() {
        smd = new ServerMessageDispatcher(this);
        smd.start();
    }
    
    // return the started threads, purely for
    // testing purposes
    public List<Thread> addClient(Socket s) {
        SocketOutputWorker sow = null;
        SSocketInputWorker ssiw = null;
        try {
            sow = new SocketOutputWorker(s);
            ssiw = new SSocketInputWorker(s, nextTempId + "", smd);
        } catch (IOException ioe) {
            try {
                s.close();
                return null;
            } catch (IOException e) { /* can safely ignore */ }
        }
        
        outputWorkers.put(nextTempId + "", sow);
        handlelessInputWorkers.put(nextTempId + "", ssiw);
        nextTempId++;
        sow.start();
        ssiw.start();
        return Arrays.asList(sow, ssiw);
    }

    @Override
    public Void visit(GetId gid) {
        ReturnId response = new ReturnId(nextConversationId++);
        outputWorkers.get(gid.getSenderHandle()).add(response.toString());
        return null;
    }
    
    private NormalAction removeInvalidAddedUsers(NormalAction na) {
        List<String> filteredAddedUsers = new ArrayList<String>();
        Conversation c = conversations.get(na.getId());
        for (String handle : na.getHandles()) {
            if (conversationsByHandle.containsKey(handle) && 
                    !c.getCommunicants().contains(handle))
                filteredAddedUsers.add(handle);
        }
        return new NormalAction(na.getId(), na.getSenderHandle(), 
                na.getActionType(), filteredAddedUsers, na.getCurrentUsers(), 
                null);
    }

    @Override
    public Void visit(NormalAction na) {
        if (!conversations.containsKey(na.getId())) {
            Conversation newC = new Conversation(Arrays.asList(
                    na.getSenderHandle()), na.getId());
            conversations.put(na.getId(), newC);
            conversationsByHandle.get(na.getSenderHandle()).add(newC);
        }
        boolean conversationEmpty = false;
        NormalAction withCurrentUsers = null;
        if (na.getActionType() == NormalAction.ActionType.ADD_USER) {
            na = removeInvalidAddedUsers(na);
            if (na.getHandles().isEmpty())
                return null;
            Conversation c = conversations.get(na.getId());
            List<String> currentUsers = new ArrayList<String>();
            for (String handle : c.getCommunicants()) {
                currentUsers.add(handle);
            }
            withCurrentUsers = new NormalAction(na.getId(), na.getSenderHandle(), 
                    na.getActionType(), na.getHandles(), currentUsers, 
                    null);
            c.addCommunicants(na.getHandles());
            for (String handle : na.getHandles()) {
                conversationsByHandle.get(handle).add(c);
            }
        } else if (na.getActionType() == NormalAction.ActionType.EXIT_CONV) {
            Conversation c = conversations.get(na.getId());
            conversationEmpty = c.deleteCommunicant(na.getSenderHandle());
            conversationsByHandle.get(na.getSenderHandle()).remove(c);
        }
        
        if (!conversationEmpty) {
            List<String> communicants = conversations.get(na.getId())
                    .getCommunicants();
            for (String handle : communicants)
                outputWorkers.get(handle).add((na.getActionType() == 
                NormalAction.ActionType.ADD_USER && 
                na.getHandles().contains(handle)) ? 
                withCurrentUsers.toString() : na.toString());
        } else {
            conversations.remove(na.getId());
        }
        return null;
    }

    @Override
    public Void visit(RegisterHandle rh) {
        SMessage response = null;
        SocketOutputWorker out = outputWorkers.get(rh.getTempHandle());
        if (!handlePattern.matcher(rh.getHandle()).matches()) {
            response = new BadHandle(rh.getHandle());
        } else {
            boolean claimed = true;
            if (!conversationsByHandle.containsKey(rh.getHandle())) {
                conversationsByHandle.put(rh.getHandle(), 
                        Collections.synchronizedSet(new HashSet<Conversation>()));
            } else {
                claimed = false;
            }
            if (claimed) {
                handlelessInputWorkers.remove(rh.getTempHandle()).setHandle(
                        rh.getHandle());
                outputWorkers.put(rh.getHandle(), outputWorkers.remove(
                        rh.getTempHandle()));
                response = new HandleClaimed(rh.getHandle());
                for (String handle : conversationsByHandle.keySet()) {
                    if (!handle.equals(rh.getHandle())) {
                        outputWorkers.get(handle).add(new 
                                AvailabilityInfo(rh.getHandle(), 
                                        AvailabilityInfo.Status.ONLINE).toString());
                    }
                }
            } else {
                response = new BadHandle(rh.getHandle());
            }
        }
        
        out.add(response.toString());
        return null;
            
    }

    @Override
    public Void visit(GetUsers gu) {
        outputWorkers.get(gu.getSenderHandle()).add(new OnlineUserList(
                conversationsByHandle.keySet()).toString());
        return null;
    }

    @Override
    public Void visit(Disconnect d) {
        // terminate the SocketOutputWorker
        outputWorkers.remove(d.getHandle()).add("");
        if (handlelessInputWorkers.remove(d.getHandle()) == null) {
            Set<Conversation> convs = new HashSet<Conversation>(conversationsByHandle.get(d.getHandle()));
            for (Conversation c : convs) {
                visit(new NormalAction(c.getId(), d.getHandle(), 
                      NormalAction.ActionType.EXIT_CONV, null, null, null));
            }
            conversationsByHandle.remove(d.getHandle());
            for (String handle : conversationsByHandle.keySet()) {
                outputWorkers.get(handle).add(new AvailabilityInfo(d.getHandle(), 
                        AvailabilityInfo.Status.OFFLINE).toString());
            }
        }
        return null;
    }

}
