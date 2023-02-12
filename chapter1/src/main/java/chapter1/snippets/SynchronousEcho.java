package chapter1.snippets;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SynchronousEcho {

    public static void main(String[] args) throws Throwable {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(3000));

        // The main application thread plays the role of an accepting thread, as it receives socket objects for all new connections.
        // The operation blocks when no connection is pending. A new thread is allocated for each connection.
        while (true) {
            Socket socket = server.accept();
            new Thread(clientHandler(socket)).start();
        }
    }

    private static Runnable clientHandler(Socket socket) {
        return () -> {
            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()))) {
                String line = "";
                while (!"/quit".equals(line)) {
                    // Reading from a socket may block the thread allocated to the connection, such as when insufficient data is being read.
                    line = reader.readLine();
                    System.out.println("~ " + line);
                    // Writing to a socket may also block, such as until the underlying TCP buffer data has been sent over the network.
                    writer.write(line + "\n");
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
