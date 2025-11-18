import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
public class MessengerServer {
    public static void main(String[] args) {
        try{
        ServerSocket serverSocket = new ServerSocket(2000);
        Socket socket = serverSocket.accept();

       
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while(true){
                String message = in.readLine();
                out.write(message);
                out.newLine();
                System.out.println("Client says:" + message);
                if(message.equals("exit")){
                    break;
                }
                socket.close();
                serverSocket.close();
                in.close();
                out.close();

        }
        } catch (IOException e){
            System.out.println("Error");
        }

    }
    
} 
