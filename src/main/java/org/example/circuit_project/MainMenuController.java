package org.example.circuit_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/*
* This is the screen that will be displayed after the user is logged in.
* User will have the option to enter the Tutorial, Create a new Playground, or visit their profile page.
* There will be a button to go back to the SplashScreen, which makes it similar to a "home dashboard".
* */
public class MainMenuController {

    // Button to enter tutorial mode
    @FXML
    private Button tutorialBtn;

    // Button to create new playground
    @FXML
    private Button newPlaygroundBtn;

    // Button to go to user profile
    @FXML
    private Button profileBtn;


    @FXML
    private void onClick(ActionEvent event) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/sandbox.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1030, 818);
            stage.setScene(scene);
            stage.show();


        } catch (IOException e){
            e.printStackTrace();
        }
    }


}
