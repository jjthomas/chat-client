package io;

import java.io.IOException;
import java.net.Socket;

import message.servertoclient.SMessageImpls;
import message.servertoclient.SMessageVisitor;

public class CSocketInputWorker extends SocketInputWorker {
    private SMessageVisitor<Void> smv;
    
    public CSocketInputWorker(Socket s, SMessageVisitor<Void> smv) 
            throws IOException {
        super(s);
        this.smv = smv;
    }

    @Override
    public void process(String input) {
        SMessageImpls.deserialize(input).accept(smv);
    }

}
