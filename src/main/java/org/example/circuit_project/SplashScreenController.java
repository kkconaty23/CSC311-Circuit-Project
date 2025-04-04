package org.example.circuit_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.scene.control.*;

/*
* This is the screen that will be displayed after the user is logged in.
* User will have the option to enter the Tutorial, Create a new Playground, or visit their profile page.
* There will be a button to go back to the SplashScreen, which makes it similar to a "home dashboard".
* */
public class SplashScreenController  {

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

    }


}
