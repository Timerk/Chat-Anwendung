package Backend;

import java.io.Serializable;

public class FileMessage extends Message implements Serializable{

    private String fileName;
    private String userToSend;
    private String fromUser;
    private byte[] fileData;

    public FileMessage(String fileName, String userToSend, byte[] fileData, String formUser){
        this.fileName = fileName;
        this.fileData = fileData;
        this.userToSend = userToSend;
        this.fromUser = formUser;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUserToSend() {
        return userToSend;
    }

    public String getFromUser() {
        return fromUser;
    }
}
