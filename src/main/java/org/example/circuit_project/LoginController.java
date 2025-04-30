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

    @FXML private Pane slidingPane;
    @FXML private Button slideRightButton;
    @FXML private Button slideLeftButton;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        slidingPane.setTranslateX(slidingPane.getWidth());

        regex = new Regex();
        clearErrorLabels();

        firstNameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateFirstName();
        });

        lastNameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateLastName();
        });

        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateEmail();
        });

        emailCheckField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateEmailMatch();
        });

        passwordCheckField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validatePasswordMatch();
        });
    }

    private void clearErrorLabels() {
        registerErrorLabel.setText("");
        firstNameRegex.setText("");
        lastNameRegex.setText("");
        emailRegex.setText("");
        emailMatchRegex.setText("");
        passwordMatchRegex.setText("");
    }

    private void validateFirstName() {
        if (!firstNameField.getText().isEmpty() && !regex.firstNameCheck(firstNameField.getText())) {
            firstNameRegex.setText("Only letters allowed, 2-25 characters");
        } else {
            firstNameRegex.setText("");
        }
    }

    private void validateLastName() {
        if (!lastNameField.getText().isEmpty() && !regex.lastNameCheck(lastNameField.getText())) {
            lastNameRegex.setText("Only letters allowed, 2-25 characters");
        } else {
            lastNameRegex.setText("");
        }
    }

    private void validateEmail() {
        if (!emailField.getText().isEmpty() && !regex.emailCheck(emailField.getText())) {
            emailRegex.setText("Must be @farmingdale.edu");
        } else {
            emailRegex.setText("");
        }
    }

    private void validateEmailMatch() {
        if (!emailField.getText().isEmpty() && !emailCheckField.getText().isEmpty()
                && !emailCheckField.getText().equals(emailField.getText())) {
            emailMatchRegex.setText("Emails must match");
        } else {
            emailMatchRegex.setText("");
        }
    }

    private void validatePasswordMatch() {
        if (!passwordField.getText().isEmpty() && !passwordCheckField.getText().isEmpty()
                && !passwordCheckField.getText().equals(passwordField.getText())) {
            passwordMatchRegex.setText("Passwords must match");
        } else {
            passwordMatchRegex.setText("");
        }
    }

    private boolean isFormValid() {
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
        DbOpps connection = new DbOpps();
        boolean checkUser = connection.queryUserByName(loginEmailField.getText(), loginPasswordField.getText());

        if (checkUser) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/loading-screen.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(loginEmailField.getText() + " " + loginPasswordField.getText() + " is invalid");
        }
    }

    @FXML
    void onRegisterClicked(ActionEvent event) {
        if (!isFormValid()) return;

        DbOpps connection = new DbOpps();
        connection.connectToDatabase();

        LocalDate dob_ = dobField.getValue();
        LocalDateTime dobDateTime = dob_.atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDOB = dobDateTime.format(formatter);

        String uniqueID = UUID.randomUUID().toString();

        connection.insertUser(uniqueID, emailCheckField.getText(), passwordField.getText(),
                firstNameField.getText(), lastNameField.getText(), formattedDOB);

        registerErrorLabel.setText("Registration successful!");
    }

    @FXML
    private void slideToRegister() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(500), slidingPane);
        slide.setToX(0);
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
}
