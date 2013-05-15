package client;

import java.util.Collection;

public interface MainListener {
    public void setController(Controller c);
    public ConversationListener makeConversationListener(long id);
    public void addOnlineUsers(Collection<String> handles);
    public void removeOfflineUser(String handle);
    public void badHandle(String handle);
    public void handleClaimed(String handle);
    public void newId(long id);
}
