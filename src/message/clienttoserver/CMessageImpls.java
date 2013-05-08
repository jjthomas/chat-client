package message.clienttoserver;

import message.servertoclient.SMessageImpls;
import message.util.Util;

public class CMessageImpls {
    public static class GetId implements CMessage {
        private static GetId instance = new GetId();
        
        private GetId() {}
        
        public static GetId getInstance() {
            return instance;
        }

        @Override
        public <T> T accept(CMessageVisitor<T> cmv) {
            return cmv.visit(this);
        }
        
        @Override
        public String toString() {
            return "getid";
        }
        
    }
    
    public static class RegisterHandle implements CMessage {
        private String handle;
        
        public RegisterHandle(String handle) {
            this.handle = handle;
        }
        
        public String getHandle() {
            return handle;
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
        private static GetUsers instance = new GetUsers();
        
        private GetUsers() {}
        
        public static GetUsers getInstance() {
            return instance;
        }
        
        @Override
        public <T> T accept(CMessageVisitor<T> cmv) {
            return cmv.visit(this);
        }
        
        @Override
        public String toString() {
            return "getusers";
        }
    }
    
    public static class Disconnect implements CMessage {
        private String handle;
        
        public Disconnect(String handle) {
            this.handle = handle;
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
    
    // TOOD: delegate deserialization to the individual classes?
    public static CMessage deserialize(String wireMessage) {
        if (wireMessage.startsWith("getid")) {
            return GetId.getInstance();
        } else if (wireMessage.startsWith("initial") || 
                wireMessage.startsWith("conv")) {
            return (CMessage) SMessageImpls.deserialize(wireMessage);
        } else if (wireMessage.startsWith("handle")) {
            return new RegisterHandle(Util.removeTag(wireMessage));
        } else if (wireMessage.startsWith("disconnect")) {
            return new Disconnect(Util.removeTag(wireMessage));
        } else {
            return GetUsers.getInstance();
        }
    }
}
