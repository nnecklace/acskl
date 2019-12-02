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
        UserService userService = new UserService(new Database());
        Interceptor interceptor = new Interceptor(userService);

        while (true) {
            System.out.println("Server listening on port 6789");
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("Someone connected");
            while (true) {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                System.out.println("Waiting for message");
                String input = inFromClient.readLine();
                System.out.println(input + " recevied");

                if (input == null) {
                    connectionSocket.close();
                    break;
                }

                String response = interceptor.parse(input);
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.writeBytes(response + "\n");
            }
        }
    }
}
