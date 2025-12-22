import java.io.*;
import java.net.Socket;
import java.util.*;
import javax.net.ssl.*;
//password for key store is password, alias password is password, local city is Birmingham, alias is Server
//For CA: PEM pass phrase is password country name is uk state or province name is brm challenge password is password
// got to step 11.
public class MessengerServerThread extends Thread {
    public static Map<String,String> userandpass = new HashMap<>();
    public static Boolean loggedin = false;
    public static String loggedinusername;
    public static final boolean DEBUG = true;
    public String clientmessage;
    
    public SSLServerSocket stringSocket;
    
    
    public MessengerServerThread(SSLServerSocket stringSocket){
        super();
        this.stringSocket = stringSocket;
        }
        public void run(){
        alloops:
        while(true){
        try{
        //SSLServerSocket objectSocket = (SSLServerSocket) socketfactory.createServerSocket(portNumber);
        stringSocket.setEnabledProtocols(new String[]{"TLSv1.3", "TLSv1.2"});
        //objectSocket.setEnabledProtocols(new String[]{"TLSv1.3", "TLSv1.2"});
        //Socket acceptedobjectSocket = objectSocket.accept();
        Socket acceptedsocket = stringSocket.accept();
       
        BufferedReader in = new BufferedReader(new InputStreamReader(acceptedsocket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(acceptedsocket.getOutputStream()));
        //ObjectOutputStream objout = new ObjectOutputStream(acceptedobjectSocket.getOutputStream());
        //ObjectInputStream objin = new ObjectInputStream(acceptedobjectSocket.getInputStream());
        String username = null;
        String password = null;
        String accountresponse = null;
        String message = null;

        //Asks user if they have an account
        while(!loggedin){

            System.out.println("Not in any if or switch");
            if (accountresponse == null){
                System.out.println("ACCOUNT RESPONSE IS NULL");
                out.flush();
                out.write("Do you have an account? y/n:");
                out.newLine();
                out.flush();
                accountresponse = in.readLine();
                //accountresponse = in.readLine();
                out.write("you typed:" + accountresponse);
                out.newLine();
                System.out.println("Account response for do you have an acc: " + accountresponse);}
            //If they do input user and pass
            while(accountresponse != null){
                System.out.println("loop checking ACCOUNT RESPONSE IS NOT NULL");
                System.out.println(accountresponse);
                System.out.println("Account response is equal to:" + accountresponse.equals("n"));
                System.out.println("Account response is equal to:" + accountresponse.equals("y"));
                if(accountresponse.equals("y")){
                    out.write("Please enter your username:");
                    out.newLine();
                    out.flush();
                    System.out.println(userandpass);
                    username = in.readLine();
                    System.out.println(username);
                    System.out.println("THIS IS BEING PRINTED" +userandpass.get(username));
                    System.out.println("User and pass map: " + userandpass);
                    if(username!=null){
                        if(userandpass.get(username)!=null){
                            out.write("Please enter your Password");
                            out.newLine();
                            out.flush();
                            password = in.readLine();
                            if(userandpass.get(username).equals(password)){
                                accountresponse = null;
                                loggedin = true;
                                System.out.println("Loggedin is: " + loggedin);
                            }
                            else{
                                out.write("Incorrect password, please try again or exit.");
                                out.newLine();
                                out.flush();
                                username = null;
                                password = null;
                            }
                        }
                        else{
                            out.write("Incorrect username, please try again or exit.");
                            out.newLine();
                            out.flush();
                            username = null;
                            password = null;
                        }
                    }
                }                
                //If they don't, create account
                else if (accountresponse.equals("n")){
                    out.write("Please enter the username you want:");
                    out.newLine();
                    out.flush();
                    username = in.readLine();
                    if(username!=null){
                        out.write("Please enter the password you want:");
                        out.newLine();
                        out.flush();
                        password = in.readLine();
                        System.out.println("user and pass" + username + password);
                        userandpass.put(username,password);
                        out.write("Account created.");
                        out.newLine();
                        out.flush();
                        accountresponse = null;
                    }
                }
                //Invalid accountresponse
                else{
                    out.write("Invalid response, please input only yes or no.");
                    out.newLine();
                    out.flush();
                    accountresponse = null;
                }
            }
        }
        while(loggedin){
                System.out.println("LOGGEDIN");
                if(message != null){
                System.out.println("Server side message is: "+ message);
                out.write(loggedinusername + " says: " + message);
                out.newLine();
                out.flush();
                }
                message = in.readLine();
                System.out.println(message);
                if(message.equals("exit")){
                    acceptedsocket.close();
                    in.close();
                    out.close();
                    loggedin = false;
                    break alloops;
                    //maybe close script somehow?
                }
            }
        }
        catch (IOException e){
            System.out.println("Error:" + e.toString());
        }
        }
    }
    //public void listenClientMessage(){
    //new Thread(new Runnable(){
    //@Override
    //public void run(SSLSocket stringSocket, BufferedReader in){
    //    while(true){
    //        try{
    //        clientmessage = in.readLine();
    //        if(!stringSocket.isConnected()){
    //            break;
    //        }
    //        else{
    //            
    //        }
    //        }
    //        catch(IOException e){
    //            System.out.println("Listening to server exception");
    //            break;
    //        }    
    //    }
    //}}).start();
//}
}

