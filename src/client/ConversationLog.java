package client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ConversationLog {
    
    public static class Message {
        private String sender, message;
        
        public Message(String sender, String message) {
            this.sender = sender;
            this.message = message;
        }
        
        public String getSender() {
            return sender;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    private long id;
    private List<Message> messages = new LinkedList<Message>();
    
    public ConversationLog(long id) {
        this.id = id;
    }
    
    public void addMessage(String sender, String message) {
        messages.add(new Message(sender, message));
    }
    
    public Iterator<Message> messageIterator() {
        return messages.iterator();
    }
    
    public long getId() {
        return id;
    }
}
