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
        String[] components = wireMessage.split(SEPARATOR);
        if (components[0].startsWith("getid")) {
            return new GetId(components[1]);
        } else if (components[0].startsWith("initial") || 
                components[0].startsWith("conv")) {
            return (CMessage) SMessageImpls.deserialize(wireMessage);
        } else if (components[0].startsWith("handle")) {
            return new RegisterHandle(Util.removeTag(components[0]), components[1]);
        } else if (components[0].startsWith("disconnect")) {
            return new Disconnect(Util.removeTag(components[0]));
        } else {
            return new GetUsers(components[1]);
        }
    }
}
