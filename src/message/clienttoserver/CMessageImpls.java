package message.clienttoserver;

import message.servertoclient.SMessageImpls;
import message.util.Util;

public class CMessageImpls {
    public static class GetId implements CMessage {
        private String senderHandle;
        
        private GetId(String senderHandle) {
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
            return "getid\n" + senderHandle;
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
        
        private GetUsers(String senderHandle) {
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
            return "getusers\n" + senderHandle;
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
            return new GetId(wireMessage.substring(wireMessage.indexOf('\n') + 1));
        } else if (wireMessage.startsWith("initial") || 
                wireMessage.startsWith("conv")) {
            return (CMessage) SMessageImpls.deserialize(wireMessage);
        } else if (wireMessage.startsWith("handle")) {
            return new RegisterHandle(Util.removeTag(wireMessage), 
                    wireMessage.substring(wireMessage.indexOf('\n') + 1));
        } else if (wireMessage.startsWith("disconnect")) {
            return new Disconnect(Util.removeTag(wireMessage));
        } else {
            return new GetUsers(wireMessage.substring(wireMessage.indexOf('\n') + 1));
        }
    }
}
