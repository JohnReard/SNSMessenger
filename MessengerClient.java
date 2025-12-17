import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MessengerClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 2000)) {

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            Scanner scanner = new Scanner(System.in);

            // Registration prompt from server
            String regPrompt = in.readLine();
            if (regPrompt == null) {
                System.out.println("Server disconnected.");
                return;
            }
            System.out.println("Server: " + decrypt(regPrompt));

            System.out.print("Create username: ");
            String newUser = scanner.nextLine();
            System.out.print("Create password: ");
            String newPass = scanner.nextLine();

            send(out, newUser);
            send(out, newPass);

            String regReply = in.readLine();
            if (regReply == null) {
                System.out.println("Server disconnected after registration.");
                return;
            }
            System.out.println("Server: " + decrypt(regReply));

            // Login loop
            while (true) {
                System.out.print("Login username: ");
                String u = scanner.nextLine();
                System.out.print("Login password: ");
                String p = scanner.nextLine();

                send(out, u);
                send(out, p);

                String resultLine = in.readLine();
                if (resultLine == null) {
                    System.out.println("Server disconnected during login.");
                    return;
                }

                String result = decrypt(resultLine);
                if ("LOGIN_OK".equals(result)) {
                    System.out.println("Logged in!");
                    break;
                } else {
                    System.out.println("Invalid username or password. Try again.");
                }
            }

            String afterLogin = in.readLine();
            if (afterLogin == null) {
                System.out.println("Server disconnected after login.");
                return;
            }
            System.out.println("Server: " + decrypt(afterLogin));

            // Chat loop (merged fix )
            while (true) {
                System.out.print("You: ");
                String msg = scanner.nextLine();

                // send message
                send(out, msg);

                // read server response every time
                String replyLine = in.readLine();
                if (replyLine == null) {
                    System.out.println("Server disconnected.");
                    break;
                }

                String reply = decrypt(replyLine);

                // If server rejected us (rate limit / blacklist / too long), stop cleanly
                if (reply.startsWith("ERR")) {
                    System.out.println("Server: " + reply);
                    break;
                }


                // System.out.println("Server: " + reply); // usually "DELIVERED"

                if ("exit".equals(msg)) break;
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e);
        }
    }

    private static void send(BufferedWriter out, String plain) throws IOException {
        out.write(encrypt(plain));
        out.newLine();
        out.flush();
    }

    // Replace with your encryption/decryption
    private static String encrypt(String s) { return s; }
    private static String decrypt(String s) { return s; }
}