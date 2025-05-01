package org.example.circuit_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.circuit_project.Storage.DbOpps;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class ProfileController implements Initializable {

    // Personal information fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;

    // Password fields
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    User currentUser = UserManager.getInstance().getCurrentUser();




    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadUserProfile();

    }

    /**
     * now this works!
     */
    private void loadUserProfile() {

        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());
        emailField.setText(currentUser.getEmail());
    }

    /**
     * Navigate back to main menu
     */
    @FXML
    private void goBackToMainMenu() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/mainmenu.fxml"));
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not return to main menu. Please try again.");
        }
    }

    /**
     * doesnt work yet
     */
    @FXML
    private void saveProfileChanges() {

        showAlert(Alert.AlertType.INFORMATION, "Profile Updated",
                "Not really tho.");
    }

    /**
     * doesnt work yet
     */
    @FXML
    private void updatePassword() {
        DbOpps connection = new DbOpps();

        if (currentPasswordField.getText().isEmpty() ||
                newPasswordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error", "All password fields are required.");
            return;
        }

        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", "New password and confirmation do not match.");
            return;
        }

        if(currentUser.getPassword().equals(currentPasswordField.getText())) {
            boolean result = connection.changePassword(String.valueOf(newPasswordField), currentUser.getID());
            if(result) {
                currentUser.setPassword(newPasswordField.getText());
            }
        }


        // Clear fields and show confirmation
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();

        showAlert(Alert.AlertType.INFORMATION, "Hooray",
                "This doesnt do anything!");
    }

    /**
     * This doesnt work yet but would be cool
     */
    @FXML
    private void importProject() {
        showAlert(Alert.AlertType.INFORMATION, "Import Project",
                "You havent imported a thing because this doesnt work yet");
    }

    /**
     * This gives a popup alert screen that shows a given message
     * @param type
     * @param title
     * @param message
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}