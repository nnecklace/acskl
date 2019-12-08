package server.utils;

import server.services.MessageService;
import server.services.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Interceptor {
    private UserService userService;
    private MessageService messageService;
    private List<String> validCommands;
    private List<String> validActions;
    private String userCommand = "USER";
    private String messageCommand = "MESSAGE";
    private Object content;

    public Interceptor(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;

        validCommands = new ArrayList<>();
        validCommands.add(userCommand);
        validCommands.add(messageCommand);

        validActions = new ArrayList<>();
        validActions.add("CREATE");
        validActions.add("LIST");
        validActions.add("LOGIN");
    }

    private String response(boolean success, String command, String action) { 
        String message = command + ":" + action;
        if (success) {
            String content = getContent();
            return "S:" + message + (content != null ? (":" + content) : "");
        }

        return "E:" + message;
    }

    private String invalid() {
        return "E:INVALID";
    }

    private String performAction(String command, String action, String[] parameters) {
        boolean success = false;
        Object payload = null;

        if (userCommand.equals(command)) {
            if ("CREATE".equals(action)) {
                payload = userService.create(parameters[0]);
            }

            if ("LOGIN".equals(action)) {
                success = userService.login(parameters[0]);
            }
        }

        if (messageCommand.equals(command)) {
            if ("CREATE".equals(action)) {
                payload = messageService.create(
                    parameters[0], 
                    Integer.parseInt(parameters[1]), 
                    Integer.parseInt(parameters[2])
                );
            }

            if ("LIST".equals(action)) {
                payload = messageService.getAll();
            }
        }

        if (payload != null) {
            success = true;
            setContent(payload);
        }

        return response(
            success,
            command,
            action
        );
    }

    private String getContent() {
        if (content instanceof List) {
            List<Object> contents = (List) content;

            List<String> values = contents.stream().map(i -> i.toString()).collect(Collectors.toList());

            return String.join(":", values);
        }
        
        if (content == null) {
            return null;
        } 

        return content.toString();
    }

    private void setContent(Object content) {
        this.content = content;
    }

    // input: USER:LOGIN:Simon
    // input: USER:CREATE:Simon
    // input: MESSAGE:LIST
    // input: MESSAGE:CREATE:content:timestamp:Simon
    public String parse(String input) {
        if (input == null) {
            return invalid();
        }

        String[] instructions = input.split(":");

        if (instructions.length < 2) {
            return invalid();
        }

        String command = instructions[0];
        String action = instructions[1];

        if (!validCommands.contains(command) || !validActions.contains(action)) {
            return invalid();
        }

        String[] parameters = Arrays.copyOfRange(instructions, 2, instructions.length);

        return performAction(command, action, parameters);
    }
}