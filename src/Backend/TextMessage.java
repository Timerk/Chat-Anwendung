package Backend;

import java.io.Serializable;

public class TextMessage extends Message implements Serializable{
    
    private String messageToSend;
    private boolean isPrivateMessage;
    private String room;
    private String userToSend;
    private String fromUser;
    
    public TextMessage(String messageToSend){
        this.messageToSend = messageToSend;
        this.isPrivateMessage = false;
    }

    public TextMessage(String messageToSend, String userToSend, String fromUser){
        this.messageToSend = messageToSend;
        this.userToSend    = userToSend;
        this.fromUser      = fromUser;
        this.isPrivateMessage = true;
    }

    public String getMessageToSend() {
        return messageToSend;
    }

    public void setMessageToSend(String messageToSend) {
        this.messageToSend = messageToSend;
    }

    public void setPrivateMessage(boolean isPrivateMessage) {
        this.isPrivateMessage = isPrivateMessage;
    }

    public boolean isPrivateMessage() {
        return isPrivateMessage;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRoom() {
        return room;
    }

    public String getUserToSend() {
        return userToSend;
    }

    public String getFromUser() {
        return fromUser;
    }
}
