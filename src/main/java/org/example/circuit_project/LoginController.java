package org.example.circuit_project;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {


    @FXML private Pane slidingPane; //Panel that slides
    @FXML private Button slideRightButton; //Panel Slide Button <RIGHT>
    @FXML private Button slideLeftButton; //Panel Slide Button <LEFT>

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

}