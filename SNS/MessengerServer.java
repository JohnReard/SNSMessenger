import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
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
    public static Map<String, Integer> blacklist = new HashMap<>();
    public static List<Map.Entry<String, Instant>> connectionTimes = new ArrayList<>();
    public static List<MessengerServerThread> clients = new ArrayList<>();
    private static Clock clock = Clock.systemDefaultZone();

    
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
        String ip = clientsocket.getInetAddress().getHostAddress();
        Instant now = clock.instant();
        for(Map.Entry<String, Instant> time : connectionTimes){
            System.out.println("connection time: "+ connectionTimes);
            if(time.getKey().equals(ip)){
                if(Math.abs(Duration.between(time.getValue(), now).toMillis()) < 100){
                    blacklist.put(ip, 1);
                    System.out.println("Blacklisting IP: " + ip);
                    clientsocket.close();
                }
            }
        }
        if(blacklist.containsKey(ip)){
            clientsocket.close();
            System.out.println("Connection from blacklisted IP: " + ip);
        }
        else{
            
        if(thread==null){
        
        MessengerServerThread thread = new MessengerServerThread(clientsocket);
        clients.add(thread);
        thread.start();
        }
        }
        connectionTimes.add(Map.entry(ip, now));

    }
    }

}
