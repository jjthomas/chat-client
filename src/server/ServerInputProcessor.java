package server;

import io.SSocketInputWorker;
import io.SocketOutputWorker;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
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
import message.servertoclient.SMessageImpls.InitialMessage;
import message.servertoclient.SMessageImpls.NormalAction;
import message.servertoclient.SMessageImpls.OnlineUserList;
import message.servertoclient.SMessageImpls.ReturnId;

public class ServerInputProcessor implements CMessageVisitor<Void> {
    
    private static Pattern handlePattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");
    
    private long nextTempId = 0;
    private AtomicLong nextConversationId = new AtomicLong();
    
    private Map<Long, Conversation> conversations = 
            Collections.synchronizedMap(new HashMap<Long, Conversation>());
    private Map<String, SocketOutputWorker> outputWorkers = 
            Collections.synchronizedMap(new HashMap<String, SocketOutputWorker>());
    private Map<String, SSocketInputWorker> handlelessInputWorkers = 
            Collections.synchronizedMap(new HashMap<String, SSocketInputWorker>());
    private Map<String, Set<Conversation>> conversationsByHandle = 
            Collections.synchronizedMap(new HashMap<String, Set<Conversation>>());
    
    public void addClient(Socket s) {
        SocketOutputWorker sow = null;
        SSocketInputWorker ssiw = null;
        try {
            sow = new SocketOutputWorker(s);
            ssiw = new SSocketInputWorker(s, nextTempId + "", this);
        } catch (IOException ioe) {
            try {
                s.close();
            } catch (IOException e) { /* can safely ignore */ }
        }
        
        outputWorkers.put(nextTempId + "", sow);
        handlelessInputWorkers.put(nextTempId + "", ssiw);
        nextTempId++;
        sow.start();
        ssiw.start();
    }

    @Override
    public Void visit(GetId gid) {
        ReturnId response = new ReturnId(nextConversationId.incrementAndGet());
        outputWorkers.get(gid.getSenderHandle()).add(response.toString());
        return null;
    }

    @Override
    public Void visit(InitialMessage im) {
        Conversation newC = new Conversation(im.getAllCommunicants(), 
                im.getId());
        conversations.put(im.getId(), newC);
        for (String handle : im.getAllCommunicants()) {
            outputWorkers.get(handle).add(im.toString());
            conversationsByHandle.get(handle).add(newC);
        }
        return null;
    }

    @Override
    public Void visit(NormalAction na) {
        boolean conversationEmpty = false;
        if (na.getActionType() == NormalAction.ActionType.ADD_USER) {
            Conversation c = conversations.get(na.getId());
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
            List<String> communicants = conversations.get(na.getId()).getCommunicants();
            synchronized(communicants) {
                for (String handle : communicants)
                    outputWorkers.get(handle).add(na.toString());
            }
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
            synchronized(conversationsByHandle) {
                if (!conversationsByHandle.containsKey(rh.getHandle())) {
                    conversationsByHandle.put(rh.getHandle(), 
                            Collections.synchronizedSet(new HashSet<Conversation>()));
                } else {
                    claimed = false;
                }
            }
            if (claimed) {
                handlelessInputWorkers.remove(rh.getTempHandle()).setHandle(
                        rh.getHandle());
                outputWorkers.put(rh.getHandle(), outputWorkers.remove(
                        rh.getTempHandle()));
                response = new HandleClaimed(rh.getHandle());
                synchronized(conversationsByHandle) {
                    for (String handle : conversationsByHandle.keySet()) {
                        if (!handle.equals(rh.getHandle())) {
                            outputWorkers.get(handle).add(new 
                                    AvailabilityInfo(rh.getHandle(), 
                                            AvailabilityInfo.Status.ONLINE).toString());
                        }
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
        synchronized(conversationsByHandle) { // toString() iterates over currentHandles
            outputWorkers.get(gu.getSenderHandle()).add(new OnlineUserList(
                    conversationsByHandle.keySet()).toString());
        }
        return null;
    }

    @Override
    public Void visit(Disconnect d) {
        outputWorkers.remove(d.getHandle());
        if (handlelessInputWorkers.remove(d.getHandle()) == null) {
            Set<Conversation> convs = conversationsByHandle.get(d.getHandle());
            synchronized(convs) {
                for (Conversation c : convs) {
                    visit(new NormalAction(c.getId(), d.getHandle(), 
                          NormalAction.ActionType.EXIT_CONV, null, null));
                }
            }
            conversationsByHandle.remove(d.getHandle());
            synchronized(conversationsByHandle) {
                for (String handle : conversationsByHandle.keySet()) {
                    outputWorkers.get(handle).add(new AvailabilityInfo(d.getHandle(), 
                            AvailabilityInfo.Status.OFFLINE).toString());
                }
            }
        }
        return null;
    }

}