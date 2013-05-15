package server;

import message.clienttoserver.CMessage;

public interface CMessageDispatcher {
    public void add(CMessage cm);
}
