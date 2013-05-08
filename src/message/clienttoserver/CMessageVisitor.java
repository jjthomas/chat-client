package message.clienttoserver;

import message.servertoclient.SMessageImpls;

public interface CMessageVisitor<T> {
    public T visit(CMessageImpls.GetId gid);
    public T visit(SMessageImpls.InitialMessage im);
    public T visit(SMessageImpls.NormalAction na);
    public T visit(CMessageImpls.RegisterHandle rh);
    public T visit(CMessageImpls.GetUsers gu);
    public T visit(CMessageImpls.Disconnect d);
}
