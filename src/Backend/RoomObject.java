package Backend;

import java.io.Serializable;
import java.util.ArrayList;


public class RoomObject implements Serializable{
    
    private String roomName;
    private ArrayList<String> userList;     //Liste der Nutzer im Raum
    private ArrayList<String> messageList;  //Liste aller Nachrichten im Raum
    
    public RoomObject(String roomName){
        this.roomName = roomName;
        this.userList = new ArrayList<>();
        this.messageList = new ArrayList<>();
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void addUser(String userName){
        this.userList.add(userName);
    }

    public String getRoomName() {
        return roomName;
    }

    public ArrayList<String> getUserList() {
        return userList;
    }

    public ArrayList<String> getMessageList(){
        return messageList;
    }

    public void setUserList(ArrayList<String> userList){
        this.userList = userList;
    }

    @Override
    public String toString() {
        return this.roomName+"("+userList.size()+"User)";
    }
}
