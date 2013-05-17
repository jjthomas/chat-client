package message.clienttoserver;

/**
 * See Conversation Design section 2 for high-level documentation.
 */
public interface CMessage {
    public <T> T accept(CMessageVisitor<T> cmv);
}
