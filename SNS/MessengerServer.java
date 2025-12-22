import java.io.IOException;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class MessengerServer {
    public static SSLServerSocket stringSocket;
    public static final int portNumber = 2000;
    public static final String KEYSTORE_LOCATION = "C:\\Keys\\ServerKeyStore.jks";
    public static final String KEYSTORE_PASSWORD = "password";
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
        MessengerServerThread thread = new MessengerServerThread(stringSocket);
        thread.start();
    }
    }

}
