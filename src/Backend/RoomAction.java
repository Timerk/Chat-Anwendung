package Backend;

import java.io.Serializable;

public class RoomAction extends Message implements Serializable{
    
    private RoomObject roomToSend;
    private String roomName;
    private boolean isRemoved;
    private boolean nameChanged;
    private int index;
    private boolean joinApproved;
    private String userJoined;
    private boolean isRoomResponse;
    private boolean isRoomRequest;
    private boolean userAdded;

    public RoomAction(RoomObject roomToSend){
        this.roomToSend = roomToSend;
    }

    public RoomAction(String roomName){
        this.roomName = roomName;
    }

    public RoomAction(boolean userAdded, String userJoined){
        this.userAdded = userAdded;
        this.userJoined = userJoined;
    }

    public RoomObject getRoomToSend() {
        return roomToSend;
    }

    public void setNameChanged(boolean nameChanged) {
        this.nameChanged = nameChanged;
    }

    public void setRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public boolean NameChanged() {
        return nameChanged;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setJoinApproved(boolean joinApproved) {
        this.joinApproved = joinApproved;
    }

    public boolean joinApproved(){
        return joinApproved;
    }

    public void setUserJoined(String userJoined) {
        this.userJoined = userJoined;
    }

    public String getUserJoined() {
        return userJoined;
    }

    public void setRoomResponse(boolean isRoomResopnse) {
        this.isRoomResponse = isRoomResopnse;
    }

    public boolean isRoomResponse() {
        return isRoomResponse;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomRequest(boolean isRoomRequest) {
        this.isRoomRequest = isRoomRequest;
    }
    
    public boolean isRoomRequest() {
        return isRoomRequest;
    }
    
    public void setUserAdded(boolean userAdded) {
        this.userAdded = userAdded;
    }

    public boolean UserAdded(){
        return this.userAdded;
    }
}
