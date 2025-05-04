package org.example.circuit_project;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.circuit_project.Elements.Battery;
import org.example.circuit_project.Elements.Circuit;
import org.example.circuit_project.Elements.Component;
import org.example.circuit_project.Elements.Lightbulb;
import org.example.circuit_project.Elements.Wire;
import org.example.circuit_project.Elements.Switch;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.net.URL;
import java.util.*;


public class SandboxController implements Initializable {

    // FXML Injected UI Components
    @FXML public Pane batteryIcon;
    @FXML public Line wire;
    @FXML private Pane playgroundPane;
    @FXML private Canvas gridCanvas;
    @FXML private Button toggleDarkModeItem;
    @FXML private HBox componentTray;
    @FXML
    private Button simBtn;

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
    private static final String SWITCH_FXML = "/org/example/circuit_project/switchICON.fxml";
    static final double SNAP_RADIUS = 5.0;//snap will happen within 20 pixels
    private final Map<Node, List<Node>> snappedPartners = new HashMap<>();
    private final Map<Node, List<Node>> snapConnections = new HashMap<>();


    @FXML private Button logoutIcon2;
    @FXML private Button homeButton;
    @FXML private ImageView darkModeIcon;
    @FXML private Button toggleGridLinesItem;
    @FXML private Button loadButton;
    @FXML private Button saveButton;
    @FXML private Button clearBtn;

