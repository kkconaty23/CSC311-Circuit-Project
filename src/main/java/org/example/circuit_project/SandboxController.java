package org.example.circuit_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class SandboxController implements Initializable {

    @FXML
    public Pane batteryIcon;

    @FXML
    public Line wire;
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

    @FXML
    private HBox componentTray;

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



    public void loadComponent(String fxmlPath, double x, double y) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node component = loader.load();  // We use Node instead of Pane to handle both types.

            // Set position
            component.setLayoutX(x);
            component.setLayoutY(y);

            // Check if the component is a Line (wire)
            if (component instanceof Line) {
                Line wire = (Line) component;
                enableLineResize(wire);  // Enable resizing for the wire
            }

            // Enable dragging for all components
            enableDrag(component);

            // Add to the playground
            playgroundPane.getChildren().add(component);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enableDrag(Node node) {
        final Delta dragDelta = new Delta();

        node.setOnMousePressed(event -> {
            dragDelta.x = event.getSceneX() - node.getLayoutX();
            dragDelta.y = event.getSceneY() - node.getLayoutY();
        });

        node.setOnMouseDragged(event -> {
            node.setLayoutX(event.getSceneX() - dragDelta.x);
            node.setLayoutY(event.getSceneY() - dragDelta.y);
        });
    }

    private void enableLineResize(Line line) {
        // Flag to indicate which end of the line is being resized
        final boolean[] isResizingStart = {false};  // Initially, we're not resizing the start

        // Handle mouse pressed event on the line
        line.setOnMousePressed(event -> {
            double startX = line.getStartX();
            double startY = line.getStartY();
            double endX = line.getEndX();
            double endY = line.getEndY();

            // Check if the mouse is close to the start or end of the line
            double startDist = Math.hypot(event.getSceneX() - startX, event.getSceneY() - startY);
            double endDist = Math.hypot(event.getSceneX() - endX, event.getSceneY() - endY);

            // Set the resizing flag based on proximity to the start or end
            isResizingStart[0] = startDist < endDist;  // If closer to the start, resize start
        });

        // Handle mouse drag event on the line
        line.setOnMouseDragged(event -> {
            if (isResizingStart[0]) {
                // Resize the start of the line
                line.setStartX(event.getSceneX());
                line.setStartY(event.getSceneY());
            } else {
                // Resize the end of the line
                line.setEndX(event.getSceneX());
                line.setEndY(event.getSceneY());
            }
        });
    }


    // Helper method to create resize handles
    private Rectangle createResizeHandle(double x, double y) {
        Rectangle handle = new Rectangle(x - 5, y - 5, 10, 10);  // Create a small square handle
        handle.setFill(Color.RED);  // Make it visible
        handle.setCursor(Cursor.HAND);  // Make it look draggable
        return handle;
    }

    @FXML
    public void batteryClick(MouseEvent mouseEvent) {
        System.out.println("Battery icon clicked");
        loadComponent("/org/example/circuit_project/batteryICON.fxml", 100, 100);
    }

    @FXML
    public void lightbulbClick(MouseEvent mouseEvent) {
        System.out.println("Lightbulb icon clicked");
        loadComponent("/org/example/circuit_project/lightbulbICON.fxml", 100, 100);
    }

    @FXML
    public void wireClick(MouseEvent mouseEvent) {
        System.out.println("Wire icon clicked");
        loadComponent("/org/example/circuit_project/wireICON.fxml", 100, 100);
    }

    private static class Delta {
        double x, y;
    }


}
