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
        
        public static OnlineUserList deserialize(String wireMessage) {
            return new OnlineUserList(Util.deserializeList(Util.removeTag(wireMessage))); 
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
        
        private static String combine(String[] components, int startInd) {
            String combo = components[startInd];
            for (int i = startInd + 1; i < components.length; i++) {
                combo += SEPARATOR + components[i];
            }
            return combo;
        }
        
        public static NormalAction deserialize(String wireMessage) {
            String textMessage = null;
            List<String> usersToAdd = null;
            List<String> currentUsers = null;
            NormalAction.ActionType at = NormalAction.ActionType.EXIT_CONV;
            String[] components = wireMessage.split(SEPARATOR, -1);
            if (components[3].startsWith("text")) {
                textMessage = Util.removeTag(combine(components, 3));
                at = NormalAction.ActionType.TEXT_MESSAGE;
            } else if (components[3].startsWith("add")) {
                usersToAdd = Util.deserializeList(
                        Util.removeTag(combine(components, 3)));
                at = NormalAction.ActionType.ADD_USER;
            } else if (components[3].startsWith("current")) {
                currentUsers = Util.deserializeList(Util.removeTag(components[3]));
                usersToAdd = Util.deserializeList(Util.removeTag(
                        combine(components, 4)));
                at = NormalAction.ActionType.ADD_USER;
            }
            return new NormalAction(Long.parseLong(components[1]), 
                    components[2], at, usersToAdd, currentUsers, textMessage);       
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
            case ADD_USER: actionPart = ((currentUsers == null) ? "" : "current: " + 
                    Util.serializeCollection(currentUsers) + SEPARATOR) + 
                    "add: " + Util.serializeCollection(handles);
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
        
        public static AvailabilityInfo deserialize(String wireMessage) {
            return new AvailabilityInfo(Util.removeTag(wireMessage), 
                    wireMessage.startsWith("online") ? Status.ONLINE : 
                        Status.OFFLINE);
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
        
        public static BadHandle deserialize(String wireMessage) {
            return new BadHandle(Util.removeTag(wireMessage));
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
        
        public static HandleClaimed deserialize(String wireMessage) {
            return new HandleClaimed(Util.removeTag(wireMessage));
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
        
        public static ReturnId deserialize(String wireMessage) {
            return new ReturnId(Long.parseLong(Util.removeTag(wireMessage)));
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
    
    public static SMessage deserialize(String wireMessage) {
        if (wireMessage.startsWith("id")) {
            return ReturnId.deserialize(wireMessage);
        } else if (wireMessage.startsWith("conv")) {
            return NormalAction.deserialize(wireMessage);
        } else if (wireMessage.startsWith("online") || 
                wireMessage.startsWith("offline")) {
            return AvailabilityInfo.deserialize(wireMessage);
        } else if (wireMessage.startsWith("unavailable")) {
            return BadHandle.deserialize(wireMessage);
        } else if (wireMessage.startsWith("claimed")) {
            return HandleClaimed.deserialize(wireMessage);
        } else {
            return OnlineUserList.deserialize(wireMessage);
        }
    }
}
