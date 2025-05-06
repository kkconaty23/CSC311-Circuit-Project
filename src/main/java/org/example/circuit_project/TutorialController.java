package org.example.circuit_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.stage.Popup;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.io.IOException;

public class TutorialController {

    @FXML
    private Button homeButton;

    @FXML
    private Button logoutIcon2;

    @FXML
    private Button loadButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button toggleGridLinesItem;

    @FXML
    private Button toggleDarkModeItem;

    @FXML
    private Button runBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private ImageView batteryIcon;

    @FXML
    private ImageView bulbIcon;

    @FXML
    private Line wireIcon;

    @FXML
    private ImageView switchIcon;

    @FXML
    private AnchorPane playgroundPane;

    private Popup currentPopup = null;

    /**
     * Creates and shows a custom tooltip popup near the source
     * @param source The control that triggered the tooltip
     * @param message The message to display
     */
    private void showCustomTooltip(Object source, String message) {
        // Remove any existing popup
        hideCurrentPopup();

        // Create popup content
        VBox popupContent = new VBox();
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setPadding(new Insets(10));
        popupContent.setBackground(new Background(new BackgroundFill(
                Color.LIGHTYELLOW, new CornerRadii(5), Insets.EMPTY)));

        // Add border
        popupContent.setStyle("-fx-border-color: #999999; -fx-border-width: 1px; -fx-border-radius: 5px;");

        Text text = new Text(message);
        popupContent.getChildren().add(text);

        // Create popup
        Popup popup = new Popup();
        popup.getContent().add(popupContent);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        // Position and show popup
        if (source instanceof Button) {
            Button button = (Button) source;
            popup.show(button.getScene().getWindow(),
                    button.localToScreen(button.getBoundsInLocal()).getMinX(),
                    button.localToScreen(button.getBoundsInLocal()).getMaxY() + 5);
        } else if (source instanceof ImageView) {
            ImageView imageView = (ImageView) source;
            popup.show(imageView.getScene().getWindow(),
                    imageView.localToScreen(imageView.getBoundsInLocal()).getMinX(),
                    imageView.localToScreen(imageView.getBoundsInLocal()).getMaxY() + 5);
        } else if (source instanceof Line) {
            Line line = (Line) source;
            popup.show(line.getScene().getWindow(),
                    line.localToScreen(line.getBoundsInLocal()).getCenterX(),
                    line.localToScreen(line.getBoundsInLocal()).getMaxY() + 5);
        }

        // Store current popup
        currentPopup = popup;
    }

    /**
     * Hides the current popup if it exists
     */
    private void hideCurrentPopup() {
        if (currentPopup != null) {
            currentPopup.hide();
            currentPopup = null;
        }
    }

    @FXML
    void showHomeTooltip(ActionEvent event) {
        showCustomTooltip(homeButton, "Navigate to Home Screen");
    }

    @FXML
    void showLogoutTooltip(ActionEvent event) {
        showCustomTooltip(logoutIcon2, "Log out from the application");
    }

    @FXML
    void showLoadTooltip(ActionEvent event) {
        showCustomTooltip(loadButton, "Load a saved circuit");
    }

    @FXML
    void showSaveTooltip(ActionEvent event) {
        showCustomTooltip(saveButton, "Save current circuit");
    }

    @FXML
    void showGridTooltip(ActionEvent event) {
        showCustomTooltip(toggleGridLinesItem, "Show or hide grid lines");
    }

    @FXML
    void showDarkModeTooltip(ActionEvent event) {
        showCustomTooltip(toggleDarkModeItem, "Switch between light and dark mode");
    }

    @FXML
    void showRunTooltip(ActionEvent event) {
        showCustomTooltip(runBtn, "Run circuit simulation");
    }

    @FXML
    void showClearTooltip(ActionEvent event) {
        showCustomTooltip(clearBtn, "Clear current circuit");
    }

    @FXML
    void showBatteryTooltip(MouseEvent event) {
        showCustomTooltip(batteryIcon, "Battery component - power source");
    }

    @FXML
    void showBulbTooltip(MouseEvent event) {
        showCustomTooltip(bulbIcon, "Light bulb component");
    }

    @FXML
    void showWireTooltip(MouseEvent event) {
        showCustomTooltip(wireIcon, "Wire - connects components");
    }

    @FXML
    void showSwitchTooltip(MouseEvent event) {
        showCustomTooltip(switchIcon, "Switch - controls circuit flow");
    }
    @FXML
    public void goToMainMenu(){
        try {
            Parent mainMenuRoot = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/mainmenu.fxml"));
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(new Scene(mainMenuRoot));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}