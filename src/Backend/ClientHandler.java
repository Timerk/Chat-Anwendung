package Backend;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClientHandler implements Runnable{

    public static List<ClientHandler>      clientHandlers = Collections.synchronizedList(new ArrayList<>());
    public static List<RoomObject>         roomObjects    = Collections.synchronizedList(new ArrayList<>());
    public static List<String>             activeUsers    = new ArrayList<>();
    public static HashMap<String,String>   bannedUser     = new HashMap<>();
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String username;
    private String password;
    private static ObservableHashMap<String,String> userdata = new ObservableHashMap<>();
    private InputStream in;
    private OutputStream out;
    private BufferedReader bufferedFileReader;
    private FileWriter userFileWriter;
    private FileWriter protocolFileWriter;
  
    public ClientHandler(Socket socket){
        try {
            this.socket=socket;
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
            this.objectOutputStream = new  ObjectOutputStream(out);
            this.objectInputStream = new ObjectInputStream(in);
            this.protocolFileWriter = new FileWriter("src\\UserData\\Protocol.txt", true); 

            clientHandlers.add(this);

            if(roomObjects.size() > 0){
                sendAllRooms();
            }

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    //Laden der Nutzerdaten aus der UserData.txt Datei
    public void loadUserData(){
        try {
            File data = new File("src\\UserData\\UserData.txt");
            if(data.exists()){            
                this.bufferedFileReader = new BufferedReader(new FileReader(data));
                String line;
                while ((line = this.bufferedFileReader.readLine()) != null) {
                    String [] parts = line.split(":");
                    if(parts.length == 2){
                        String left = parts[0];
                        String right = parts[1];
                        if(!userdata.containsKey(left)){
                            userdata.put(left, right);
                        }
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //Laden der Ausgeschlossen Nutzer aus der BannList.txt Datei
    public void loadBannedUsers(){
        try{
            File data = new File("src\\UserData\\BannList.txt");
            if (data.exists()){
                this.bufferedFileReader = new BufferedReader(new FileReader(data));
                String line;
                while ((line = bufferedFileReader.readLine()) != null) {
                    String [] parts = line.split(":");
                    if (parts.length == 2) {
                        String left = parts[0];
                        String right = parts[1];
                        if (!bannedUser.containsKey(left)) {
                            bannedUser.put(left,right);
                        }
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //Sende aller momentan aktiven Nutzer an einen Clietn
    public void sendActiveUsers(){
        if(clientHandlers.size() > 1){
            for (ClientHandler clientHandler : clientHandlers) {
                try {
                    if(clientHandler.username.equals(this.username)){
                        for (String user : activeUsers) {
                            if (!user.equals(this.username)) {
                                UserAction userToSend = new UserAction(user);
                                userToSend.setUserAdded(true);;

                                clientHandler.objectOutputStream.writeObject(userToSend);
                            }
                        }
                    
                    }else{
                        UserAction userToSend = new UserAction(this.username);
                        userToSend.setUserAdded(true);

                        clientHandler.objectOutputStream.writeObject(userToSend);
                    }
                }catch(IOException e){
                    closeEverything(socket, objectOutputStream, objectInputStream);
                }   
            }
        }
    }

    public void userQuit(String user){
        for (ClientHandler clientHandler : clientHandlers) {
            if(!clientHandler.username.equals(this.username)){
                UserAction userQuit = new UserAction(user);
                userQuit.setUserRemoved(true);
                try {
                    clientHandler.objectOutputStream.writeObject(userQuit);
                } catch (IOException e) {
                    closeEverything(socket, objectOutputStream, objectInputStream);
                }
            }
        }
    }

    //Verarbeitung eingehender Nachrichten 
    @Override
    public void run(){
        Object objectFromClient;
        TextMessage textMessage;
        UserAction userAction;
        RoomAction roomAction;
        FileMessage fileMessage;
        LoginMessage loginMessage;
        while (socket.isConnected()) {
            try {
                objectFromClient = objectInputStream.readObject();
                if(objectFromClient.getClass().equals(TextMessage.class)){
                    textMessage = (TextMessage) objectFromClient;
                    broadcastMessage(textMessage);
                }else if(objectFromClient.getClass().equals(UserAction.class)){
                    userAction = (UserAction) objectFromClient;
                }else if(objectFromClient.getClass().equals(RoomAction.class)){
                    roomAction = (RoomAction) objectFromClient;
                    respondToRoomRequest(roomAction);
                }else if(objectFromClient.getClass().equals(FileMessage.class)){
                    fileMessage = (FileMessage) objectFromClient;
                    broadcastMessage(fileMessage);
                }else{
                    loginMessage = (LoginMessage) objectFromClient;
                    processLogin(loginMessage);
                }
            }catch (IOException e) {
                closeEverything(socket,objectOutputStream,objectInputStream);
                break;
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    //Verarbeitung einer Loginanfrage
    private void processLogin(LoginMessage loginMessage){
        activityProtocol(loginMessage, loginMessage.getLoginData()[0]);
        if (bannedUser.containsKey(loginMessage.getLoginData()[0])) {                                                       //Überprüfung ob Nutzer Temporär/Permanent Ausgeschlossen ist
            UserAction clientUpdate;
            if(bannedUser.get(loginMessage.getLoginData()[0]).equals("perm")){
               clientUpdate = new UserAction(loginMessage.getLoginData()[0], true, true, false);
            }else{
               clientUpdate = new UserAction(loginMessage.getLoginData()[0], true, false, true);
            }
            try {
                objectOutputStream.writeObject(clientUpdate);                                                              //Versenden der Serverantwort
                objectOutputStream.flush(); 
            } catch (IOException e) {
                closeEverything(socket, objectOutputStream, objectInputStream);
            }
        }else{
            LoginMessage clientUpdate = new LoginMessage(checkLogin(loginMessage.getLoginData()[0], loginMessage.getLoginData()[1]));  //Erstellung der Serverantowrt
            activityProtocol(clientUpdate, loginMessage.getLoginData()[0]);
            if (checkLogin(loginMessage.getLoginData()[0], loginMessage.getLoginData()[1])) {   
                if (!activeUsers.contains(loginMessage.getLoginData()[0])) {                                             //bei korekter Anmeldung hizufügen zu aktiven Benutzern
                    activeUsers.add(loginMessage.getLoginData()[0]); 
                }
                sendActiveUsers();
            }
            try { 
                objectOutputStream.writeObject(clientUpdate);                                                            //versenden der Serverantwort
                objectOutputStream.flush(); 
            } catch (IOException e) {
                closeEverything(socket, objectOutputStream, objectInputStream);
            }
        }
    }

    //Überprüfung der Logindaten
    public boolean checkLogin(String username, String password){
        //Anlegen eines neues Nutzer
        if (!userdata.containsKey(username)) {
            userdata.put(username, password);
            this.username = username;
            this.password = password;
            try {
                this.userFileWriter = new FileWriter("src\\UserData\\UserData.txt", true);
                userFileWriter.append(this.username+":"+this.password+"\n");
                userFileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        //anbgleich der Daten mit bestehenden Daten
        }else{
            if (userdata.get(username).equals(password)) {
                this.username = username;
                this.password = password;
                return true;
            }else{
                return false;
            }
        }
    }

    //Versendung von Nachrichten an andere Clients
    public synchronized <T extends Message> void broadcastMessage(T messageToSend){
        for(ClientHandler clientHandler:clientHandlers){
            try {
                if(messageToSend instanceof TextMessage && !clientHandler.username.equals(this.username)){
                    TextMessage textMessage = (TextMessage) messageToSend;
                    if(!textMessage.isPrivateMessage()){
                        activityProtocol(textMessage, clientHandler.username);
                        clientHandler.objectOutputStream.writeObject((TextMessage) messageToSend);
                    }else if(clientHandler.username.equals(textMessage.getUserToSend())){
                        activityProtocol(textMessage, clientHandler.username);
                        clientHandler.objectOutputStream.writeObject((TextMessage) messageToSend);
                    }
                }else if(messageToSend instanceof RoomAction){
                    RoomAction roomAction = (RoomAction) messageToSend;
                    if(roomAction.UserAdded() && !clientHandler.username.equals(this.username)){
                        clientHandler.objectOutputStream.writeObject((RoomAction) messageToSend);
                    }else if(!roomAction.UserAdded()){
                        clientHandler.objectOutputStream.writeObject((RoomAction) messageToSend);
                    }
                }else if (messageToSend instanceof FileMessage) {
                    FileMessage fileMessage = (FileMessage) messageToSend;

                    if(clientHandler.username.equals(fileMessage.getUserToSend())){
                        activityProtocol(fileMessage, clientHandler.username);
                        clientHandler.objectOutputStream.writeObject(fileMessage);
                    }
                }else if(messageToSend instanceof UserAction && !clientHandler.username.equals(this.username)){
                    UserAction userAction = (UserAction) messageToSend;
                    if(userAction.userAdded()){
                        clientHandler.objectOutputStream.writeObject(messageToSend);                        
                    }
                }
            } catch (IOException e) {
                closeEverything(socket, objectOutputStream, objectInputStream);
            }
        }
    }

    //Verarbeitung von Raumbeitrittsanfrage 
    private void respondToRoomRequest(RoomAction roomRequest){

        roomRequest.setJoinApproved(true);
        roomRequest.setRoomResponse(true);

        RoomAction messageToClients = new RoomAction(true, this.username);
        messageToClients.setRoomName(roomRequest.getRoomName());
        broadcastMessage(messageToClients);

        synchronized (roomObjects) {
            for (RoomObject roomObject : roomObjects) {
                if (roomRequest.getRoomName().equals(roomObject.getRoomName())) {
                    synchronized(roomObject){
                        roomObject.addUser(this.username);
                    }

                }
            }
        }
        try {
            this.objectOutputStream.writeObject(roomRequest);
            this.objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Übermittlung aller bestehenden Räume an Neuen Client
    public void sendAllRooms(){
        for (RoomObject roomObject : roomObjects) {
            try {
                this.objectOutputStream.writeObject(new RoomAction(roomObject));
            } catch (IOException e) {
                closeEverything(socket, objectOutputStream, objectInputStream);
            }
        }
    }

    //Speicherung von Informationen für das Serverlog in Protocol.txt
    private<T extends Message> void activityProtocol(T messageReceived, String user){
        LocalTime currentTime = LocalTime.now();
        int hours = currentTime.getHour();
        int minute = currentTime.getMinute();
        String time = "["+hours+":"+minute+"]";
        StringBuilder activity = new StringBuilder(time);
        if (messageReceived instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) messageReceived;
            activity.append(" --> ");
            activity.append(user);
            activity.append(" [");
            activity.append("ICOMING_MESSAGE ");
            activity.append(textMessage.getMessageToSend());
            activity.append("]\n");
        }else if (messageReceived instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) messageReceived; 
            activity.append(fileMessage.getUserToSend());
            activity.append(" --> ");
            activity.append(user);
            activity.append(" [");
            activity.append("FILE: ");
            activity.append(fileMessage.getFileName());
            activity.append("]\n");
        }else if(messageReceived instanceof LoginMessage){
            LoginMessage loginMessage = (LoginMessage) messageReceived;
            if (loginMessage.getLoginData() != null) {
                activity.append(" --> SERVER");
                activity.append(" [");
                activity.append("LOGIN_REQUEST: ");
                activity.append("FOR USER:");
                activity.append(loginMessage.getLoginData()[0]);
                activity.append("]\n");   
            }else{
                activity.append(" <-- ");
                activity.append(user);
                activity.append(" [");
                activity.append("LOGIN_RESPONSE ");
                activity.append(loginMessage.vaildLogin());
                activity.append("]\n");            
            }    
        }
        try {
            protocolFileWriter.append(activity);
            protocolFileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public static ObservableHashMap<String, String> getUserdata() {
        return userdata;
    }

    public String getUserName(){
        return this.username;
    }

    public ObjectOutputStream getOjectOutputStream(){
        return this.objectOutputStream;
    }

    public void removeClientHandler(){
        activeUsers.remove(this.username);
        clientHandlers.remove(this);
        userQuit(this.username);
        broadcastMessage(new TextMessage(username+" hat den Chat verlassen !"));
    }

    public void closeEverything(Socket socket,ObjectOutputStream objectOutputStream,ObjectInputStream objectInputStream){
        removeClientHandler();
        try {
            if(objectInputStream != null){
                objectInputStream.close();
            }
            if(objectOutputStream !=null){
                objectOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


}


    

