package client;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.IOException;

public class Communicator {
    DataOutput output;
    BufferedReader input;

    public Communicator(DataOutput output, BufferedReader input) {
        this.output = output;
        this.input = input;
    }

    public boolean sendMessage(String message) {
        try {
            output.writeBytes(message + "\n"); 
            String[] response = input.readLine().split(":");
            if ("E".equals(response[0])) {
                return false;
            }
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
            return false;
        }

        return true;
    }
}