package message.clienttoserver;

import message.servertoclient.SMessageImpls;

/**
 * See Conversation Design section 2 for high-level documentation.
 */
public interface CMessageVisitor<T> {
    public T visit(CMessageImpls.GetId gid);
    public T visit(SMessageImpls.NormalAction na);
    public T visit(CMessageImpls.RegisterHandle rh);
    public T visit(CMessageImpls.GetUsers gu);
    public T visit(CMessageImpls.Disconnect d);
}