    Tooltip darkTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        return tooltip;
    }

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

        toggleDarkModeItem.setTooltip(darkTooltip("Toggle Light/Dark Mode"));
        toggleGridLinesItem.setTooltip(darkTooltip("Toggle Grid"));
        logoutIcon2.setTooltip(darkTooltip("Log Out"));
        saveButton.setTooltip(darkTooltip("Save Circuit"));
        loadButton.setTooltip(darkTooltip("Load Circuit"));
        clearBtn.setTooltip(darkTooltip("Clear Workspace"));
        homeButton.setTooltip(darkTooltip("Return to Main Menu")); //
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

        // Subtle semi-transparent stroke for a clean, high-quality look
        Color gridColor = darkMode
                ? Color.web("#3a3a3a", 0.35)  // softer gray for dark mode
                : Color.web("#cccccc", 0.4);  // soft light gray for light mode

        gc.setStroke(gridColor);
        gc.setLineWidth(1.0);

        double width = gridCanvas.getWidth();
        double height = gridCanvas.getHeight();

        for (double x = 0; x < width; x += GRID_SPACING) {
            gc.strokeLine(x, 0, x, height);
        }
        for (double y = 0; y < height; y += GRID_SPACING) {
            gc.strokeLine(0, y, width, y);
        }
    }


    /**
     * Toggles the visibility of the grid lines based on the user's checkbox selection.
     * Updates the canvas by redrawing the grid accordingly
     */
    @FXML
    private void onToggleGridLines() {
        gridLinesEnabled = !gridLinesEnabled;
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
            playgroundPane.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #121212, #1a1a1a);");
            toggleDarkModeItem.setTooltip(new Tooltip("Switch to Light Mode"));
            darkModeIcon.setImage(new Image(getClass().getResourceAsStream("/org/example/circuit_project/images/sun.png")));
        } else {
            playgroundPane.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #f5f5f5, #d9d9d9);");
            toggleDarkModeItem.setTooltip(new Tooltip("Switch to Dark Mode"));
            darkModeIcon.setImage(new Image(getClass().getResourceAsStream("/org/example/circuit_project/images/moon.png")));
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

    private void updateWireSnapPoints(Node wireNode, Line wireLine) {
        List<Node> snaps = findSnapPoints(wireNode);
        double offsetX = wireNode.getLayoutX();
        double offsetY = wireNode.getLayoutY();

        for (Node snap : snaps) {
            String id = snap.getId().toLowerCase();
            if (id.contains("start")) {
                snap.setLayoutX(wireLine.getStartX());
                snap.setLayoutY(wireLine.getStartY());
            } else if (id.contains("end")) {
                snap.setLayoutX(wireLine.getEndX());
                snap.setLayoutY(wireLine.getEndY());
            }
        }

    }

    private void updateWireSnapPointsFromPartner(Node node) {
        if (!(node instanceof Parent parent)) return;
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof Line line && "wire".equals(child.getId())) {
                updateWireSnapPoints(node, line);
            }
        }
    }
    private static class Delta {
        double x, y;
    }

    /**
     * Makes a node draggable
     *
     * @param node The node to make draggable
     */
    private void enableDrag(Node node) {
        final Delta dragDelta = new Delta();

        node.setOnMousePressed(event -> {
            node.toFront();
            dragDelta.x = event.getSceneX() - node.getLayoutX();
            dragDelta.y = event.getSceneY() - node.getLayoutY();
            node.setCursor(Cursor.MOVE);
            event.consume();
        });

        node.setOnMouseDragged(event -> {
            // Calculate the new position based on mouse drag
            double newX = event.getSceneX() - dragDelta.x;
            double newY = event.getSceneY() - dragDelta.y;

            // Store original position to calculate delta later
            double originalX = node.getLayoutX();
            double originalY = node.getLayoutY();

            // Temporarily move to new position to get accurate snap point positions
            node.setLayoutX(newX);
            node.setLayoutY(newY);

            // Default to the new position (non-snapped)
            double finalX = newX;
            double finalY = newY;

            boolean snapped = false;
            Node snappedToComponent = null;
            Node sourceSnapPoint = null;
            Node targetSnapPoint = null;

            // Find the closest snap
            double closestDistance = SNAP_RADIUS;

            // Get this component's snap points
            List<Node> thisSnapPoints = findSnapPoints(node);

            // Try to snap to another component
            for (Node thisSnap : thisSnapPoints) {
                // Get this snap point's position in scene coordinates
                Point2D thisScenePos = thisSnap.localToScene(0, 0);

                for (Node otherComponent : componentNodesMap.values()) {
                    // Skip self
                    if (otherComponent == node) continue;

                    List<Node> otherSnapPoints = findSnapPoints(otherComponent);

                    for (Node otherSnap : otherSnapPoints) {
                        // Get other snap point's position in scene coordinates
                        Point2D otherScenePos = otherSnap.localToScene(0, 0);

                        // Calculate distance between snap points
                        double distance = thisScenePos.distance(otherScenePos);

                        // If within snap radius and closer than previous matches
                        if (distance <= closestDistance) {
                            closestDistance = distance;

                            // Calculate the offset needed to align the snap points
                            double dx = otherScenePos.getX() - thisScenePos.getX();
                            double dy = otherScenePos.getY() - thisScenePos.getY();

                            // Update final position
                            finalX = newX + dx;
                            finalY = newY + dy;

                            snapped = true;
                            snappedToComponent = otherComponent;
                            sourceSnapPoint = thisSnap;
                            targetSnapPoint = otherSnap;
                        }
                    }
                }
            }

            // Clear previous connections for this component if we're snapping
            if (snapped) {
                clearSnapsFor(node);

                // Record new snap connection
                if (sourceSnapPoint != null && targetSnapPoint != null) {
                    snapConnections.computeIfAbsent(sourceSnapPoint, k -> new ArrayList<>()).add(targetSnapPoint);
                    snapConnections.computeIfAbsent(targetSnapPoint, k -> new ArrayList<>()).add(sourceSnapPoint);

                    // Record component partnership
                    snappedPartners.computeIfAbsent(node, k -> new ArrayList<>()).add(snappedToComponent);
                    snappedPartners.computeIfAbsent(snappedToComponent, k -> new ArrayList<>()).add(node);
                }
            }

            // Restore original position before applying final position
            node.setLayoutX(originalX);
            node.setLayoutY(originalY);

            // Calculate delta for connected components
            double dx = finalX - originalX;
            double dy = finalY - originalY;

            // Set final position
            node.setLayoutX(finalX);
            node.setLayoutY(finalY);

            // Move connected components if we moved
            if (dx != 0 || dy != 0) {
                Set<Node> visited = new HashSet<>();
                visited.add(node);
                moveSnappedPartners(node, dx, dy, visited);
            }

            // Update model
            Component model = findComponentById(node.getId());
            if (model != null) {
                model.setX(finalX);
                model.setY(finalY);
            }

            event.consume();
        });

        node.setOnMouseReleased(event -> node.setCursor(Cursor.HAND));
    }

    // Helper method to clear snap connections for a node
    private void clearSnapsFor(Node node) {
        // Find all snap points for this node
        List<Node> snapPoints = findSnapPoints(node);

        // Remove connections for each snap point
        for (Node snap : snapPoints) {
            List<Node> connections = snapConnections.getOrDefault(snap, new ArrayList<>());

            // For each connection, remove the back reference
            for (Node connectedSnap : new ArrayList<>(connections)) {
                List<Node> backConnections = snapConnections.getOrDefault(connectedSnap, new ArrayList<>());
                backConnections.remove(snap);

                if (backConnections.isEmpty()) {
                    snapConnections.remove(connectedSnap);
                } else {
                    snapConnections.put(connectedSnap, backConnections);
                }
            }

            // Remove this snap's connections
            snapConnections.remove(snap);
        }

        // Clear component partnerships for this node
        List<Node> partners = snappedPartners.getOrDefault(node, new ArrayList<>());
        for (Node partner : new ArrayList<>(partners)) {
            List<Node> backPartners = snappedPartners.getOrDefault(partner, new ArrayList<>());
            backPartners.remove(node);

            if (backPartners.isEmpty()) {
                snappedPartners.remove(partner);
            } else {
                snappedPartners.put(partner, backPartners);
            }
        }
        snappedPartners.remove(node);
    }

    private void moveSnappedPartners(Node node, double dx, double dy, Set<Node> visited) {
        List<Node> partners = snappedPartners.getOrDefault(node, List.of());

        for (Node partner : partners) {
            // Skip if already visited to prevent cycles
            if (visited.contains(partner)) continue;
            visited.add(partner);

            // Move the partner
            partner.setLayoutX(partner.getLayoutX() + dx);
            partner.setLayoutY(partner.getLayoutY() + dy);

            // Update model for partner
            Component model = findComponentById(partner.getId());
            if (model != null) {
                model.setX(partner.getLayoutX());
                model.setY(partner.getLayoutY());
            }

            // Update wire positions (if needed)
            if (partner instanceof Parent parent) {
                for (Node child : parent.getChildrenUnmodifiable()) {
                    if (child instanceof Line line && "wire".equals(child.getId())) {
                        double offsetX = partner.getLayoutX();
                        double offsetY = partner.getLayoutY();

                        Component comp = findComponentById(partner.getId());
                        if (comp instanceof Wire wire) {
                            wire.setStartX(line.getStartX() + offsetX);
                            wire.setStartY(line.getStartY() + offsetY);
                            wire.setEndX(line.getEndX() + offsetX);
                            wire.setEndY(line.getEndY() + offsetY);
                        }
                    }
                }
            }

            // Recursively move partners of this partner
            moveSnappedPartners(partner, dx, dy, visited);
        }
    }

    private List<Node> findSnapPoints(Node node) {
        List<Node> snapPoints = new ArrayList<>();
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                if (child.getId() != null && child.getId().toLowerCase().startsWith("snap")) {
                    snapPoints.add(child);

                     child.setStyle("-fx-fill: rgba(0, 0, 255, 0.3); -fx-stroke: blue;");
                }
            }
        }
        return snapPoints;
    }







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

            Node wireNode = componentNodesMap.get(wireId);
            if (wireNode != null) {
                // Update model
                Component c = findComponentById(wireId);
                if (c instanceof Wire wireModel) {
                    double offsetX = wireNode.getLayoutX();
                    double offsetY = wireNode.getLayoutY();
                    wireModel.setStartX(line.getStartX() + offsetX);
                    wireModel.setStartY(line.getStartY() + offsetY);
                    wireModel.setEndX(line.getEndX() + offsetX);
                    wireModel.setEndY(line.getEndY() + offsetY);
                }

                // Update snap points
                updateWireSnapPoints(wireNode, line);

                // Move connected nodes (if any)
                List<Node> partners = snappedPartners.getOrDefault(wireNode, new ArrayList<>());
                for (Node partner : partners) {
                    if (partner instanceof Parent parent) {
                        for (Node snap : findSnapPoints(partner)) {
                            // Check if this snap point overlaps with a wire snap point
                            for (Node wireSnap : findSnapPoints(wireNode)) {
                                Point2D wireScene = wireSnap.localToScene(wireSnap.getLayoutX(), wireSnap.getLayoutY());
                                Point2D partnerScene = snap.localToScene(snap.getLayoutX(), snap.getLayoutY());

                                if (wireScene.distance(partnerScene) < SNAP_RADIUS) {
                                    double dx = wireScene.getX() - partnerScene.getX();
                                    double dy = wireScene.getY() - partnerScene.getY();
                                    partner.setLayoutX(partner.getLayoutX() + dx);
                                    partner.setLayoutY(partner.getLayoutY() + dy);

                                    // Update partner model
                                    Component pComp = findComponentById(partner.getId());
                                    if (pComp != null) {
                                        pComp.setX(partner.getLayoutX());
                                        pComp.setY(partner.getLayoutY());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            event.consume();
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
     * Adds a new switch to the playground
     */
    @FXML
    public void switchClick(MouseEvent mouseEvent) {
        double x = 100;
        double y = 100;
        String switchId = "switch-" + UUID.randomUUID().toString();

        // Create UI component
        Node switchNode = loadComponent(SWITCH_FXML, x, y, switchId);

        if (switchNode != null) {
            // Create data model (initially closed)
            Switch switchComponent = new Switch(x, y, true);
            switchComponent.setId(switchId);

            // Set up click handler to toggle switch state
            setupSwitchToggle(switchNode, switchComponent);

            // Store component
            components.add(switchComponent);
            componentNodesMap.put(switchId, switchNode);
        }
    }

    /**
     * Set up toggle functionality for a switch
     * @param switchNode The switch UI node
     * @param switchComponent The switch data model
     */
    private void setupSwitchToggle(Node switchNode, Switch switchComponent) {
        // Find the lever line in the switch node
        Line leverLine = findSwitchLeverInNode(switchNode);
        if (leverLine == null) return;

        // Set up click handler to toggle switch
        switchNode.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to toggle
                boolean newState = switchComponent.toggle();
                updateSwitchVisual(leverLine, newState);
                event.consume();
            }
        });
    }

    /**
     * Update the visual state of a switch
     * @param leverLine The lever line UI element
     * @param closed Whether the switch is closed
     */
    private void updateSwitchVisual(Line leverLine, boolean closed) {
        if (closed) {
            // Closed position - horizontal line
            leverLine.setStartX(15.0);
            leverLine.setStartY(19.0);
            leverLine.setEndX(75.0);
            leverLine.setEndY(19.0);
        } else {
            // Open position - angled line
            leverLine.setStartX(15.0);
            leverLine.setStartY(19.0);
            leverLine.setEndX(65.0);
            leverLine.setEndY(5.0);
        }
    }

    /**
     * Utility to find the lever Line object within a Switch Node
     */
    private Line findSwitchLeverInNode(Node node) {
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                if (child instanceof Line && "leverLine".equals(child.getId())) {
                    return (Line) child;
                } else {
                    Line found = findSwitchLeverInNode(child);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }

// Add this method for loading switches from saved circuits
    /**
     * Add switches from a circuit to the playground
     */
    private void addSwitchesToPlayground(List<Switch> switches) {
        if (switches == null) return;

        for (Switch switchComponent : switches) {
            // Create UI component
            Node switchNode = loadComponent(SWITCH_FXML, switchComponent.getX(), switchComponent.getY(), switchComponent.getId());

            if (switchNode != null) {
                // Update visual state
                Line leverLine = findSwitchLeverInNode(switchNode);
                if (leverLine != null) {
                    updateSwitchVisual(leverLine, switchComponent.isClosed());
                }

                // Set up toggle functionality
                setupSwitchToggle(switchNode, switchComponent);

                // Store component
                components.add(switchComponent);
                componentNodesMap.put(switchComponent.getId(), switchNode);
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
                else if (component instanceof Switch) {
                    circuit.addSwitch((Switch) component);
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
                    Circuit.class, Battery.class, Lightbulb.class, Wire.class, Switch.class
            );

            // Create and configure the unmarshaller
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // Unmarshal (deserialize) the XML file into a Circuit object
            Circuit circuit = (Circuit) unmarshaller.unmarshal(file);

            // Clear current playground and data
            playgroundPane.getChildren().clear();
            components.clear();
            componentNodesMap.clear();

            drawGrid(gc, isDarkMode, gridLinesEnabled);



            // Load batteries
            if (circuit.getBatteries() != null) {
                for (Battery battery : circuit.getBatteries()) {
                    Node batteryNode = loadComponent(BATTERY_FXML, battery.getX(), battery.getY(), battery.getId());
                    if (batteryNode != null) {
                        components.add(battery);
                        componentNodesMap.put(battery.getId(), batteryNode);
                    }
                }
            }

            // Load lightbulbs
            if (circuit.getLightbulbs() != null) {
                for (Lightbulb lightbulb : circuit.getLightbulbs()) {
                    Node lightbulbNode = loadComponent(LIGHTBULB_FXML, lightbulb.getX(), lightbulb.getY(), lightbulb.getId());
                    if (lightbulbNode != null) {
                        components.add(lightbulb);
                        componentNodesMap.put(lightbulb.getId(), lightbulbNode);
                    }
                }
            }

            // Load wires
            if (circuit.getWires() != null) {
                for (Wire wire : circuit.getWires()) {
                    Node wireNode = loadComponent(WIRE_FXML, wire.getX(), wire.getY(), wire.getId());
                    if (wireNode != null) {
                        Line wireLine = findLineInNode(wireNode);
                        if (wireLine != null) {
                            double offsetX = wireNode.getLayoutX();
                            double offsetY = wireNode.getLayoutY();
                            wireLine.setStartX(wire.getStartX() - offsetX);
                            wireLine.setStartY(wire.getStartY() - offsetY);
                            wireLine.setEndX(wire.getEndX() - offsetX);
                            wireLine.setEndY(wire.getEndY() - offsetY);

                            enableLineResize(wireLine, wire.getId());
                        }

                        components.add(wire);
                        componentNodesMap.put(wire.getId(), wireNode);
                    }
                }
            }

            // Load switches
            if (circuit.getSwitches() != null) {
                addSwitchesToPlayground(circuit.getSwitches());
            }

        } catch (Exception e) {
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
        addSwitchesToPlayground(circuit.getSwitches());
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

    // Methods for Voltmeter

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

    @FXML
    public void logoutUser2() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/org/example/circuit_project/login.fxml"));
            Stage stage = (Stage) logoutIcon2.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            showAlert("Navigation Error", "Failed to load Main Menu", e.getMessage());
        }
    }
    @FXML
    private void simulateCircuit() {
        Map<String, Component> componentMap = new HashMap<>();
        for (Component c : components) {
            componentMap.put(c.getId(), c);
        }

        for (Component c : components) {
            if (c instanceof Battery battery) {
                simulateFromBattery(battery, componentMap, new HashSet<>());
            }
        }
    }

    private void simulateFromBattery(Battery battery, Map<String, Component> all, Set<String> visited) {
        Queue<String> toVisit = new LinkedList<>();
        toVisit.add(battery.getId());
        visited.add(battery.getId());

        while (!toVisit.isEmpty()) {
            String currentId = toVisit.poll();
            Component current = all.get(currentId);

            for (Wire wire : getConnectedWires(currentId)) {
                String nextId = wire.getFromComponentId().equals(currentId) ? wire.getToComponentId() : wire.getFromComponentId();
                if (!visited.contains(nextId)) {
                    Component next = all.get(nextId);
                    if (next instanceof Switch s && !s.isClosed()) continue;
                    if (next instanceof Lightbulb b) b.setOn(true);
                    visited.add(nextId);
                    toVisit.add(nextId);
                }
            }
        }
    }

    private List<Wire> getConnectedWires(String componentId) {
        List<Wire> result = new ArrayList<>();
        for (Component c : components) {
            if (c instanceof Wire wire) {
                if (componentId.equals(wire.getFromComponentId()) || componentId.equals(wire.getToComponentId())) {
                    result.add(wire);
                }
            }
        }
        return result;
    }
}