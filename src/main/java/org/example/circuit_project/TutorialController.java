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

/**
 * Controller for the tutorial screen in the circuit simulator application.
 * <p>
 * Provides interactive tooltip-based guidance for buttons and components to help
 * users understand the UI. Also includes navigation back to the main menu.
 *
 * Features:
 * <ul>
 *     <li>Custom tooltips for each button and icon</li>
 *     <li>Popup styling and dynamic positioning near the hovered control</li>
 *     <li>Main menu navigation</li>
 * </ul>
 */
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
     * Displays a custom-styled tooltip popup near the specified UI control.
     *
     * @param source  The UI control (Button, ImageView, or Line) that triggered the tooltip
     * @param message The tooltip message to display
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
     * Hides the currently displayed tooltip popup, if any.
     */
    private void hideCurrentPopup() {
        if (currentPopup != null) {
            currentPopup.hide();
            currentPopup = null;
        }
    }

    /** Shows tooltip for the Home button. */
    @FXML
    void showHomeTooltip(ActionEvent event) {
        showCustomTooltip(homeButton, "Navigate to Home Screen");
    }

    /** Shows tooltip for the Logout button. */
    @FXML
    void showLogoutTooltip(ActionEvent event) {
        showCustomTooltip(logoutIcon2, "Log out from the application");
    }

    /** Shows tooltip for the Load button. */
    @FXML
    void showLoadTooltip(ActionEvent event) {
        showCustomTooltip(loadButton, "Load a saved circuit");
    }

    /** Shows tooltip for the Save button. */
    @FXML
    void showSaveTooltip(ActionEvent event) {
        showCustomTooltip(saveButton, "Save current circuit");
    }

    /** Shows tooltip for the Grid toggle button. */
    @FXML
    void showGridTooltip(ActionEvent event) {
        showCustomTooltip(toggleGridLinesItem, "Show or hide grid lines");
    }

    /** Shows tooltip for the Dark Mode toggle button. */
    @FXML
    void showDarkModeTooltip(ActionEvent event) {
        showCustomTooltip(toggleDarkModeItem, "Switch between light and dark mode");
    }

    /** Shows tooltip for the Run button. */
    @FXML
    void showRunTooltip(ActionEvent event) {
        showCustomTooltip(runBtn, "Run circuit simulation");
    }

    /** Shows tooltip for the Clear button. */
    @FXML
    void showClearTooltip(ActionEvent event) {
        showCustomTooltip(clearBtn, "Clear current circuit");
    }

    /** Shows tooltip when hovering over the battery icon. */
    @FXML
    void showBatteryTooltip(MouseEvent event) {
        showCustomTooltip(batteryIcon, "Battery component - power source");
    }

    /** Shows tooltip when hovering over the bulb icon. */
    @FXML
    void showBulbTooltip(MouseEvent event) {
        showCustomTooltip(bulbIcon, "Light bulb component");
    }

    /** Shows tooltip when hovering over the wire icon. */
    @FXML
    void showWireTooltip(MouseEvent event) {
        showCustomTooltip(wireIcon, "Wire - connects components");
    }

    /** Shows tooltip when hovering over the switch icon. */
    @FXML
    void showSwitchTooltip(MouseEvent event) {
        showCustomTooltip(switchIcon, "Switch - controls circuit flow");
    }

    /**
     * Navigates the user back to the main menu screen.
     */
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