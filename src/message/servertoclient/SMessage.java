package message.servertoclient;


public interface SMessage {
    public <T> T accept(SMessageVisitor<T> smv);
} 
