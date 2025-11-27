import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
public class MessengerServer {
    public static Map<String,String> userandpass = new HashMap<>();
    public static Boolean loggedin = false;
    public static String loggedinusername;

    public static void main(String[] args) {
        while(true){
        try{
        ServerSocket serverSocket = new ServerSocket(2000);
        Socket socket = serverSocket.accept();
       
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            while(true){
                String username = in.readLine();
                if(username!=null){
                    String password = in.readLine();
                    userandpass.put(username,password);
                    break;
                }
                out.write(username);
                break;
            }
            while(true){
                if(loggedin){
                String message = in.readLine();
                out.write(message);
                out.newLine();
                System.out.println(loggedinusername + " says: " + message);
                if(message.equals("exit")){
                    serverSocket.close();
                    serverSocket.close();
                    in.close();
                    out.close();
                    break;
                }
                }
                else{
                    System.out.println("Welcome to SNSMessenger, please enter your username");
                    String inputusername = in.readLine();
                    System.out.println("Now, please enter your password");
                    String inputpassword = in.readLine();
                    if(userandpass.get(inputusername)==inputpassword){
                        System.out.println("You are now logged in.");
                        loggedinusername = inputusername;
                        loggedin = true;
                    }
                    else{
                        System.out.println("Invalid username or password, please try again.");
                    }
                }

        }
        } catch (IOException e){
            System.out.println("Error:" + e.toString());
        }
    }
    }   
} 
