package client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void init() {
        // connect to server
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setMinHeight(250);
        container.setAlignment(Pos.CENTER);
        HBox textContainer = new HBox(10);
        textContainer.setMinWidth(400);
        textContainer.setAlignment(Pos.CENTER);
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setMinWidth(400);
        Button login = new Button("Login");
        Button create = new Button("Create");
        TextField textfield = new TextField();
        textContainer.getChildren().add(textfield);
        container.getChildren().add(textContainer);
        buttonContainer.getChildren().addAll(login, create);
        container.getChildren().add(buttonContainer);
        container.setSpacing(20);
        root.getChildren().add(container);
        primaryStage.setScene(new Scene(root, 400, 250));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
