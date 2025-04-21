package org.example.circuit_project;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.example.circuit_project.Elements.Battery;
import org.example.circuit_project.Elements.Circuit;
import org.example.circuit_project.Elements.Component;
import org.example.circuit_project.Elements.Lightbulb;
import org.example.circuit_project.Elements.Wire;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

public class SandboxController implements Initializable {

    // FXML Injected UI Components
    @FXML public Pane batteryIcon;
    @FXML public Line wire;
    @FXML private Pane playgroundPane;
    @FXML private Canvas gridCanvas;
    @FXML private CheckMenuItem toggleGridLinesItem;
    @FXML private MenuItem toggleDarkModeItem;
    @FXML private HBox componentTray;

    //circuit Components
    private final List<Component> components = new ArrayList<>();
    private final Map<String, Node> componentNodesMap = new HashMap<>(); // Maps component IDs to UI nodes

    // UI State
    private GraphicsContext gc;
    private boolean isDarkMode = false;
    private boolean gridLinesEnabled = true;
    private static final double GRID_SPACING = 25.0;

    //component Types
    private static final String BATTERY_FXML = "/org/example/circuit_project/batteryICON.fxml";
    private static final String LIGHTBULB_FXML = "/org/example/circuit_project/lightbulbICON.fxml";
    private static final String WIRE_FXML = "/org/example/circuit_project/wireICON.fxml";

    /**
     * initialize method used for setting the UI on boot up
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = gridCanvas.getGraphicsContext2D();
        drawGrid(gc, isDarkMode, gridLinesEnabled);

        gridCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawGrid(gc, isDarkMode, gridLinesEnabled));
        gridCanvas.heightProperty().addListener((obs, oldVal, newVal) -> drawGrid(gc, isDarkMode, gridLinesEnabled));

        gridCanvas.widthProperty().bind(playgroundPane.widthProperty());
        gridCanvas.heightProperty().bind(playgroundPane.heightProperty());

        drawGrid(gc, isDarkMode, gridLinesEnabled);
    }

    /**
     * Draws the grid on the canvas
     *
     * @param gc       the GraphicsContext used to draw the grid
     * @param darkMode true if dark mode is enabled; affects the grid color
     * @param gridOn   true if the grid should be drawn; false to hide it
     */
    private void drawGrid(GraphicsContext gc, boolean darkMode, boolean gridOn) {
        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        if (!gridOn) return;

        gc.setStroke(darkMode ? Color.web("#9ca3af") : Color.DARKGRAY);
        gc.setLineWidth(1.5);

        for (double x = 0; x < gridCanvas.getWidth(); x += GRID_SPACING) {
            gc.strokeLine(x, 0, x, gridCanvas.getHeight());
        }
        for (double y = 0; y < gridCanvas.getHeight(); y += GRID_SPACING) {
            gc.strokeLine(0, y, gridCanvas.getWidth(), y);
        }
    }

    /**
     * Toggles the visibility of the grid lines based on the user's checkbox selection.
     * Updates the canvas by redrawing the grid accordingly
     */
    @FXML
    private void onToggleGridLines() {
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

    @FXML
    private void clearBtnClick(){
        clearPlayground();
    }

    /**
     * Loads a component from an FXML file and adds it to the playground
     *
     * @param fxmlPath Path to the FXML file to load
     * @param x X position for the component
     * @param y Y position for the component
     * @param componentId Unique ID for the component, or null to generate one
     * @return The loaded Node or null if loading failed
     */
    private Node loadComponent(String fxmlPath, double x, double y, String componentId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node component = loader.load(); // Changed from Parent to Node

            // Generate or use provided ID
            String id = componentId != null ? componentId : UUID.randomUUID().toString();
            component.setId(id);

            component.setLayoutX(x);
            component.setLayoutY(y);

            component.setUserData("dropped"); //Marks the entity as "dropped"

            enableDrag(component);
            playgroundPane.getChildren().add(component);

            return component;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load component", e.getMessage());
            return null;
        }
    }

