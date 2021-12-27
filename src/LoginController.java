import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
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
        String un = username.getText().toLowerCase();
        String pw = password.getText();
        if (un.equals("") || pw.equals("")) { // If the user didn't enter anything
            popupLabel.setText("You failed. You didn't enter anything, try again");
            popUp.setVisible(true);
        }
        else if (event.getSource().equals(signInB)) { // User pressed on the sign in button
            String response = sm.exchangeMessages("signin " + un + " " + pw).getMessage();
            switch (response) {
                case "signedin" -> { // Sign in successful
                    setCurrentUser(un);
                    loginStatus.setText("Sign in Successful. Welcome " + un);
                    switchScene(event);
                }
                case "unknown-user" -> {  // Sign in not successful - username doesn't exist
                    loginStatus.setText("Username: " + un + " does not exist");
                    popupLabel.setText("You entered a username that doesn't exist, try again");
                    popUp.setVisible(true);
                }
                case "incorrect-password" -> loginStatus.setText("You entered the wrong password"); // wrong pw
            }
        }
        else if (event.getSource().equals(registerB)) { // User pressed register button
            String response = sm.exchangeMessages("register " + un + " " + pw).getMessage();
            if (response.equals("signedin")) { // Registration successful
                setCurrentUser(un);
                switchScene(event);
            }
            else { // Registration failed
                loginStatus.setText("Registration Failed");
                popupLabel.setText("Registration Failed, try again");
                popUp.setVisible(true);
            }
        }
    }

    /**
     * Function will switch between the Login page and the User page
     * @param event the action being passed in -> button click in this case?
     */
    private void switchScene(ActionEvent event) {
        try {
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            stage.setResizable(false);
            FXMLLoader loader = new FXMLLoader();
            Pane root = loader.load(Objects.requireNonNull(getClass().getResource("UserPage.fxml")).openStream());
            Scene scene = new Scene(root);
            stage.setTitle("Welcome to Jabber " + getCurrentUser());
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) { e.printStackTrace(); }

    }

    /**
     * Closes the popup that appears when the sign in or registration fails
     */
    public void closePopUp() { popUp.setVisible(false); }

    // Getters and Setters
    public String getCurrentUser() { return currentUser; }
    public void setCurrentUser(String currentUser) { LoginController.currentUser = currentUser; }
}