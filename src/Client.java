import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class Client extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            SocketConnection.setSocket(new Socket("localhost", 44444)); //establishing socket connection
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")));

            primaryStage.setTitle("Jabber!"); // Setting the title of the stage
            primaryStage.setResizable(false);
            primaryStage.setScene(new Scene(root)); // Setting the login.fxml scene on the stage

            primaryStage.show(); // Showing the stage [to the user]
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) { launch(); }
}