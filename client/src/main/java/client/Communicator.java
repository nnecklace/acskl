package client;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import client.models.Message;

public class Communicator {
    private DataOutput output;
    private BufferedReader input;
    private Object payload;

    public Communicator(DataOutput output, BufferedReader input) {
        this.output = output;
        this.input = input;
    }

    private Message parseMessage(String content) {
        String[] contents = content.split("\\|");
        return new Message(
            Integer.parseInt(contents[0]),
            contents[1], 
            Integer.parseInt(contents[2]),
            Integer.parseInt(contents[3]) 
        );
    }

    private List<Message> parseMessage(String[] contents) {
        List<Message> messages = new ArrayList<>();

        for (String content: contents) {
            messages.add(parseMessage(content));
        }
    
        return messages;
    }

    private void handleResponse(String[] response) {
        String command = response[1];
        String action = response[2];

        if ("MESSAGE".equals(command)) {
            String[] parameters = Arrays.copyOfRange(response, 3, response.length);
            if ("CREATE".equals(action)) {
                payload = parseMessage(parameters[0]);
            } else {
                payload = parseMessage(parameters);
            }
        }
    }

    public <T> T getPayload(Class<T> model) {
        return (T) payload;
    }

    public boolean sendMessage(String message) {
        try {
            output.writeBytes(message + "\n"); 
            String[] response = input.readLine().split(":");
            if ("E".equals(response[0])) {
                return false;
            }
            handleResponse(response);
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
            return false;
        }

        return true;
    }
}