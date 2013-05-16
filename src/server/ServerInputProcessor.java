package server;

import io.SSocketInputWorker;
import io.SocketOutputWorker;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * See Conversation Design section 4d for high-level documentation.
 */
public class ServerInputProcessor implements CMessageVisitor<Void> {
    
    // regex that all handles must match
    private static Pattern handlePattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");
    
    // we first assign a temporary ID to connected clients since they have
    // not yet registered a handle
    private long nextTempId = 0;
    
    private long nextConversationId = 0;
    private ServerMessageDispatcher smd;
    
    private Map<Long, Conversation> conversations = 
            new HashMap<Long, Conversation>();
    // a SocketOutputWorker for every connected client
    private Map<String, SocketOutputWorker> outputWorkers = 
            new HashMap<String, SocketOutputWorker>();
    // SSocketInputWorkers for clients that have not yet 
    // claimed handles. We need this map because once
    // the handle is claimed, we need to set the handle
    // variable in SSocketInputWorker.
    private Map<String, SSocketInputWorker> handlelessInputWorkers = 
            new HashMap<String, SSocketInputWorker>();
    // for faster lookup of conversations by user
    // the keySet of this Map is also our record of the 
    // claimed handles
    private Map<String, Set<Conversation>> conversationsByHandle = 
            new HashMap<String, Set<Conversation>>();
    
    
    public ServerInputProcessor() {
        // we use a sequential message dispatcher
        // to eliminate all possiblity of concurrency
        // issues
        smd = new ServerMessageDispatcher(this);
        smd.start();
    }
    

    /**
     * Set up the SSocketInputWorker and SocketOutputWorker for a
     * newly connected client.
     * @param s socket for newly connected client
     * @return the started SocketInputWorker and SocketOutputWorker, purely for
     * testing purposes
     */
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
            } catch (IOException e) { return null; }
        }
        
        outputWorkers.put(nextTempId + "", sow);
        handlelessInputWorkers.put(nextTempId + "", ssiw);
        nextTempId++;
        sow.start();
        ssiw.start();
        return Arrays.asList(sow, ssiw);
    }

    /**
     * Return the next available conversation ID to the requesting client.
     */
    @Override
    public Void visit(GetId gid) {
        ReturnId response = new ReturnId(nextConversationId++);
        outputWorkers.get(gid.getSenderHandle()).add(response.toString());
        return null;
    }
    
    /**
     * Remove from a NormalAction any added users who are already
     * in the targeted conversation or are no longer online.
     * @param na a deserialized NormalAction message
     * @return a sanitized version of the same NormalAction
     */
    private NormalAction removeInvalidAddedUsers(NormalAction na) {
        List<String> filteredAddedUsers = new ArrayList<String>();
        Conversation c = conversations.get(na.getId());
        for (String handle : na.getHandles()) {
            if (conversationsByHandle.containsKey(handle) /* user online */ && 
                    !c.getCommunicants().contains(handle))
                filteredAddedUsers.add(handle);
        }
        return new NormalAction(na.getId(), na.getSenderHandle(), 
                na.getActionType(), filteredAddedUsers, na.getCurrentUsers(), 
                null);
    }
    
    /**
     * Process a NormalAction, updating our data structures
     * and then forwarding the message to the relevant
     * clients.
     */
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
                return null; // do nothing since no added users were valid
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
    
    /**
     * Act on a user's request to register a handle and tell them
     * whether the registration was successful or not.
     */
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
                        new HashSet<Conversation>());
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
    
    /**
     * Sends the list of online users back to the 
     * requesting user.
     */
    @Override
    public Void visit(GetUsers gu) {
        outputWorkers.get(gu.getSenderHandle()).add(new OnlineUserList(
                conversationsByHandle.keySet()).toString());
        return null;
    }
    
    /**
     * Perform the necessary actions when a client disconnects,
     * including deleting items from our data structures, exiting
     * conversations, and notifying all remaining clients of the
     * user offline event.
     */
    @Override
    public Void visit(Disconnect d) {
        // terminate the SocketOutputWorker
        outputWorkers.remove(d.getHandle()).add("");
        // if the disconnecting user had claimed a handle
        if (handlelessInputWorkers.remove(d.getHandle()) == null) {
            // need to make a copy because we will be deleting elements from the
            // the original set during the iteration
            Set<Conversation> convs = new HashSet<Conversation>(
                    conversationsByHandle.get(d.getHandle()));
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
