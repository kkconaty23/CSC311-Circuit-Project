package org.example.circuit_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.circuit_project.Storage.DbOpps;
import org.example.circuit_project.Storage.BlobDbOpps;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;


public class ProfileController implements Initializable {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ListView<Project> projectListView;

    User currentUser = UserManager.getInstance().getCurrentUser();
    private ObservableList<Project> userProjects;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserProfile();
        setupProjectListView();
        loadUserProjects();
    }

    /**
     * This method loads the first name, last name and email of the current user.
     */
    private void loadUserProfile() {
        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());
        emailField.setText(currentUser.getEmail());
    }

    /**
     * This method loads all projects associated with the current user
     */
    private void loadUserProjects() {
        User currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        DbOpps dbOpps = new DbOpps();
        List<Project> projects = dbOpps.getUserProjects(currentUser.getID());

        // Initialize the observable list
        userProjects = FXCollections.observableArrayList();

        // Add all projects to the observable list
        if (projects != null && !projects.isEmpty()) {
            userProjects.addAll(projects);
            System.out.println("Added " + projects.size() + " projects to observable list");
        } else {
            System.out.println("No projects found for user");
        }

        // Set the observable list to the list view
        projectListView.setItems(userProjects);
    }

    /**
     * Sets up the project list view with custom cell factory to display project info and actions
     */
    private void setupProjectListView() {
//        projectListView.setItems(userProjects);
        projectListView.setCellFactory(param -> new ProjectListCell());
    }

    /**
     * This method is used to navigate back to the main-menu from the profile screen
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
     * This method changes the personal information of the current user. The new inputted
     * information is first checked through regex and then used in creating an UPDATE SQL
     * query.
     */
    @FXML
    private void saveProfileChanges() {
        DbOpps connection = new DbOpps();
        Regex checker = new Regex();
        boolean changedInfo = false;

        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || emailField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields. ");
            return;
        }

        if (checker.firstNameCheck(firstNameField.getText()) &&
                checker.lastNameCheck(lastNameField.getText()) &&
                checker.emailCheck(emailField.getText())) {
            changedInfo = connection.chanegUserInfo(currentUser.getID(), firstNameField.getText(), lastNameField.getText(), emailField.getText());
            currentUser.setFirstName(firstNameField.getText());
            currentUser.setLastName(lastNameField.getText());
            currentUser.setEmail(emailField.getText());
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "One of your fields are not in the correct format. ");
        }

        if (changedInfo) {
            showAlert(Alert.AlertType.INFORMATION, "Update Success",
                    "Your profile information has been successfully updated.");
        }
    }

    /**
     * This method makes an UPDATE SQL query to change the password of a given user
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

        if (currentUser.getPassword().equals(currentPasswordField.getText())) {
            boolean result = connection.changePassword(newPasswordField.getText(), currentUser.getID());
            if (result) {
                currentUser.setPassword(newPasswordField.getText());
            }
        }

        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();

        showAlert(Alert.AlertType.INFORMATION, "Successful Password Update",
                "Your password has been changed.");
    }

    /**
     * Imports a project from a file
     */
    @FXML
    private void importProject() {
        // Placeholder for future implementation
        showAlert(Alert.AlertType.INFORMATION, "Import Project",
                "You haven't imported a thing because this doesn't work yet");
    }

    /**
     * Opens the selected project in the circuit editor
     *
     * @param project The project to open
     */
    private void openProject(Project project) {
        try {
            // Store the selected project in a singleton
            ProjectManager.getInstance().setCurrentProject(project);

            // Load the circuit editor FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/circuit_project/sandbox.fxml"));
            Parent root = loader.load();

            // Get the controller and tell it to load the project
            SandboxController controller = loader.getController();
            controller.loadProject(project);

            // Navigate to circuit editor
            Stage stage = (Stage) projectListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not open the circuit editor. Please try again.");
        }
    }

    /**
     * Deletes a project from the database and refreshes the list
     *
     * @param project The project to delete
     */
    private void deleteProject(Project project) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the project \"" + project.getName() + "\"?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DbOpps connection = new DbOpps();
                boolean deleted = connection.deleteProject(project.getId());

                if (deleted) {
                    userProjects.remove(project);
                    showAlert(Alert.AlertType.INFORMATION, "Project Deleted",
                            "The project was successfully deleted.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed",
                            "Could not delete the project. Please try again.");
                }
            }
        });
    }

    /**
     * This method triggers a pop-up screen to that will come with an alert message
     *
     * @param type Alert type
     * @param title Header of the alert screen pop-up
     * @param message Body of alert message
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Custom ListCell for displaying projects with actions
     */
    private class ProjectListCell extends ListCell<Project> {
        private final Button openButton = new Button("Open");
        private final Button deleteButton = new Button("Delete");
        private final HBox content = new HBox(10);
        private final Label nameLabel = new Label();
        private final Label dateLabel = new Label();

        public ProjectListCell() {
            // Configure the cell layout
            content.setStyle("-fx-alignment: center-left; -fx-padding: 5;");

            // Set up buttons
            openButton.getStyleClass().add("action-button-small");
            deleteButton.getStyleClass().add("delete-button");

            // Add event handlers to buttons
            openButton.setOnAction(e -> openProject(getItem()));
            deleteButton.setOnAction(e -> deleteProject(getItem()));

            // Set up layout
            nameLabel.setPrefWidth(200);
            dateLabel.setPrefWidth(150);
            HBox actionButtons = new HBox(5, openButton, deleteButton);

            content.getChildren().addAll(nameLabel, dateLabel, actionButtons);
        }

        @Override
        protected void updateItem(Project project, boolean empty) {
            super.updateItem(project, empty);

            if (empty || project == null) {
                setText(null);
                setGraphic(null);
            } else {
                nameLabel.setText(project.getName());

                // Format the last modified date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                dateLabel.setText(project.getLastModified().format(formatter));

                setGraphic(content);
            }
        }
    }
}