package server;

import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private List<String> communicants;
    private long id;
    
    public Conversation(List<String> communicants, long id) {
        this.communicants = new ArrayList<String>(communicants);
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public List<String> getCommunicants() {
        return communicants;
    }
    
    public void addCommunicants(List<String> additionalCommunicants) {
        communicants.addAll(additionalCommunicants);
    }
    
    // returns true if communicants is now empty
    public boolean deleteCommunicant(String communicant) {
        communicants.remove(communicant);
        return communicants.isEmpty();
    }
}
