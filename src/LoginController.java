import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    private static String currentUser;
    private final StreamManager sm = new StreamManager();

    @FXML private TextField username, password;
    @FXML private Label loginStatus, popupLabel;
    @FXML private Pane popUp;
    @FXML private Button signInB, registerB;

    /**
     * Function that is connected to sign in and register buttons
     * This will send the appropriate message to server and go through the sign in or registration process
     * @param event the button click on sign in or register
     */
    @FXML
    private void buttonResponse(ActionEvent event) {
        popupLabel.setTextAlignment(TextAlignment.CENTER);
        String un = username.getText().toLowerCase();
        String pw = password.getText();
        if (event.getSource().equals(registerB)) switchScene(event, "register"); // User pressed register button
        else if (un.equals("") || pw.equals("")) { // If the user didn't enter anything
            popupLabel.setText("You failed. You didn't enter anything, try again");
            popUp.setVisible(true);
        }
        else if (event.getSource().equals(signInB)) signIn(event, un, pw); // User pressed on the sign in button
    }

    /**
     * Signing in a user after they press the sign in button
     * @param event pressing the sign in button
     * @param un username that the user is logging in to
     * @param pw password that the user enters
     */
    private void signIn(ActionEvent event, String un, String pw) {
        String response = sm.exchangeMessages("signin " + un + " " + pw).getMessage();
        switch (response) {
            case "signedin" -> { // Sign in successful
                setCurrentUser(un);
                loginStatus.setText("Sign in Successful. Welcome " + un);
                switchScene(event, "signin");
            }
            case "unknown-user" -> {  // Sign in not successful - username doesn't exist
                loginStatus.setText("Username: " + un + " does not exist");
                popupLabel.setText("You entered a username that doesn't exist, try again");
                popUp.setVisible(true);
            }
            case "incorrect-password" -> loginStatus.setText("You entered the wrong password"); // wrong pw
        }
    }

    /**
     * Function will switch between the Login page and the User page
     * @param event the action being passed in -> button click in this case?
     * @param command the command being passed to indicate signin or register
     */
    private void switchScene(ActionEvent event, final String command) {
        try {
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            stage.setResizable(false);
            FXMLLoader loader = new FXMLLoader();
            if (command.equals("signin")) { // Sign in and switch to user page
                Pane root = loader.load(Objects.requireNonNull(getClass().getResource("UserPage.fxml")).openStream());
                Scene scene = new Scene(root);
                stage.setTitle("Welcome to Jabber " + getCurrentUser());
                stage.setScene(scene);
                stage.show();
            }
            else if (command.equals("register")) { // Switch to registration page
                Pane root = loader.load(Objects.requireNonNull(getClass().getResource("Register.fxml")).openStream());
                Scene scene = new Scene(root);
                stage.setTitle("Register for Jabber!");
                stage.setScene(scene);
                stage.show();
            }
        }
        catch (IOException e) { e.printStackTrace(); }

    }

    /**
     * Closes the popup that appears when the sign in or registration fails
     */
    @FXML
    private void closePopUp() { popUp.setVisible(false); }

    // Getters and Setters
    private String getCurrentUser() { return currentUser; }
    private void setCurrentUser(String currentUser) { LoginController.currentUser = currentUser; }
}