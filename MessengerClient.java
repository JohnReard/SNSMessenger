import java.io.*;
import java.net.*;
public class MessengerClient {
    public static String sendMessage(DataInputStream input){
        try {
            String output = input.toString();
            return output;
            
        } catch (Exception e) {
            return "Error";
        }
    }
    public static void main(String[] args) {
        int portnumber = 0;
        try {
            DatagramSocket socket =   socket = new DatagramSocket(portnumber);
        } catch (Exception e) {
        }
        
    }
}
