package org.example.circuit_project;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.net.URL;
import java.util.ResourceBundle;


public class SandboxController implements Initializable {

    // Main Playground
    @FXML private Pane playgroundPane;

    // Grid Lines
    @FXML private Canvas gridCanvas;

    // Grid Lines Toggle
    @FXML private CheckMenuItem toggleGridLinesItem;

    // Dark / Light Mode
    @FXML private MenuItem toggleDarkModeItem;
    private boolean isDarkMode = false;
    private GraphicsContext gc;
    private boolean gridLinesEnabled = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = gridCanvas.getGraphicsContext2D();
        drawGrid(gc, isDarkMode, gridLinesEnabled); // Draw the grid initially in light mode
    }

    /**
     * Draws the grid on the canvas
     * @param gc the GraphicsContext used to draw the grid
     * @param darkMode true if dark mode is enabled; affects the grid color
     * @param gridOn true if the grid should be drawn; false to hide it
     */
    private void drawGrid(GraphicsContext gc,boolean darkMode, boolean gridOn) {

        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        if (!gridOn) return;

        gc.setStroke(darkMode ? Color.web("#9ca3af") : Color.DARKGRAY);
        gc.setLineWidth(1.5);

        double gridSpacing = 25;
        for (double x = 0; x < gridCanvas.getWidth(); x += gridSpacing) {
            gc.strokeLine(x, 0, x, gridCanvas.getHeight());
        }
        for (double y = 0; y < gridCanvas.getHeight(); y += gridSpacing) {
            gc.strokeLine(0, y, gridCanvas.getWidth(), y);
        }
    }

    /**
     * Toggles the visibility of the grid lines based on the user's checkbox selection.
     * Updates the canvas by redrawing the grid accordingly
     */
    @FXML
    private void onToggleGridLines(){
        gridLinesEnabled = toggleGridLinesItem.isSelected();
        drawGrid(gc, isDarkMode, gridLinesEnabled);
    }

    /**
     * Toggles between dark and light mode for the playground UI.
     * Updates the background color and button label, and redraws the grid to match the theme.
     */
    @FXML
    private void onToggleDarkMode() {
        isDarkMode = !isDarkMode;

        if (isDarkMode) {
            playgroundPane.setStyle("-fx-background-color: #0D1117;");
            toggleDarkModeItem.setText("Light Mode");
        } else {
            playgroundPane.setStyle("-fx-background-color: #BFBFBF;");
            toggleDarkModeItem.setText("Dark Mode");
        }

        drawGrid(gc, isDarkMode, gridLinesEnabled);
    }


}
