import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserPageController extends ScheduledService<Void> implements Initializable {

    private final StreamManager sm = new StreamManager();

    @FXML private VBox timelineBox;
    @FXML private VBox whoToFollowBox;
    @FXML private TextField newJab;

    /**
     * Initialising the UserPage scene before displaying it and the timer to refresh it every 2 seconds
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        settingTimeline(gettingServerInfo("timeline"));
        settingWhoToFollow(gettingServerInfo("users"));

        setPeriod(Duration.seconds(2));
        start();
    }

    /**
     * Creating the time task -> resetting timeline and who to follow
     */
    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    resetTimeline();
                    resetWhoToFollow();
                });
                return null;
            }
        };
    }

    /**
     * Sends a message to the server and get the data
     * @param message the message being sent to client ie. "timeline"
     * @return the data response from the server side
     */
    private ArrayList<ArrayList<String>> gettingServerInfo(String message) {
        try {
            JabberMessage response = sm.exchangeMessages(message);
            return response.getData();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creating the timeline VBOX that has a list of the jabs and a like button
     * @param timelineInfo All the jabs that the user should be seeing
     */
    public void settingTimeline(ArrayList<ArrayList<String>> timelineInfo) {
        Platform.runLater(() -> {
            for (ArrayList<String> jabs : timelineInfo) {
                // Extracting the elements
                String un = jabs.get(0);
                String jabtext = jabs.get(1);
                String jabid = jabs.get(2);
                String likecount = jabs.get(3);
                int currentLikeCount = Integer.parseInt(likecount);

                // Create label that has the username: jabText
                Label jabTextFormat = new Label("    " + un + ": " + jabtext + "     ");
                jabTextFormat.setId(jabid);
                jabTextFormat.setPrefHeight(50);
                jabTextFormat.setStyle("-fx-wrap-text: true");

                // TODO have the heart be different depending on whether it was liked before
                // Setting up how the image in button should look
                Image heart = new Image("FullHeart.jpg");
                ImageView image = new ImageView(heart);
                image.setFitHeight(50);
                image.setFitWidth(50);
                image.preserveRatioProperty();


                // Button that has the heart image and the number of likes
                Button jabLikes = new Button(likecount);
                jabLikes.setId(jabid); // Setting the jabID to be the button's id
                jabLikes.setGraphic(image); // Setting the image to be in the button
                jabLikes.setFont(Font.font(16));
                jabLikes.setStyle("-fx-background-color: White");

                // HBox in order to format how the label and button will be displayed
                HBox oneJab = new HBox();
                oneJab.setStyle("-fx-background-color: white");
                oneJab.setStyle("-fx-border-color: black");
                oneJab.getChildren().add(jabTextFormat);
                oneJab.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(jabTextFormat, Priority.ALWAYS);

                Region r = new Region(); // Small region to have space between label and button
                r.setPrefWidth(1);
                oneJab.getChildren().add(r);

                oneJab.getChildren().add(jabLikes);
                HBox.setHgrow(jabLikes, Priority.ALWAYS);

                timelineBox.getChildren().add(oneJab); // Adding the HBox to the VBox
                VBox.setVgrow(oneJab, Priority.ALWAYS);

                // Button listener for each button i hope and only if like has not been pressed before
                jabLikes.setOnAction(actionEvent -> {
                    String result = sm.exchangeMessages("like " + jabLikes.getId()).getMessage();
                    if (result.equals("posted")) {
                        int newLikeCount = currentLikeCount + 1;
                        String nLC = String.valueOf(newLikeCount);
                        jabLikes.setText(nLC);
                        Image full = new Image("FullHeart.jpg");
                        image.setImage(full);
                    }
                });
            }
        });
    }

    /**
     * Creaing the whoToFollow VBox
     * @param whoToFollow all the people that the current user is not following
     */
    public void settingWhoToFollow(ArrayList<ArrayList<String>> whoToFollow) {
        Platform.runLater(() -> {
            // Each user in the list of people that this user could follow aka not following right now
            for(ArrayList<String> users : whoToFollow) {
                // Extract wanted elements
                String un = users.get(0);

                // Setting up Label
                Label toFollow = new Label("    " + un);
                toFollow.setPrefHeight(50);
                toFollow.setPrefWidth(175);
                toFollow.setId(un);
                toFollow.setStyle("-fx-wrap-text: true");

                // Setting up button TODO could set up elsewhere since it looks the same everytime?
                Button follow = new Button();
                follow.setId(un);
                // TODO try without the ImageView maybe -> different than Joe's
                ImageView image = new ImageView(new Image("Follow.png"));
                image.setFitHeight(50);
                image.setFitWidth(50);
                image.preserveRatioProperty();
                follow.setGraphic(image);
                follow.setStyle("-fx-background-color: white");

                // HBox to format the label next to the button
                HBox oneUser = new HBox();
                oneUser.getChildren().add(toFollow); // Adding label
                oneUser.setStyle("-fx-border-color: black");
                oneUser.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(toFollow, Priority.ALWAYS); // grows according to the size of the contente
                Region r = new Region();
                r.setPrefWidth(30);
                oneUser.getChildren().add(r); // Adding space between label and region
                oneUser.getChildren().add(follow); // Adding the follow button
                HBox.setHgrow(follow, Priority.ALWAYS);

                // VBox to format the list on top of each other
                whoToFollowBox.getChildren().add(oneUser); // add HBox to VBox
                VBox.setVgrow(oneUser, Priority.ALWAYS);

                // Adding a listener to these buttons if follow is pressed
                follow.setOnAction(actionEvent -> {
                    try {
                        String reply = sm.exchangeMessages("follow " + un).getMessage();
                        if (reply.equals("posted")) {
                            whoToFollowBox.getChildren().remove(oneUser); // removing the HBox user
                            resetTimeline();
                        }
                    }
                    catch (Exception e) { e.printStackTrace(); }
                });
            }
        });
    }

    /**
     * User is posting a jab so it will send a message to user and add it to timeline
     */
    public void postingJab() {
        Platform.runLater(() -> {
            String response = sm.exchangeMessages("post " + newJab.getText()).getMessage();
            if (response.equals("posted")) {
                newJab.clear();
                resetTimeline();
            }
        });
    }

    /**
     * Resets the timeline display
     */
    private void resetTimeline() {
        timelineBox.getChildren().clear();
        settingTimeline(gettingServerInfo("timeline"));
    }

    /**
     * Resets the who to follow part
     */
    private void resetWhoToFollow() {
        whoToFollowBox.getChildren().clear();
        settingWhoToFollow(gettingServerInfo("users"));
    }

    /**
     * User is signing out so and will be taken back to Login page
     * @param event the button click on sign out
     */
    public void signOut(ActionEvent event) {
        try {
            String response = sm.exchangeMessages("signout").getMessage();
            // Going back to login page
            if (response.equals("signedout")) {
                ((Node)event.getSource()).getScene().getWindow().hide();
                Stage stage = new Stage();
                stage.setResizable(false);
                FXMLLoader loader = new FXMLLoader();
                Pane root = loader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")).openStream());
                Scene scene = new Scene(root);
                stage.setTitle("Welcome to Jabber");
                stage.setScene(scene);
                stage.show();
            }

        }
        catch (IOException e) { e.printStackTrace(); }
    }
}
