package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import client.models.Message;
import client.models.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {
    Communicator communicator;
    Runnable poll;
    User user;    

    @Override
    public void init() {
        try {
            Socket connection = new Socket("localhost", 6789);
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            this.communicator = new Communicator(output, input);
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    private static void parseMessages(List<Object> messages, ObservableList<StackPane> items) {
        for (Object o : messages) {
            Message message = (Message) o;
            Label messageText = new Label(message.getAuthor() + ": " + message.getContent());
            messageText.setPrefWidth(300);
            messageText.setWrapText(true);
            Date date = new Date(message.getTimestamp() * 1000);
            DateFormat df = new SimpleDateFormat("E, dd MMM HH:mm", Locale.getDefault());
            Label dateText = new Label(df.format(date));
            StackPane messageContainer = new StackPane(messageText, dateText);
            StackPane.setAlignment(messageText, Pos.CENTER_LEFT);
            StackPane.setAlignment(dateText, Pos.CENTER_RIGHT);

            items.add(messageContainer);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Login
        Pane loginRoot = new Pane();
        VBox loginContainer = new VBox(10);
        loginContainer.setPadding(new Insets(10));
        loginContainer.setMinHeight(250);
        loginContainer.setAlignment(Pos.CENTER);

        HBox errorContainer = new HBox(10);
        errorContainer.setMinWidth(400);
        errorContainer.setAlignment(Pos.CENTER);

        HBox textContainer = new HBox(10);
        textContainer.setMinWidth(400);
        textContainer.setAlignment(Pos.CENTER);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setMinWidth(400);

        Button login = new Button("Login");
        Button create = new Button("Create");
        TextField textfield = new TextField();
        Label labelMsg = new Label();

        create.setOnAction(ev -> {
            String username = textfield.getText();
            boolean yes = communicator.sendMessage("USER:CREATE:" + username);

            if (yes) {
                labelMsg.setText("Username " + username + " successfully created!");
                labelMsg.setTextFill(Color.GREEN);
            } else {
                labelMsg.setText("Username could not be created");
                labelMsg.setTextFill(Color.RED);
            }
        });

        errorContainer.getChildren().add(labelMsg);
        loginContainer.getChildren().add(errorContainer);
        textContainer.getChildren().add(textfield);
        loginContainer.getChildren().add(textContainer);
        buttonContainer.getChildren().addAll(login, create);
        loginContainer.getChildren().add(buttonContainer);
        loginContainer.setSpacing(20);
        loginRoot.getChildren().add(loginContainer);

        Scene loginScene = new Scene(loginRoot, 400, 250); 

        // Chat view
        Pane chatRoot = new Pane();
        VBox chatContainer = new VBox();

        VBox logoutContainer = new VBox();
        logoutContainer.setMinWidth(400);
        logoutContainer.setAlignment(Pos.TOP_RIGHT);
        Button logoutButton = new Button("Logout");
        logoutContainer.getChildren().add(logoutButton);

        logoutButton.setOnAction(e -> {
            primaryStage.setScene(loginScene);
            user = null;
        });

        HBox chatError = new HBox();
        Label chatErrorMessage = new Label();
        chatError.getChildren().add(chatErrorMessage);

        ListView<StackPane> list = new ListView<>();
        ObservableList<StackPane> items = FXCollections.observableArrayList();
        list.setItems(items);

        ScrollPane chatListContainer = new ScrollPane();
        chatListContainer.fitToWidthProperty().set(true);
        chatListContainer.setPadding(new Insets(0, 50, 10, 50));
        chatListContainer.setMinWidth(400);
        chatListContainer.setPannable(true);
        VBox chatList = new VBox();
        chatListContainer.setContent(chatList);

        HBox bottomActionsContainer = new HBox();
        TextField text = new TextField();
        text.setMinWidth(330);
        Button send = new Button("Send");
        send.setMinWidth(70);

        send.setOnAction(e -> {
            long epoch = Instant.now().getEpochSecond();
            boolean yes = communicator.sendMessage("MESSAGE:CREATE:" + text.getText() + ":" + epoch + ":" + user.getId());
            
            if (yes) {
                Message message = communicator.getPayload(Message.class);
                Label messageText = new Label(message.getAuthor() + ": " + message.getContent());
                messageText.setPrefWidth(300);
                messageText.setWrapText(true);
                Date date = new Date(message.getTimestamp() * 1000);
                DateFormat df = new SimpleDateFormat("E, dd MMM HH:mm", Locale.getDefault());
                Label dateText = new Label(df.format(date));
                StackPane messageContainer = new StackPane(messageText, dateText);
                StackPane.setAlignment(messageText, Pos.CENTER_LEFT);
                StackPane.setAlignment(dateText, Pos.CENTER_RIGHT);
                items.add(messageContainer);
                text.setText("");
                chatErrorMessage.setText("");
            } else {
                chatErrorMessage.setText("Could not send message");
                chatErrorMessage.setTextFill(Color.RED);
            }
        });

        bottomActionsContainer.setAlignment(Pos.BOTTOM_CENTER);
        bottomActionsContainer.setMinWidth(400);
        bottomActionsContainer.getChildren().addAll(text, send);

        chatContainer.getChildren().addAll(logoutContainer, list, bottomActionsContainer);
        chatRoot.getChildren().add(chatContainer);

        Scene chatScene = new Scene(chatRoot, 400, 500);

        login.setOnAction(ev -> {
            boolean yes = communicator.sendMessage("USER:LOGIN:" + textfield.getText());

            if (yes) {
                primaryStage.setScene(chatScene);
                items.clear();
                user = communicator.getPayload(User.class);
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        boolean yes = communicator.sendMessage("MESSAGE:LIST");
                        if (yes) {
                            List<Object> messages = communicator.getPayload(List.class);
                            if (messages.size() > items.size()) {
                                items.clear();
                                parseMessages(messages, items);
                            }
                        } else {
                            System.out.println("Polling failed");
                        }
                    }
                }));

                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();

                yes = communicator.sendMessage("MESSAGE:LIST");

                if (yes) {
                    List<Object> messages = communicator.getPayload(List.class);
                    parseMessages(messages, items);
                    chatErrorMessage.setText("");
                } else {
                    chatErrorMessage.setText("Could not list messages");
                    chatErrorMessage.setTextFill(Color.RED);
                }

                textfield.setText("");
                labelMsg.setText("");
            } else {
                labelMsg.setText("Could not login!");
                labelMsg.setTextFill(Color.RED);
            }
        });

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
