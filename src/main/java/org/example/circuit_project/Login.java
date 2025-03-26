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



public class Login extends HelloApplication {


        /**
         * load the default login screen
         * @param stage
         * @throws IOException
         */
        @Override
        public void start(Stage stage) throws IOException {


            FXMLLoader fxmlLoader = new FXMLLoader(Login.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 475);
            stage.setTitle("PintFinder's Login Page!");
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