    /**
     * Makes a node draggable
     *
     * @param node The node to make draggable
     */
    private void enableDrag(Node node) {
        final Delta dragDelta = new Delta();

        node.setOnMousePressed(event -> {
            // Bring to front when selected
            node.toFront();
            dragDelta.x = event.getSceneX() - node.getLayoutX();
            dragDelta.y = event.getSceneY() - node.getLayoutY();
            node.setCursor(Cursor.MOVE);
            event.consume();
        });

        node.setOnMouseDragged(event -> {
            node.setLayoutX(event.getSceneX() - dragDelta.x);
            node.setLayoutY(event.getSceneY() - dragDelta.y);

            // Update component model with new position
            String nodeId = node.getId();
            Component component = findComponentById(nodeId);
            if (component != null) {
                component.setX(node.getLayoutX());
                component.setY(node.getLayoutY());
            }

            event.consume();
        });

        node.setOnMouseReleased(event -> {
            node.setCursor(Cursor.HAND);
        });
    }

    /**
     * MAKING IT MORE SNAPPY WITH THE GRIDS (_OPTIONAL_)
     * Aligns a value to the nearest grid position
     */
//    private double alignToGrid(double value) {
//        return Math.round(value / GRID_SPACING) * GRID_SPACING;
//    }

    /**
     * Enables resizing of line components (wires)
     *NEEDS SOME WORK TO MAKE IT ON THE LINE ADJUSTMENTS
     * @param line The line to make resizable
     * @
     */
    private void enableLineResize(Line line, String wireId) {
        final boolean[] resizingStart = new boolean[1]; // true = resizing start, false = end
        final Delta dragDelta = new Delta();

        line.setOnMousePressed(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            double startX = line.getStartX();
            double startY = line.getStartY();
            double endX = line.getEndX();
            double endY = line.getEndY();

            // Decide which endpoint is being dragged
            resizingStart[0] = Math.hypot(mouseX - startX, mouseY - startY) <
                    Math.hypot(mouseX - endX, mouseY - endY);

            dragDelta.x = mouseX;
            dragDelta.y = mouseY;

            event.consume();
        });

        line.setOnMouseDragged(event -> {
            double newX = event.getX();
            double newY = event.getY();

            if (resizingStart[0]) {
                line.setStartX(newX);
                line.setStartY(newY);
            } else {
                line.setEndX(newX);
                line.setEndY(newY);
            }

            // 🔥 Update the corresponding Wire's absolute start/end points
            Wire wire = (Wire) findComponentById(wireId);
            Node wireNode = componentNodesMap.get(wireId);
            if (wire != null && wireNode != null) {
                double offsetX = wireNode.getLayoutX();
                double offsetY = wireNode.getLayoutY();

                wire.setStartX(line.getStartX() + offsetX);
                wire.setStartY(line.getStartY() + offsetY);
                wire.setEndX(line.getEndX() + offsetX);
                wire.setEndY(line.getEndY() + offsetY);
            }

            event.consume();
        });

        line.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            double startX = line.getStartX();
            double startY = line.getStartY();
            double endX = line.getEndX();
            double endY = line.getEndY();

