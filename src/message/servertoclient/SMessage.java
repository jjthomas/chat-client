package message.servertoclient;


/**
 * See Conversation Design section 1 for high-level documentation.
 */
public interface SMessage {
    public <T> T accept(SMessageVisitor<T> smv);
} 
