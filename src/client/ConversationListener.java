package client;

import java.util.List;

public interface ConversationListener {
    public void setController(Controller c);
    public void addMessage(String senderHandle, String message);
    public void removeUser(String handle);
    public void addUsers(List<String> handles);
}
