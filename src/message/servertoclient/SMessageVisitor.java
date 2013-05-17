package message.servertoclient;

/**
 * See Conversation Design section 1 for high-level documentation.
 */
public interface SMessageVisitor<T> {
    public T visit(SMessageImpls.ReturnId rid);
    public T visit(SMessageImpls.NormalAction na);
    public T visit(SMessageImpls.AvailabilityInfo ai);
    public T visit(SMessageImpls.BadHandle bh);
    public T visit(SMessageImpls.HandleClaimed hc);
    public T visit(SMessageImpls.OnlineUserList oul);
}
