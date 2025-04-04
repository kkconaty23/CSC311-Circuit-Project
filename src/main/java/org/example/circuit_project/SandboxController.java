package org.example.circuit_project;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
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
    @FXML private CheckMenuItem gridToggle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        drawGrid(true);

        gridToggle.setOnAction(e -> {
            boolean showGrid = gridToggle.isSelected();
            drawGrid(showGrid);
        });


    }

    /**
     * drawGrid
     * Main Function: Create Grid if (show) is true, if false, clear Grid.
     * Show is connected directly to CheckMenuItem selection (check mark)
     * @param show
     */
    private void drawGrid(boolean show) {
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        // If box is checked show grid
        if(show){
            gc.setStroke(Color.rgb(180, 180, 180));
            gc.setLineWidth(1);

            for (int x = 0; x < gridCanvas.getWidth(); x+= 20) {
                gc.strokeLine(x, 0, x, gridCanvas.getHeight());
            }

            for(int y = 0; y < gridCanvas.getHeight(); y+= 20){
                gc.strokeLine(0, y, gridCanvas.getWidth(), y);
            }
        }
    }
}
