package Backend;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clientHandlersServer;
    private boolean loaded;
    private BufferedReader bufferedFileReader;
    private File file;

    public Server(ServerSocket serverSocket) throws IOException{
        this.serverSocket = serverSocket;
        this.clientHandlersServer = new ArrayList<>();
        this.loaded = false;
        this.file = new File("src\\UserData\\Protocol.txt");
        this.file.createNewFile();
    }

    public ArrayList<ClientHandler> getClientHandlersServer() {
        return clientHandlersServer;
    }

    public void startServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!serverSocket.isClosed()) {
                        Socket socket = serverSocket.accept();                    //Warten auf Verbindungen zum Server
                        System.out.println("Client connected");
                        ClientHandler clientHandler = new ClientHandler(socket);  //Erstellung eines ClientHandler-Objektes für jeden Client welcher eine Verbindung aufbaut

                        Thread thread = new Thread(clientHandler);
                        thread.start();

                        if (!loaded) {
                            clientHandler.loadUserData();                         //Laden der Nuterdaten in den ClientHandler
                            loaded = true;
                        }
                        clientHandler.loadBannedUsers();                          //Laden der BannList.txt in den ClientHandler
                    }
                }catch (IOException e) {
                    closeServer();
                }
            }
        }).start();
    }

    //Warten auf ablauf eines timeouts 
    public void waitForBannEnd(int time, String username){                    
        new Thread(() ->{
            while (true) {
                try {
                    Thread.sleep(time*1000);
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cleanUpBannList(username);    //entfernen des Eintrags aus der BannList.txt nach Ablauf des timeouts
        }).start();
    }

    private synchronized void cleanUpBannList(String username){
        try {
            this.bufferedFileReader = new BufferedReader(new FileReader("src\\UserData\\BannList.txt"));
            StringBuffer buffer  = new StringBuffer();
            String line;

            while ((line = bufferedFileReader.readLine()) != null) {
                String [] parts = line.split(":");
                if(parts.length == 2){;
                    if (!parts[0].equals(username)) {
                        buffer.append(line).append(System.lineSeparator()); 
                    } 
                }
            }
            FileWriter fileWriter = new FileWriter("src\\UserData\\BannList.txt");
            fileWriter.write(buffer.toString());
            fileWriter.close();
            ClientHandler.bannedUser.remove(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    
    public synchronized void removeBann(String user){
        File bannList = new File("src\\UserData\\BannList.txt");
        if(bannList.exists()){
            cleanUpBannList(user);
        }
    }

    public void closeServer(){
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}