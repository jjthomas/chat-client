package server;

import io.SSocketInputWorker;
import io.SocketOutputWorker;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import message.clienttoserver.CMessageImpls.Disconnect;
import message.clienttoserver.CMessageImpls.GetId;
import message.clienttoserver.CMessageImpls.GetUsers;
import message.clienttoserver.CMessageImpls.RegisterHandle;
import message.clienttoserver.CMessageVisitor;
import message.servertoclient.SMessageImpls.InitialMessage;
import message.servertoclient.SMessageImpls.NormalAction;

public class ServerInputProcessor implements CMessageVisitor<Void> {
    
    private long nextTempId = 0;
    
    private Map<Long, Conversation> conversations = 
            Collections.synchronizedMap(new HashMap<Long, Conversation>());
    private Map<String, SocketOutputWorker> outputWorkers = 
            Collections.synchronizedMap(new HashMap<String, SocketOutputWorker>());
    private Map<String, SSocketInputWorker> handlelessInputWorkers = 
            Collections.synchronizedMap(new HashMap<String, SSocketInputWorker>());
    
    public void addClient(Socket s) {
        SocketOutputWorker sow = null;
        SSocketInputWorker ssiw = null;
        try {
            sow = new SocketOutputWorker(s);
            ssiw = new SSocketInputWorker(s, nextTempId + "", this);
        } catch (IOException ioe) {
            // update concurrency strategy with Conversation plus other
            // map value modification -- these are thread-safe structures
            // too
        }
        outputWorkers.put(nextTempId + "", sow);
        
    }

    @Override
    public Void visit(GetId gid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visit(InitialMessage im) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visit(NormalAction na) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visit(RegisterHandle rh) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visit(GetUsers gu) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visit(Disconnect d) {
        // TODO Auto-generated method stub
        return null;
    }

}
