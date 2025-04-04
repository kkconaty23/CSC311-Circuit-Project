package org.example.circuit_project;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoadingScreenController {

    @FXML
    private ImageView loadingGif;

    @FXML
    public void initialize() {

        Image gif = new Image(getClass().getResource("/org/example/circuit_project/images/lightning.gif").toExternalForm());
        loadingGif.setImage(gif);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/circuit_project/splash-screen.fxml"));
                Parent homeRoot = loader.load();
                Stage stage = (Stage) loadingGif.getScene().getWindow();
                stage.setScene(new Scene(homeRoot));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        delay.play();
    }
}
