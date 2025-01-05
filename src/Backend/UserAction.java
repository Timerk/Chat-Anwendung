package Backend;

import java.io.Serializable;

public class UserAction extends Message implements Serializable{
    private String  userToSend;
    private boolean userAdded;
    private boolean userRemoved;
    private boolean bannRequest;
    private boolean isPerm;
    private boolean isTemp;
    private String  warningMessage;
    private String  roomName;

    public UserAction(String userToSend){
        this.userToSend = userToSend;
    }

    public UserAction(String userToSend, boolean bannRequest, boolean isPerm, boolean isTemp){
        this.userToSend = userToSend;
        this.bannRequest = bannRequest;
        this.isPerm = isPerm;
        this.isTemp = isTemp;
    }

    public void setBannRequest(boolean bannRequest) {
        this.bannRequest = bannRequest;
    }

    public void setUserAdded(boolean userAdded) {
        this.userAdded = userAdded;
    }

    public String getUserToSend() {
        return userToSend;
    }

    public boolean userAdded(){
        return userAdded;
    }

    public boolean bannRequest(){
        return bannRequest;
    }

    public void setUserRemoved(boolean userRemoved) {
        this.userRemoved = userRemoved;
    }

    public boolean userRemoved(){
        return this.userRemoved;
    }
    
    public boolean isPerm() {
        return isPerm;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }
}
