import java.io.IOException;
import java.util.*;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class MessengerServer {
    public static SSLServerSocket stringSocket;
    public static final int portNumber = 2000;
    public static final String KEYSTORE_LOCATION = "C:\\Keys\\ServerKeyStore.jks";
    public static final String KEYSTORE_PASSWORD = "password";
    public static MessengerServerThread thread;
    public static List<MessengerServerThread> clients = new ArrayList<>();
    public static void main(String[] args) throws IOException {
    try{
        System.setProperty("javax.net.ssl.keyStore", KEYSTORE_LOCATION);
        System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);
        ServerSocketFactory socketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        stringSocket = (SSLServerSocket) socketfactory.createServerSocket(portNumber);
    }
    catch(IOException e){
        System.out.println("Socket exception.");

    }
    while(true){
        SSLSocket clientsocket = (SSLSocket) stringSocket.accept();
        int i = 0;
        if(thread==null){
        MessengerServerThread thread = new MessengerServerThread(clientsocket);
        System.out.println("i = : "+ i);
        clients.add(thread);
        thread.start();
        i = i + 1;
        }

    }
    }

}
