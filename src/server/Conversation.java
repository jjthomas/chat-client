package server;

import java.util.List;

public class Conversation {
    private List<String> communicants;
    private long id;
    
    public Conversation(List<String> communicants, long id) {
        this.communicants = communicants;
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public List<String> getCommunicants() {
        return communicants;
    }
    
    public void addCommunicant(String communicant) {
        communicants.add(communicant);
    }
    
    public void addCommunicants(List<String> additionalCommunicants) {
        communicants.addAll(additionalCommunicants);
    }
}
