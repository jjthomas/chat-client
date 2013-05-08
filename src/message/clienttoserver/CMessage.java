package message.clienttoserver;


public interface CMessage {
    public <T> T accept(CMessageVisitor<T> cmv);
}
