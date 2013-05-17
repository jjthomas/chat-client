package io;

import java.io.IOException;
import java.net.Socket;

import server.CMessageDispatcher;

import message.clienttoserver.CMessageImpls;

/**
 * See Conversation Design section 4c for high-level documentation.
 */
public class SSocketInputWorker extends SocketInputWorker {
    
    private String tempHandle;
    private String handle;
    private CMessageDispatcher cmd;
    
    public SSocketInputWorker(Socket s, String tempHandle, CMessageDispatcher cmd) 
            throws IOException {
        super(s);
        this.tempHandle = tempHandle;
        this.cmd = cmd;
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
        cmd.add(CMessageImpls.deserialize(input));
    }

}
