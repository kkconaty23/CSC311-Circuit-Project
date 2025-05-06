package org.example.circuit_project;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * This is the screen that will be displayed after the user is logged in.
 * User will have the option to enter the Tutorial, Create a new Playground, or visit their profile page.
 * There will be a button to go back to the SplashScreen, which makes it similar to a "home dashboard".
 * */
public class MainMenuController implements Initializable {


    @FXML private Button tutorialBtn; // Button to enter tutorial mode
    @FXML private Button newPlaygroundBtn; // Button to create new playground
    @FXML private Button profileBtn; // Button to go to user profile
    @FXML private Button logoutIcon;
    @FXML private Button helpIcon;
    @FXML private Pane aboutOverlay;


    @FXML private Label profileLabel;
    @FXML private Label tutorialLabel;
    @FXML private Label sandboxLabel;

    @FXML private ImageView sandboxPreview;

    @FXML
    private void onClick(ActionEvent event) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/sandbox.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1030, 818);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();


        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Navigate to profile page when profile button is clicked
     * @param event The action event triggered by clicking the profile button
     */
    @FXML
    private void openProfilePage(ActionEvent event) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/profile_page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1030, 818);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void openTutorialPage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/tutorial-page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1030, 818);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupHoverSync(profileBtn, profileLabel);
        setupHoverSync(tutorialBtn, tutorialLabel);
        setupHoverSync(newPlaygroundBtn, sandboxLabel);

        // Set up action handlers for buttons
        profileBtn.setOnAction(this::openProfilePage);
        newPlaygroundBtn.setOnAction(this::onClick); // Use existing onClick method for sandbox
    }

    /**
     * Allows for the hover sync of both button and label
     * @param button
     * @param label
     */
    private void setupHoverSync(Button button, Label label){
        button.setOnMouseEntered(e -> {
            label.setScaleX(1.08);
            label.setScaleY(1.08);
            label.setOpacity(1.0);
        });

        button.setOnMouseExited(e -> {
            label.setScaleX(1.0);
            label.setScaleY(1.0);
            label.setOpacity(.9);
        });
    }

    @FXML
    public void showAboutOverlay(){
        aboutOverlay.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), aboutOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        setMainMenuButtonsDisabled(true);
    }

    @FXML
    public void hideAboutOverlay() {
        aboutOverlay.setVisible(false);
        setMainMenuButtonsDisabled(false);
    }

    @FXML
    public void logoutUser() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/login.fxml"));
            Stage stage = (Stage) logoutIcon.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMainMenuButtonsDisabled(boolean disable){
        profileBtn.setDisable(disable);
        tutorialBtn.setDisable(disable);
        newPlaygroundBtn.setDisable(disable);
        logoutIcon.setDisable(disable);
        helpIcon.setDisable(disable);
    }

    @FXML
    public void showSandboxImage() {
        System.out.println("hovering in"); // <- add this for debug
        sandboxPreview.setVisible(true);
    }

    @FXML
    public void hideSandboxImage() {
        System.out.println("hovering out"); // <- add this for debug
        sandboxPreview.setVisible(false);
    }

}