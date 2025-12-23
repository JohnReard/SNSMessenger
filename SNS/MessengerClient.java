import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MessengerClient extends Thread {
    public static final String TRUSTSTORE_LOCATION = "C:\\CA\\ServerKeyStore.jks";
    public static final String TRUSTSTORE_PASSWORD = "password";
    public static String servermessage;
    private static String loggedinusername;
    public static void main(String[] args)throws IOException {
        System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE_LOCATION);
        System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASSWORD);
        SSLSocketFactory sslfact = (SSLSocketFactory) SSLSocketFactory.getDefault();
        String hostName = "192.168.1.98"; //args[0];
        int portNumber = 2000; //Integer.parseInt(args[1]);
        String message = null;
       // String loggedinusername;

        try {
            SSLSocket stringSocket = (SSLSocket) sslfact.createSocket(hostName, portNumber);
            //SSLSocket objectSocket = (SSLSocket) sslfact.createSocket(hostName, portNumber);
            stringSocket.startHandshake();
            //objectSocket.startHandshake();
            BufferedReader in = new BufferedReader(new InputStreamReader(stringSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stringSocket.getOutputStream()));
            //ObjectOutputStream objout = new ObjectOutputStream(objectSocket.getOutputStream());
            //ObjectInputStream objin = new ObjectInputStream(objectSocket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            //System.out.println("Welcome to SNSMessenger, please create an account.");
            //System.out.println("First pick a username: ");
            
            MessengerClientThread clientthread = new MessengerClientThread(stringSocket, in);
            clientthread.start();
            while (true){
        
                message = scanner.nextLine();
                out.write(message);
                out.newLine();
                out.flush();

                

                //System.out.println("Message: " + message);
                if(message.equals("exit")){
                    stringSocket.close();
                    break;
                }
        
            }
        } catch (UnknownHostException e) {
            System.out.println("Error connecting to socket");
    }
}
}
