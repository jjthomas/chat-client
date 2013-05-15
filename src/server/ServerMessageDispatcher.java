package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import message.clienttoserver.CMessage;
import message.clienttoserver.CMessageVisitor;

public class ServerMessageDispatcher extends Thread implements CMessageDispatcher {
    
    private BlockingQueue<CMessage> q = new LinkedBlockingQueue<CMessage>();
    private CMessageVisitor<Void> cmv;
    
    public ServerMessageDispatcher(CMessageVisitor<Void> cmv) {
        this.cmv = cmv;
    }

    @Override
    public void add(CMessage cm) {
        q.add(cm);
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                q.take().accept(cmv);
            } catch (InterruptedException impossible) {}
        }
    }
    
}
