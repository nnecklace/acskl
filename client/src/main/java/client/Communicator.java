package client;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import client.models.Message;
import client.models.User;

/**
 * Class acts as the official delegate between client and server.
 * The class can be seen as a telnet client.
 * Communicator takes input and sends it to the server and handles the responses given by the server.
 */
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
            contents[3]
        );
    }

    private User parseUser(String details) {
        String[] userDetails = details.split("\\|");
        return new User(Integer.parseInt(userDetails[0]), userDetails[1]);
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

        if ("USER".equals(command)) {
            if ("LOGIN".equals(action)) {
                String[] parameters = Arrays.copyOfRange(response, 3, response.length);
                payload = parseUser(parameters[0]);
            }
        }
    }

    public <T> T getPayload(Class<T> model) {
        return (T) payload;
    }

    /**
     * Method sends messages to the server and blocks IO until a response has been recieved.
     * @param message the message that should be sent to the server
     * @return true if message was successfully interpreted by the server, otherwise false
     */
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