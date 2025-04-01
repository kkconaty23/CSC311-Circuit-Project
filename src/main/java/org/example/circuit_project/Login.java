package org.example.circuit_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.IOException;

import static javafx.application.Application.launch;


public class Login extends Application  {


        /**
         * load the default login screen
         * @param stage
         * @throws IOException
         */

        public void start(Stage stage) throws IOException {


            FXMLLoader fxmlLoader = new FXMLLoader(Login.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 475);
            stage.setTitle("Circuit's Login Page!");//main page of the application
            stage.setScene(scene);
            stage.show();
        }

        /**
         * enables the firebase to link to the application
         * key.json is stored in the gitignore file
         * @throws IOException
         */


        public static void main(String[] args) {
            launch();
        }
    }


