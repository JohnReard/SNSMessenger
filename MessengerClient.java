import java.io.*;
import java.net.*;
import java.util.Scanner;
public class MessengerClient {
    public static String sendMessage(DataInputStream input){
        try {
            String output = input.toString();
            return output;
            
        } catch (Exception e) {
            return "Error";
        }
    }
    public static void main(String[] args)throws IOException {
        String hostName = "localhost"; //args[0];
        int portNumber = 2000; //Integer.parseInt(args[1]);

        try {
            Socket stringSocket = new Socket(hostName, portNumber);
            BufferedReader in = new BufferedReader(new InputStreamReader(stringSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stringSocket.getOutputStream()));

            Scanner scanner = new Scanner(System.in);

            while (true){
                String message = scanner.nextLine();
                out.write(message);
                out.newLine();
                out.flush(); 
                if(message.equals("exit")){
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Error");
    }
}
}