            if (Math.hypot(mouseX - startX, mouseY - startY) < 10 ||
                    Math.hypot(mouseX - endX, mouseY - endY) < 10) {
                line.setCursor(Cursor.CROSSHAIR);
            } else {
                line.setCursor(Cursor.DEFAULT);
            }
        });
    }



    /**
     * Creates a resize handle for line endpoints
     */
    private Rectangle createResizeHandle(double x, double y) {
        Rectangle handle = new Rectangle(x - 5, y - 5, 10, 10);
        handle.setFill(Color.BLUE);
        handle.setOpacity(0.7);
        handle.setCursor(Cursor.CROSSHAIR);
        return handle;
    }

    /**
     * Finds a component by its ID
     */
    private Component findComponentById(String id) {
        for (Component component : components) {
            if (component.getId().equals(id)) {
                return component;
            }
        }
        return null;
    }

    /**
     * Adds a new battery to the playground in the form of FXML icon
     * and also creates a new battery instance
     */
    @FXML
    public void batteryClick(MouseEvent mouseEvent) {
        double x = 100;
        double y = 100;
        String batteryId = "battery-" + UUID.randomUUID().toString();

        // Create UI component
        Node batteryNode = loadComponent(BATTERY_FXML, x, y, batteryId);

        if (batteryNode != null) {
            // Create data model
            Battery battery = new Battery(x, y, 9.0); // Default 9V battery
            battery.setId(batteryId);

            // store component SO IT CAN BE RELOADED
            components.add(battery);
            componentNodesMap.put(batteryId, batteryNode);
        }
    }

    /**
     * Adds a new lightbulb to the playground
     */
    @FXML
    public void lightbulbClick(MouseEvent mouseEvent) {
        double x = 100;
        double y = 100;
        String lightbulbId = "lightbulb-" + UUID.randomUUID().toString();

        // Create UI component
        Node lightbulbNode = loadComponent(LIGHTBULB_FXML, x, y, lightbulbId);

        if (lightbulbNode != null) {
            // Create data model
            Lightbulb lightbulb = new Lightbulb(x, y);
            lightbulb.setId(lightbulbId);

            // Store component
            components.add(lightbulb);
            componentNodesMap.put(lightbulbId, lightbulbNode);
        }
    }


    /**
     * creates a new dragable wire and creates a wire java obj
     * @param mouseEvent
     */
    @FXML
    public void wireClick(MouseEvent mouseEvent) {
        double x = 100;
        double y = 100;
        String wireId = "wire-" + UUID.randomUUID().toString();

        Node wireNode = loadComponent(WIRE_FXML, x, y, wireId);

        if (wireNode != null) {
            Line wireLine = findLineInNode(wireNode);

            if (wireLine != null) {
                Wire wire = new Wire(
                        wireLine.getStartX() + x,
                        wireLine.getStartY() + y,
                        wireLine.getEndX() + x,
                        wireLine.getEndY() + y
                );
                wire.setId(wireId);
                wire.setX(x);
                wire.setY(y);

                // Pass the wireId so resizing updates the model
                enableLineResize(wireLine, wireId);

                components.add(wire);
                componentNodesMap.put(wireId, wireNode);
            }
        }
    }


    /**
     * Utility to find a Line object within a Node hierarchy
     */
    private Line findLineInNode(Node node) {
        if (node instanceof Line) {
            return (Line) node;
        } else if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                Line found = findLineInNode(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Delta class used for dragging operations
     */
    private static class Delta {
        double x, y;
    }


    /**
     * Show the save file dialog and save the circuit to you local machine (currently)
     */
    @FXML
    public void saveBtnClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Circuit");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );
        fileChooser.setInitialFileName("circuit.xml");

        File file = fileChooser.showSaveDialog(playgroundPane.getScene().getWindow());

        if (file != null) {
            try {
                saveCircuitToXML(file);
                showAlert("Success", "Circuit Saved", "Circuit saved successfully to " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Save Failed", "Failed to save circuit: " + e.getMessage());
            }
        }
    }

    /**
     * Show the load file dialog and load a circuit
     * pulls up file explorer and allows you to open previous builds
     * POTENTIALLY KEEP TIS WAY SINCE IT IS A DESKTOP APPLICATION
     */
    @FXML
    public void loadBtnClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Circuit");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );

        File file = fileChooser.showOpenDialog(playgroundPane.getScene().getWindow());

        if (file != null) {
            try {
                loadCircuitFromXML(file);
                showAlert("Success", "Circuit Loaded", "Circuit loaded successfully from " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Load Failed", "Failed to load circuit: " + e.getMessage());
            }
        }
    }

    /**
     * Save the current circuit to XML
     * creates a new circuit xml file that the user can rename
     */
    public void saveCircuitToXML(File file) {
        try {

            Circuit circuit = new Circuit(); //create a new circuit object with all components
            circuit.setName("Circuit " + System.currentTimeMillis());
            circuit.setId(System.currentTimeMillis());

            // Add all components to the circuit
            for (Component component : components) {
                if (component instanceof Battery) {
                    circuit.addBattery((Battery) component);
                } else if (component instanceof Lightbulb) {
                    circuit.addLightbulb((Lightbulb) component);
                } else if (component instanceof Wire) {
                    circuit.addWire((Wire) component);
                }
            }

            // Create JAXB context for all relevant classes
            JAXBContext context = JAXBContext.newInstance(
                    Circuit.class, Battery.class, Lightbulb.class, Wire.class
            );

            // Create and configure the marshaller
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Marshal the circuit to XML
            marshaller.marshal(circuit, file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save circuit: " + e.getMessage(), e);//error checking
        }
    }

    /**
     * Load a circuit from XML
     */
    public void loadCircuitFromXML(File file) {
        try {
            // Create JAXB context for all relevant classes
            JAXBContext context = JAXBContext.newInstance(
                    Circuit.class, Battery.class, Lightbulb.class, Wire.class
            );

            // Create unmarshaller
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // Unmarshal the XML file to a Circuit object
            Circuit circuit = (Circuit) unmarshaller.unmarshal(file);

            // Update the UI with the loaded circuit
            updateUIWithCircuit(circuit);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load circuit: " + e.getMessage(), e);
        }
    }

    /**
     * Update the UI with components from a loaded circuit
     * puts the components back into their position
     */
    private void updateUIWithCircuit(Circuit circuit) {

        clearPlayground(); //clear the playground

        //get all componentes from the circuit xml and add the m to correct position
        addBatteriesToPlayground(circuit.getBatteries());
        addLightbulbsToPlayground(circuit.getLightbulbs());
        addWiresToPlayground(circuit.getWires());
    }

    /**
     * Clear the playground of all components
     */
    private void clearPlayground() {
        // Remove all nodes except the grid canvas
        playgroundPane.getChildren().removeIf(node -> {
            return "dropped".equals(node.getUserData());
                });

        // Clear component collections
        components.clear();
        componentNodesMap.clear();
    }

    /**
     * helper method
     * Add batteries from a circuit to the playground
     */
    private void addBatteriesToPlayground(List<Battery> batteries) {
        if (batteries == null) return;

        for (Battery battery : batteries) {
            // Create UI component
            Node batteryNode = loadComponent(BATTERY_FXML, battery.getX(), battery.getY(), battery.getId());

            if (batteryNode != null) {
                // Store component
                components.add(battery);
                componentNodesMap.put(battery.getId(), batteryNode);
            }
        }
    }

    /**
     * helper method
     * Add lightbulbs from a circuit to the playground
     */
    private void addLightbulbsToPlayground(List<Lightbulb> lightbulbs) {
        if (lightbulbs == null) return;

        for (Lightbulb lightbulb : lightbulbs) {
            // Create UI component
            Node lightbulbNode = loadComponent(LIGHTBULB_FXML, lightbulb.getX(), lightbulb.getY(), lightbulb.getId());

            if (lightbulbNode != null) {
                // Store component
                components.add(lightbulb);
                componentNodesMap.put(lightbulb.getId(), lightbulbNode);
            }
        }
    }

    /**
     * helper method
     * Add wires from a circuit to the playground
     */
    private void addWiresToPlayground(List<Wire> wires) {
        if (wires == null) return;

        for (Wire wire : wires) {
            // Create UI component at the wire's position
            Node wireNode = loadComponent(WIRE_FXML, wire.getX(), wire.getY(), wire.getId());

            if (wireNode != null) {
                // Find the Line within the loaded FXML
                Line wireLine = findLineInNode(wireNode);

                if (wireLine != null) {
                    // Calculate the offset-adjusted positions
                    // The line's endpoints are relative to its container's position
                    double startX = wire.getStartX() - wire.getX();
                    double startY = wire.getStartY() - wire.getY();
                    double endX = wire.getEndX() - wire.getX();
                    double endY = wire.getEndY() - wire.getY();

                    // Update line endpoints
                    wireLine.setStartX(startX);
                    wireLine.setStartY(startY);
                    wireLine.setEndX(endX);
                    wireLine.setEndY(endY);

                    // Enable line resizing
                    enableLineResize(wireLine, wire.getId());

                    // Store component
                    components.add(wire);
                    componentNodesMap.put(wire.getId(), wireNode);
                }
            }
        }
    }

    /**
     * Show an alert dialog
     */
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}