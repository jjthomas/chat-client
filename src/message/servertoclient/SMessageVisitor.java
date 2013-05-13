package message.servertoclient;

public interface SMessageVisitor<T> {
    public T visit(SMessageImpls.ReturnId rid);
    public T visit(SMessageImpls.NormalAction na);
    public T visit(SMessageImpls.AvailabilityInfo ai);
    public T visit(SMessageImpls.BadHandle bh);
    public T visit(SMessageImpls.HandleClaimed hc);
    public T visit(SMessageImpls.OnlineUserList oul);
}
