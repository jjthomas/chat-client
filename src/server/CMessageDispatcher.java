package server;

import message.clienttoserver.CMessage;

/**
 * See Conversation Design section 4b for high-level documentation.
 */
public interface CMessageDispatcher {
    public void add(CMessage cm);
}
