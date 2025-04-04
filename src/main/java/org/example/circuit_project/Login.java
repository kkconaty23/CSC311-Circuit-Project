package org.example.circuit_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
            Scene scene = new Scene(fxmlLoader.load(), 1000, 677);
            stage.setTitle("Circuit Sandbox");
            stage.setResizable(false);
            stage.setScene(scene);

            // Setting Favicon
            Image icon = new Image(getClass().getResource("/org/example/circuit_project/images/raw.png").toExternalForm());
            stage.getIcons().add(icon);

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


