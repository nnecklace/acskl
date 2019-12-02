package server;

import java.net.ServerSocket;
import java.net.Socket;

import server.services.UserService;
import server.database.Database;
import server.utils.Interceptor;

import java.io.*;

public class App {
    public static void main(String[] args) throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(6789);
        Database db = new Database();
        UserService userService = new UserService(db);
        Interceptor interceptor = new Interceptor(userService);

        while (true) {
            System.out.println("Server listening on port 6789");
            System.out.println("Waiting for connections");
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("Someone connected!");
            while (true) {
                System.out.println("Waiting for messages...");
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                String input = inFromClient.readLine();

                String response = interceptor.parse(input);

                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.writeBytes(response + "\n");

                if (input == null) {
                    System.out.println(welcomeSocket.getInetAddress() + " disconnected");
                    connectionSocket.close();
                    break;
                }
            }
        }
    }
}
