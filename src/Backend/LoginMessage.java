package Backend;

import java.io.Serializable;

public class LoginMessage extends Message implements Serializable{
    
    private String username;
    private String password;
    private boolean vaildLogin;

    public LoginMessage(String username, String password){
        this.username = username;
        this.password = password;
    }

    public LoginMessage(boolean vaildLogin){
        this.vaildLogin = vaildLogin;
    }
    
    public String [] getLoginData(){
        String [] data = {this.username, this.password};
        return data;
    }

    public boolean vaildLogin(){
        return this.vaildLogin;
    }
}
