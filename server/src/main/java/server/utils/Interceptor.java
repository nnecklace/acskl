package server.utils;

import server.services.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Interceptor {
    private UserService userService;
    private List<String> validCommands;
    private List<String> validActions;
    private String userCommand = "USER";

    public Interceptor(UserService userService) {
        this.userService = userService;

        validCommands = new ArrayList<>();
        validCommands.add("USER");

        validActions = new ArrayList<>();
        validActions.add("CREATE");
        validActions.add("LIST");
        validActions.add("LOGIN");
    }

    private String response(boolean success, String command, String action, String[] parameters) {
        String message = command + ":" + action + ":" + String.join(":", parameters);
        if (success) {
            return "S:" + message;
        }

        return "E:" + message;
    }

    private String invalid() {
        return "E:INVALID";
    }

    private String performAction(String command, String action, String[] parameters) {
        boolean success = false;
        if (userCommand.equals(command)) {
            if ("CREATE".equals(action)) {
                success = userService.create(parameters[0]);
            }

            if ("LOGIN".equals(action)) {
                success = userService.login(parameters[0]);
            }
        }

        return response(
            success,
            command,
            action,
            parameters
        );
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