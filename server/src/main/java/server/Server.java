package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import server.utils.Interceptor;

public class Server {
    private ServerSocket server;
    private Interceptor interceptor;

    public Server(ServerSocket server, Interceptor interceptor) {
        this.server = server;
        this.interceptor = interceptor;
    }

    public void launch() {
        while (true) {
            System.out.println("Server listening on port 6789");
            try {
                Socket socket = server.accept();
                Connection connection = new Connection(socket, interceptor);
                connection.setReader(new BufferedReader(new InputStreamReader(socket.getInputStream())));
                connection.setWriter(new DataOutputStream(socket.getOutputStream()));
                connection.start();
            } catch (IOException e) {
                System.err.println("Server could not accpet requests: " + e.getMessage());
                break;
            }
        }

        shutdown();
    }

    private void shutdown() {
        try {
            this.server.close();
        } catch (IOException e) {
            System.err.println("Server could not shutdown: " + e.getMessage());
        }
    }

    public ServerSocket getServer() {
        return server;
    }
}