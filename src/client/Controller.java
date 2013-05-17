package client;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * See Conversation Design section 5bi for high-level documentation.
 */
public interface Controller {
    public void initialize(Socket s, MainListener ml) throws IOException;
    public void getId();
    public void registerHandle(String handle);
    public void getUsers();
    public void addUsers(long id, List<String> users);
    public void exitConversation(long id);
    public void sendMessage(long id, String message);
    public void addConversationListener(long id, ConversationListener cl);
}
