package server;

import server.services.MessageService;
import server.services.UserService;

import java.io.IOException;
import java.net.ServerSocket;

import server.database.Database;
import server.utils.Interceptor;

public class App {
    public static void main(String[] args) {
        // setup
        Database database = new Database();
        UserService userService = new UserService(database);
        MessageService messageService = new MessageService(database);
        Interceptor interceptor = new Interceptor(userService, messageService);
        try {
            Server server = new Server(new ServerSocket(6789), interceptor);
            // start server
            server.launch();
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
            System.exit(1);
        }
    }
}
