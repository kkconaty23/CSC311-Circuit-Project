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

/**
 * Controller for the main menu after user login.
 * Provides navigation options to tutorial, sandbox playground, profile page, and logout.
 */
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

    /**
     * Opens the sandbox/playground scene when the relevant button is clicked.
     *
     * @param event The ActionEvent triggered by the button
     */
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

    /**
     * Opens the tutorial page.
     *
     * @param event The ActionEvent triggered by the tutorial button
     */
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

    /**
     * Initializes the main menu controller and sets up hover behavior and button actions.
     */
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
     * Adds hover scaling effect between a button and its associated label.
     *
     * @param button The button to attach the effect to
     * @param label  The label to animate when the button is hovered
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

    /**
     * Shows the "About" overlay pane with fade-in animation.
     */
    @FXML
    public void showAboutOverlay(){
        aboutOverlay.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), aboutOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        setMainMenuButtonsDisabled(true);
    }

    /**
     * Hides the "About" overlay and re-enables main menu buttons.
     */
    @FXML
    public void hideAboutOverlay() {
        aboutOverlay.setVisible(false);
        setMainMenuButtonsDisabled(false);
    }

    /**
     * Logs the user out and returns to the login screen.
     */
    @FXML
    public void logoutUser() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/login.fxml"));
            Stage stage = (Stage) logoutIcon.getScene().getWindow();
            stage.setScene(new Scene(loginRoot, 1000, 679));

            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enables or disables all main menu buttons.
     *
     * @param disable true to disable all main menu controls; false to enable
     */
    private void setMainMenuButtonsDisabled(boolean disable){
        profileBtn.setDisable(disable);
        tutorialBtn.setDisable(disable);
        newPlaygroundBtn.setDisable(disable);
        logoutIcon.setDisable(disable);
        helpIcon.setDisable(disable);
    }

    /**
     * Displays the sandbox preview image when the user hovers over the sandbox option.
     */
    @FXML
    public void showSandboxImage() {
        System.out.println("hovering in"); // <- add this for debug
        sandboxPreview.setVisible(true);
    }

    /**
     * Hides the sandbox preview image when the user moves the cursor away.
     */
    @FXML
    public void hideSandboxImage() {
        System.out.println("hovering out"); // <- add this for debug
        sandboxPreview.setVisible(false);
    }

}