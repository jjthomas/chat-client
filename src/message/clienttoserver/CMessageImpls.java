package message.clienttoserver;

import message.servertoclient.SMessageImpls;
import message.util.Util;

public class CMessageImpls {
    
    public static final String SEPARATOR = "/";
    public static class GetId implements CMessage {
        private String senderHandle;
        
        public GetId(String senderHandle) {
            this.senderHandle = senderHandle;
        }
        
        public static GetId deserialize(String wireMessage) {
            return new GetId(wireMessage.substring(
                    wireMessage.indexOf(SEPARATOR) + 1));
        }
        
        public String getSenderHandle() {
            return senderHandle;
        }

        @Override
        public <T> T accept(CMessageVisitor<T> cmv) {
            return cmv.visit(this);
        }
        
        @Override
        public String toString() {
            return "getid" + SEPARATOR + senderHandle;
        }
        
    }
    
    public static class RegisterHandle implements CMessage {
        private String handle;
        private String tempHandle;
        
        public RegisterHandle(String handle, String tempHandle) {
            this.handle = handle;
            this.tempHandle = tempHandle;
        }
        
        public static RegisterHandle deserialize(String wireMessage) {
            String[] components = wireMessage.split(SEPARATOR);
            return new RegisterHandle(Util.removeTag(components[0]), components[1]);
        }
        
        public String getHandle() {
            return handle;
        }
        
        public String getTempHandle() {
            return tempHandle;
        }
        
        @Override
        public <T> T accept(CMessageVisitor<T> cmv) {
            return cmv.visit(this);
        }
        
        @Override
        public String toString() {
            return "handle: " + handle;
        }
    }
    
    public static class GetUsers implements CMessage {
        private String senderHandle;
        
        public GetUsers(String senderHandle) {
            this.senderHandle = senderHandle;
        }
        
        public static GetUsers deserialize(String wireMessage) {
            return new GetUsers(wireMessage.substring(
                    wireMessage.indexOf(SEPARATOR) + 1));            
        }
        
        public String getSenderHandle() {
            return senderHandle;
        }
        
        @Override
        public <T> T accept(CMessageVisitor<T> cmv) {
            return cmv.visit(this);
        }
        
        @Override
        public String toString() {
            return "getusers" + SEPARATOR + senderHandle;
        }
    }
    
    public static class Disconnect implements CMessage {
        private String handle;
        
        public Disconnect(String handle) {
            this.handle = handle;
        }
        
        public static Disconnect deserialize(String wireMessage) {
            return new Disconnect(Util.removeTag(wireMessage));
        }
        
        public String getHandle() {
            return handle;
        }
        
        @Override
        public <T> T accept(CMessageVisitor<T> cmv) {
            return cmv.visit(this);
        }
        
        /*
         * We don't need a toString() because we'll
         * never need to explicitly send this message
         * from the client -- it's always faked on the server
         * when the server detects that the client has
         * disconnected.
         */
    }
    
    public static CMessage deserialize(String wireMessage) {
        if (wireMessage.startsWith("getid")) {
            return GetId.deserialize(wireMessage);
        } else if (wireMessage.startsWith("conv")) {
            return SMessageImpls.NormalAction.deserialize(wireMessage);
        } else if (wireMessage.startsWith("handle")) {
            return RegisterHandle.deserialize(wireMessage);
        } else if (wireMessage.startsWith("disconnect")) {
            return Disconnect.deserialize(wireMessage);
        } else {
            return GetUsers.deserialize(wireMessage);
        }
    }
}
