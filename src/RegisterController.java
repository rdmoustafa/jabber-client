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
import java.util.ArrayList;
import java.util.Objects;

public class RegisterController {

    private String currentUser;
    private final StreamManager sm = new StreamManager();

    @FXML private TextField name, username, password, passwordConfirm;
    @FXML private Label errors;
    @FXML private Button submitB, backB;

    /**
     * Registering a user after they press the submit button
     * @param event pressing the register button
     */
    @FXML
    private void register(ActionEvent event) {
        errors.setText("");
        String n = name.getText();
        String un = username.getText().toLowerCase();
        String pw = password.getText();
        String pwC = passwordConfirm.getText();
        if (pw.equals(pwC)) { // password and password confirmation equal each other
            ArrayList<String> user = new ArrayList<>();
            user.add(n);
            user.add(un);
            user.add(pw);
            ArrayList<ArrayList<String>> data = new ArrayList<>();
            data.add(user);
            String response = sm.exchangeMessages("register", data).getMessage();
            switch (response) {
                case "signedin" -> { // Registration successful
                    setCurrentUser(un);
                    switchScene(event, "next");
                }
                case "invalid-username" -> errors.setText("Username \"" + un + "\" is already taken, " +
                        "try a different username");
                case "invalid-password" -> errors.setText("Your password needs to have 5 characters " +
                        "\nwhere one of which is a number");
                default -> errors.setText("Registration Failed"); // Registration failed
            }
        }
        else errors.setText("Your passwords don't even match, try again");
    }

    /**
     * Takes the user back to the login page
     * @param event go back button being pressed
     */
    @FXML
    private void goBack(ActionEvent event) { switchScene(event, "back"); }

    /**
     * Function will switch between the Login page and the User page
     * @param event the action being passed in -> button click in this case?
     */
    private void switchScene(ActionEvent event, final String command) {
        try {
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            stage.setResizable(false);
            FXMLLoader loader = new FXMLLoader();
            if (command.equals("back")) {
                Pane root = loader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")).openStream());
                Scene scene = new Scene(root);
                stage.setTitle("Jabber!");
                stage.setScene(scene);
                stage.show();
            }
            else if (command.equals("next")) {
                Pane root = loader.load(Objects.requireNonNull(getClass().getResource("UserPage.fxml")).openStream());
                Scene scene = new Scene(root);
                stage.setTitle("Welcome to Jabber " + getCurrentUser());
                stage.setScene(scene);
                stage.show();
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    private String getCurrentUser() { return currentUser; }
    private void setCurrentUser(String currentUser) { this.currentUser = currentUser; }
}