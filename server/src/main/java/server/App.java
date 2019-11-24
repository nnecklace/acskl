package server;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.*;

public class App {
    public static void main(String[] args) throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(6789);
        while (true) {
            System.out.println("Server listening on port 6789");
            System.out.println("Waiting for connections");
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("Someone connected!");
            while (true) {
                System.out.println("Waiting for messages...");
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                String clientSentence = inFromClient.readLine();
                if (clientSentence == null) {
                    System.out.println(welcomeSocket.getInetAddress() + " disconnected");
                    connectionSocket.close();
                    break;
                }
                System.out.println("Received: " + clientSentence);
            }
        }
    }
}
