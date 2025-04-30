package org.example.circuit_project;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.circuit_project.Storage.DbOpps;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.UUID;

public class LoginController implements Initializable {

    @FXML private Pane slidingPane; //Panel that slides
    @FXML private Button slideRightButton; //Panel Slide Button <RIGHT>
    @FXML private Button slideLeftButton; //Panel Slide Button <LEFT>
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private TextField emailCheckField;
    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private PasswordField passwordCheckField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker dobField;
    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label emailRegex, emailMatchRegex, passwordMatchRegex, firstNameRegex, lastNameRegex, registerErrorLabel;

    private Regex regex;

    /**
     *
     * @param location
     * @param resources
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        slidingPane.setTranslateX(slidingPane.getWidth());

        // Initialize regex validator
        regex = new Regex();

        // Make sure error labels are initially empty
        clearErrorLabels();

        // Add listeners for field validation
        firstNameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // When focus is lost
                validateFirstName();
            }
        });

        lastNameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateLastName();
            }
        });

        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateEmail();
            }
        });

        emailCheckField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateEmailMatch();
            }
        });

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                // Password validation when implemented
            }
        });

        passwordCheckField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validatePasswordMatch();
            }
        });
    }

    /**
     * This method is used to clear all the error labels within the registration screen
     *
     */
    private void clearErrorLabels() {
        registerErrorLabel.setText("");
        firstNameRegex.setText("");
        lastNameRegex.setText("");
        emailRegex.setText("");
        emailMatchRegex.setText("");
        passwordMatchRegex.setText("");
    }

    /**
     * This method ensures that the inputted first name is made up of
     * only characters and starts with a capital letter
     *
     */
    private void validateFirstName() {
        if (!firstNameField.getText().isEmpty()) {
            if (!regex.firstNameCheck(firstNameField.getText())) {
                firstNameRegex.setText("Only letters allowed, 2-25 characters");
            } else {
                firstNameRegex.setText("");
            }
        }
    }

    /**
     * This method ensures that the inputted last name is made up of
     * only letters and starts with a capital letter
     *
     */
    private void validateLastName() {
        if (!lastNameField.getText().isEmpty()) {
            if (!regex.lastNameCheck(lastNameField.getText())) {
                lastNameRegex.setText("Only letters allowed, 2-25 characters");
            } else {
                lastNameRegex.setText("");
            }
        }
    }

    /**
     * This method ensures that the inputted email is only a
     * @farmingdale.edu email
     *
     */
    private void validateEmail() {
        if (!emailField.getText().isEmpty()) {
            if (!regex.emailCheck(emailField.getText())) {
                emailRegex.setText("Must be @farmingdale.edu");
            } else {
                emailRegex.setText("");
            }
        }
    }

    /**
     * This method ensures that the email in the "retype email" textField
     * matches with the "email" textField
     *
     */
    private void validateEmailMatch() {
        if (!emailField.getText().isEmpty() && !emailCheckField.getText().isEmpty()) {
            if (!emailCheckField.getText().equals(emailField.getText())) {
                emailMatchRegex.setText("Emails must match");
            } else {
                emailMatchRegex.setText("");
            }
        }
    }

    /**
     * This method ensures that the password in the "retype password" textFields
     * matches with the "password" textField
     *
     */
    private void validatePasswordMatch() {
        if (!passwordField.getText().isEmpty() && !passwordCheckField.getText().isEmpty()) {
            if (!passwordCheckField.getText().equals(passwordField.getText())) {
                passwordMatchRegex.setText("Passwords must match");
            } else {
                passwordMatchRegex.setText("");
            }
        }
    }

    @FXML
    private void slideToRegister() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(500), slidingPane);
        slide.setToX(0); // Slide into view
        slide.setOnFinished(e -> slidingPane.setPrefWidth(400));
        slide.play();
    }

    @FXML
    private void slideToLogin() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(500), slidingPane);
        slidingPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        slide.setToX(500);
        slide.play();
    }

    /**
     * This method checks for any empty fields and ensures the inputted data passes
     * the respective regex patterns
     *
     * @return boolean according to filled fields
     */
    private boolean isFormValid() {
        // Check if all fields are filled
        if (firstNameField.getText().isEmpty() ||
                lastNameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                emailCheckField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                passwordCheckField.getText().isEmpty() ||
                dobField.getValue() == null) {
            registerErrorLabel.setText("All fields are required");
            return false;
        }

        // Check if all validations pass
        if (!regex.firstNameCheck(firstNameField.getText())) {
            registerErrorLabel.setText("First name format is invalid");
            return false;
        }

        if (!regex.lastNameCheck(lastNameField.getText())) {
            registerErrorLabel.setText("Last name format is invalid");
            return false;
        }

        if (!regex.emailCheck(emailField.getText())) {
            registerErrorLabel.setText("Email must be @farmingdale.edu");
            return false;
        }

        if (!emailField.getText().equals(emailCheckField.getText())) {
            registerErrorLabel.setText("Emails do not match");
            return false;
        }

        if (!passwordField.getText().equals(passwordCheckField.getText())) {
            registerErrorLabel.setText("Passwords do not match");
            return false;
        }

        registerErrorLabel.setText("");
        return true;
    }

    @FXML
    private void onLoginClicked(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/loading-screen.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to establish a connection to the database and insert a user through the DbOpps
     * class
     * @param event clicking the register button
     */
    @FXML
    void onRegisterClicked(ActionEvent event) {
        if (!isFormValid()) {
            return;
        }

        // Database connection
        DbOpps connection = new DbOpps();
        connection.connectToDatabase();

        // DOB date picker converter
        LocalDate dob_ = dobField.getValue();
        LocalDateTime dobDateTime = dob_.atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDOB = dobDateTime.format(formatter);

        // UniqueID generation
        String uniqueID = UUID.randomUUID().toString();

        // User is inserted(registered) into database
        connection.insertUser(uniqueID, emailCheckField.getText(), passwordField.getText(),
                firstNameField.getText(), lastNameField.getText(), formattedDOB);

        // Show success message
        registerErrorLabel.setText("Registration successful!");
    }
}