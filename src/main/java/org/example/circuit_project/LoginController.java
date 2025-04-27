package org.example.circuit_project;

//<<<<<<< HEAD
//        =======
//
//        >>>>>>> Storage-Integration
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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



    @FXML
    public void initialize(URL location, ResourceBundle resources){
        slidingPane.setTranslateX(slidingPane.getWidth());
    }

    @FXML
    private void slideToRegister(){
        TranslateTransition slide = new TranslateTransition(Duration.millis(500), slidingPane);
        slide.setToX(0); //Slide into view
        slide.setOnFinished(e-> slidingPane.setPrefWidth(400));
        slide.play();
    }

    @FXML
    private void slideToLogin(){
        TranslateTransition slide = new TranslateTransition(Duration.millis(500), slidingPane);
        slidingPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        slide.setToX(500);
        slide.play();
    }

    /**
     * I HAVE THE LOGIN BUTTON GOING DIRECTLY INTO THE SANDBOX, AS WE DO NOT HAVE A MAIN MENU YET. - JUSTIN
     * @param event
     */
    @FXML
    private void onLoginClicked(ActionEvent event){

//        DbOpps connection = new DbOpps();//establish database connection
//
//
//        boolean checkUser = connection.queryUserByName(loginEmailField.getText(), loginPasswordField.getText());
//
//        System.out.println(checkUser);
//
//        if (checkUser){

            try {
                Parent root = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/loading-screen.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();


            } catch (IOException e) {
                e.printStackTrace();
            }
//        }else{
//            System.out.println(loginEmailField.getText() + " " + loginPasswordField.getText() + "is invalid");
//        }

    }
    @FXML
    void onRegisterClicked(ActionEvent event) {
        System.out.println("onRegisterClicked");

        //Database connection
        DbOpps connection = new DbOpps();
        connection.connectToDatabase();

        //DOB date picker converter
        LocalDate dob_ = dobField.getValue();
        LocalDateTime dobDateTime = dob_.atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDOB = dobDateTime.format(formatter);

        //UniqueID generation
        String uniqueID = UUID.randomUUID().toString();

        //User is inserted(registered) into database
        connection.insertUser(uniqueID, emailCheckField.getText(), passwordField.getText(), firstNameField.getText(), lastNameField.getText(), formattedDOB);


    }

}