package message.servertoclient;

import java.util.Collection;
import java.util.List;

import message.clienttoserver.CMessage;
import message.clienttoserver.CMessageVisitor;
import message.util.Util;

public class SMessageImpls {
    
    public static final String SEPARATOR = "/";
    public static class OnlineUserList implements SMessage {
        private Collection<String> handles;
        
        public OnlineUserList(Collection<String> handles) {
            this.handles = handles;
        }
        
        public Collection<String> getHandles() {
            return handles;
        }

        @Override
        public <T> T accept(SMessageVisitor<T> smv) {
            return smv.visit(this);
        }
        
        @Override
        public String toString() {
            return "users: " + Util.serializeCollection(handles);
        }
    }
    
    public static class NormalAction implements SMessage, CMessage {
        private long id;
        private String senderHandle;
        private ActionType at;
        private List<String> handles;
        private List<String> currentUsers;
        private String textMessage;
        
        public static enum ActionType {
            TEXT_MESSAGE, EXIT_CONV, ADD_USER;
        }
        
        public NormalAction(long id, String senderHandle, ActionType at, 
                List<String> handles, List<String> currentUsers, 
                String textMessage) {
            this.id = id;
            this.senderHandle = senderHandle;
            this.at = at;
            this.handles = handles;
            this.currentUsers = currentUsers;
            this.textMessage = textMessage;
        }
        
        public long getId() {
            return id;
        }
        
        public String getSenderHandle() {
            return senderHandle;
        }
        
        public ActionType getActionType() {
            return at;
        }
        
        public List<String> getHandles() {
            return handles;
        }
        
        public List<String> getCurrentUsers() {
            return currentUsers;
        }
        
        public String getTextMessage() {
            return textMessage;
        }

        @Override
        public <T> T accept(SMessageVisitor<T> smv) {
            return smv.visit(this);
        }
        
        @Override
        public String toString() {
            String actionPart = null;
            switch(at) {
            case TEXT_MESSAGE: actionPart = "text: " + textMessage; break;
            case EXIT_CONV: actionPart = "exit"; break;
            case ADD_USER: actionPart = "add: " + Util.serializeCollection(handles)
                    + ((currentUsers == null) ? "" : SEPARATOR + "current: " + 
                      Util.serializeCollection(currentUsers));
            }
            return "conv" + SEPARATOR + id + SEPARATOR + senderHandle + 
                    SEPARATOR + actionPart;
        }

        @Override
        public <T> T accept(CMessageVisitor<T> cmv) {
            return cmv.visit(this);
        }
    }
    
    public static class AvailabilityInfo implements SMessage {
        private String handle;
        private Status s;
        
        public static enum Status {
            OFFLINE, ONLINE;
        }
        
        public AvailabilityInfo(String handle, Status s) {
            this.handle = handle;
            this.s = s;
        }
        
        public String getHandle() {
            return handle;
        }
        
        public Status getStatus() {
            return s;
        }

        @Override
        public <T> T accept(SMessageVisitor<T> smv) {
            return smv.visit(this);
        }
        
        @Override
        public String toString() {
            String tag = null;
            switch(s) {
            case OFFLINE: tag = "offline: "; break;
            case ONLINE: tag = "online: "; break;
            }
            return tag + handle;
        }
    }
    
    public static class BadHandle implements SMessage {
        private String handle;
        
        public BadHandle(String handle) {
            this.handle = handle;
        }
        
        public String getHandle() {
            return handle;
        }

        @Override
        public <T> T accept(SMessageVisitor<T> smv) {
            return smv.visit(this);
        }
        
        @Override
        public String toString() {
            return "unavailable: " + handle;
        }
    }
    
    public static class HandleClaimed implements SMessage {
        private String handle;
        
        public HandleClaimed(String handle) {
            this.handle = handle;
        }
        
        public String getHandle() {
            return handle;
        }
        
        @Override
        public <T> T accept(SMessageVisitor<T> smv) {
            return smv.visit(this);
        }
        
        @Override
        public String toString() {
            return "claimed: " + handle;
        }
        
    }
    
    public static class ReturnId implements SMessage {
        long id;
        
        public ReturnId(long id) {
            this.id = id;
        }
        
        public long getId() {
            return id;
        }
        
        @Override
        public <T> T accept(SMessageVisitor<T> smv) {
            return smv.visit(this);
        }
        
        @Override
        public String toString() {
            return "id: " + id;
        }
    }
    
    // TOOD: delegate deserialization to the individual classes?
    public static SMessage deserialize(String wireMessage) {
        String[] components = wireMessage.split(SEPARATOR);
        if (components[0].startsWith("id")) {
            return new ReturnId(Long.parseLong(
                    Util.removeTag(components[0])));
        } else if (components[0].startsWith("conv")) {
            String textMessage = null;
            List<String> usersToAdd = null;
            List<String> currentUsers = null;
            NormalAction.ActionType at = NormalAction.ActionType.EXIT_CONV;
            if (components[3].startsWith("text")) {
                textMessage = Util.removeTag(components[3]);
                at = NormalAction.ActionType.TEXT_MESSAGE;
            } else if (components[3].startsWith("add")) {
                usersToAdd = Util.deserializeList(
                        Util.removeTag(components[3]));
                at = NormalAction.ActionType.ADD_USER;
                if (components.length == 5) {
                    currentUsers = Util.deserializeList(Util.removeTag(
                            components[4]));
                }
            }
            return new NormalAction(Long.parseLong(components[1]), 
                    components[2], at, usersToAdd, currentUsers, textMessage);
        } else if (components[0].startsWith("online")) {
            return new AvailabilityInfo(Util.removeTag(components[0]), 
                    AvailabilityInfo.Status.ONLINE);
        } else if (components[0].startsWith("offline")) {
            return new AvailabilityInfo(Util.removeTag(components[0]), 
                    AvailabilityInfo.Status.OFFLINE);
        } else if (components[0].startsWith("unavailable")) {
            return new BadHandle(Util.removeTag(components[0]));
        } else {
            return new OnlineUserList(Util.deserializeList(
                    Util.removeTag(components[0])));
        }
    }
}
