import java.io.BufferedReader;
import java.io.IOException;
import javax.net.ssl.SSLSocket;


public class MessengerClientThread extends Thread {
    private SSLSocket stringSocket;
    private BufferedReader in;
    String servermessage;

    public MessengerClientThread(SSLSocket stringSocket, BufferedReader in) {
        this.stringSocket = stringSocket;
        this.in = in;
    }

   //public void listenServerMessage(){
    //new Thread(new Runnable(){
    //@Override
    public void run(){

        while(true){
            try{
            servermessage = in.readLine();
            if(!stringSocket.isConnected() || servermessage == null){
                break;
            }
            else{
                System.out.println(servermessage);
            }
            }
            catch(IOException e){
                System.out.println("Listening to server exception");
                break;
            }    
        }
}
}