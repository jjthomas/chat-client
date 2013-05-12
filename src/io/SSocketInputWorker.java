package io;

import java.io.IOException;
import java.net.Socket;

import message.clienttoserver.CMessageImpls;
import message.clienttoserver.CMessageVisitor;

public class SSocketInputWorker extends SocketInputWorker {
    
    private String tempHandle;
    private String handle;
    private CMessageVisitor<Void> cmv;
    
    public SSocketInputWorker(Socket s, String tempHandle, CMessageVisitor<Void> cmv) 
            throws IOException {
        super(s);
        this.tempHandle = tempHandle;
        this.cmv = cmv;
    }
    
    public void setHandle(String handle) {
        this.handle = handle;
    }

    @Override
    public void process(String input) {
        if (input.startsWith(SocketInputWorker.DISCONNECT))
            input += ": " + (handle == null ? tempHandle : handle);
        else if (input.startsWith("handle"))
            input += CMessageImpls.SEPARATOR + tempHandle;
        CMessageImpls.deserialize(input).accept(cmv);
    }

}
