package server;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.IOException;
import java.net.Socket;

import server.utils.Interceptor;

public class Connection extends Thread {
    private Socket connection;
    private Interceptor interceptor;
    private BufferedReader reader;
    private DataOutput writer;

    public Connection(Socket connection, Interceptor interceptor) {
        this.connection = connection;
        this.interceptor = interceptor;
    }

    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for message");
                String input = reader.readLine();
                System.out.println(input + " recevied");

                if (input == null) {
                    break;
                }

                String response = interceptor.parse(input);
                writer.writeBytes(response + "\n");
            } catch (IOException e) {
                System.err.println("Could not get input stream from client: " + e.getMessage());
                break;
            }
        }
        shutDown();
    }

    private void shutDown() {
        try {
            connection.close();
        } catch (IOException e) {
            System.err.println("Could not close connection to client: " + e.getMessage());
        }
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void setWriter(DataOutput writer) {
        this.writer = writer;
    }

    public DataOutput getWriter() {
        return writer;
    }

    public boolean isClosed() {
        return connection.isClosed();
    }
}