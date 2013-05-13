package client;

import java.util.List;

public interface Controller {
    public void getId();
    public void registerHandle(String handle);
    public void getUsers();
    public void addUsers(long id, List<String> users);
    public void exitConversation(long id);
    public void sendMessage(long id, String message);
}
