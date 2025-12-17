import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessengerServer {

    private static final Map<String, String> users = new ConcurrentHashMap<>();

    // anti-DoS
    private static final Map<String, RateState> ipRate = new ConcurrentHashMap<>();
    private static final Map<String, Long> blacklistUntil = new ConcurrentHashMap<>();

    private static final long WINDOW_MS = 10_000;          // 10 seconds
    private static final int MAX_CONN_PER_WINDOW = 10;     // connections per 10s per IP
    private static final int MAX_MSG_PER_WINDOW  = 50;     // messages per 10s per IP
    private static final long BLACKLIST_MS = 60_000;       // 60 seconds blacklist
    private static final int MAX_MESSAGE_LEN = 20;         // max message size
    private static final int SOCKET_TIMEOUT_MS = 30_000;   // 30 seconds read timeout

    private static class RateState {
        long windowStart = System.currentTimeMillis();
        int connCount = 0;
        int msgCount = 0;
    }

    private static boolean isBlacklisted(String ip) {
        Long until = blacklistUntil.get(ip);
        if (until == null) return false;

        long now = System.currentTimeMillis();
        if (now >= until) {
            blacklistUntil.remove(ip);
            return false;
        }
        return true;
    }

    private static boolean allowConnection(String ip) {
        if (isBlacklisted(ip)) return false;

        RateState st = ipRate.computeIfAbsent(ip, k -> new RateState());
        long now = System.currentTimeMillis();

        synchronized (st) {
            if (now - st.windowStart > WINDOW_MS) {
                st.windowStart = now;
                st.connCount = 0;
                st.msgCount = 0;
            }

            st.connCount++;

            if (st.connCount > MAX_CONN_PER_WINDOW) {
                blacklistUntil.put(ip, now + BLACKLIST_MS);
                System.out.println("[" + ip + "] too many connections -> blacklisted for " + (BLACKLIST_MS / 1000) + "s");
                return false;
            }
            return true;
        }
    }

    private static boolean allowMessage(String ip) {
        if (isBlacklisted(ip)) return false;

        RateState st = ipRate.computeIfAbsent(ip, k -> new RateState());
        long now = System.currentTimeMillis();

        synchronized (st) {
            if (now - st.windowStart > WINDOW_MS) {
                st.windowStart = now;
                st.connCount = 0;
                st.msgCount = 0;
            }

            st.msgCount++;

            if (st.msgCount > MAX_MSG_PER_WINDOW) {
                blacklistUntil.put(ip, now + BLACKLIST_MS);
                System.out.println("[" + ip + "] too many messages -> blacklisted for " + (BLACKLIST_MS / 1000) + "s");
                return false;
            }
            return true;
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(2000)) {
            System.out.println("Server listening on port 2000...");

            // ACCEPT LOOP (needed for Test 5) 
            while (true) {
                Socket socket = serverSocket.accept();
                String ip = socket.getInetAddress().getHostAddress();

                socket.setSoTimeout(SOCKET_TIMEOUT_MS);

                if (!allowConnection(ip)) {
                    System.out.println("Rejected connection from: " + ip);
                    socket.close();
                    continue;
                }

                System.out.println("Client connected: " + ip);

                // handle one client fully, then return to accept next connection
                try {
                    handleClient(socket, ip);
                } catch (SocketTimeoutException e) {
                    System.out.println("[" + ip + "] timed out (idle too long).");
                } catch (IOException e) {
                    System.out.println("[" + ip + "] handler error: " + e);
                } finally {
                    try { socket.close(); } catch (IOException ignored) {}
                    System.out.println("Connection closed: " + ip);
                }
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e);
        }
    }

    private static void handleClient(Socket socket, String ip) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        // 1) Register (prompt-flow)
        send(out, "Welcome to SNSMessenger. Create an account: send username then password.");

        String newUser = decrypt(in.readLine());
        String newPass = decrypt(in.readLine());
        if (newUser == null || newPass == null) return;

        if (users.containsKey(newUser)) {
            send(out, "Username already exists. Restart client and choose another username.");
            return;
        }

        users.put(newUser, sha256(newPass));
        send(out, "Account created. Now login: send username then password.");
        System.out.println("Registered user: " + newUser);

        // 2) Login loop
        boolean loggedIn = false;
        String loggedInUser = null;

        while (!loggedIn) {
            String u = decrypt(in.readLine());
            String p = decrypt(in.readLine());
            if (u == null || p == null) return;

            String storedHash = users.get(u);

            if (storedHash != null && storedHash.equals(sha256(p))) {
                loggedIn = true;
                loggedInUser = u;
                send(out, "LOGIN_OK");
                System.out.println("Logged in: " + loggedInUser);
            } else {
                send(out, "LOGIN_FAIL");
                System.out.println("Failed login attempt for: " + u);
            }
        }

        // 3) Chat loop (prints on server)
        send(out, "You are now logged in. Send messages. Type exit to quit.");

        while (true) {
            if (!allowMessage(ip)) {
                send(out, "ERR Rate limit exceeded. Try again later.");
                break;
            }

            String line = in.readLine();
            if (line == null) break;

            String msg = decrypt(line);

            if (msg.length() > MAX_MESSAGE_LEN) {
                send(out, "ERR Message too long (max " + MAX_MESSAGE_LEN + ")");
                continue;
            }

            System.out.println(loggedInUser + " (" + ip + ") says: " + msg);

            send(out, "DELIVERED");

            if ("exit".equals(msg)) break;
        }
    }

    private static void send(BufferedWriter out, String plain) throws IOException {
        out.write(encrypt(plain));
        out.newLine();
        out.flush();
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Replace with your encryption/decryption
    private static String encrypt(String s) { return s; }
    private static String decrypt(String s) { return s; }
}