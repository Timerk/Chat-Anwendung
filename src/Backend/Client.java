package Backend;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;


public class Client {
    
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String username;
    private String password;

    public Client(Socket socket){
        try {
            this.socket=socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            
        } catch (IOException e) {
            closeEverything(socket , objectOutputStream,objectInputStream);
        }
    }

    public void setUserData(String username, String password){
        this.username = username;
        this.password = password;
    }

    //Versenden von Nachrichten an den Server
    public <T extends Message> void sendMessage(T messageToSend){
        try {
            if(socket.isConnected()){
                if (messageToSend instanceof TextMessage) {
                    LocalTime currentTime = LocalTime.now();
                    int hours = currentTime.getHour();
                    int minute = currentTime.getMinute();
                    String time = "["+hours+":"+minute+"]";
                    TextMessage textMessage = (TextMessage) messageToSend;
                    textMessage.setMessageToSend(username +": "+textMessage.getMessageToSend()+time);
                    this.objectOutputStream.writeObject(textMessage);
                    this.objectOutputStream.flush();                    
                }else{
                    this.objectOutputStream.writeObject(messageToSend);
                    this.objectOutputStream.flush();
                }
            }
        } catch (IOException e) {
            closeEverything(socket , objectOutputStream,objectInputStream);
        }
    }

    //Hören auf Nachtichten vom Server
    public void listenForMessage(MessageListener listener){
        new Thread(new Runnable() {
            @Override
            public void run(){
                while (socket.isConnected()) {
                    try {
                        listener.MessageReceived(objectInputStream.readObject());         //Implementation der MessageReceived Methode erfolgt in der Methode porcessMessages in der Klasse ClientGui 
                    } catch (IOException | ClassNotFoundException e) {
                        closeEverything(socket, objectOutputStream, objectInputStream);
                        break;
                    }
                }
            }
        }).start();
    }


    public boolean isConnected(){
        return socket.isConnected();
    }

    public String getUserName(){
        return this.username;
    }

    public void closeEverything(Socket socket ,ObjectOutputStream objectOutputStream,ObjectInputStream objectInputStream){
        try {
            if(objectOutputStream != null){
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectOutputStream.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

