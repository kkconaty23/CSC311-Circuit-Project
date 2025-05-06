package org.example.circuit_project;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.circuit_project.Components.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.*;
import java.net.URL;
import java.util.*;

import static java.lang.Math.clamp;


// Serializable component and wire structure
class SerializableComponent {
    public String id, type;
    public double x, y, voltage;
    public boolean isClosed;
}

class SerializableWire {
    public String startComponentId;
    public int startPortIndex;
    public String endComponentId;
    public int endPortIndex;
    public double startX, startY, endX, endY;
}

public class SandboxController implements Initializable {

    // FXML Injected UI Components
    @FXML private Pane playgroundPane;
    @FXML private Canvas gridCanvas;
    @FXML private Button toggleDarkModeItem;
    @FXML private HBox componentTray;
    @FXML
    private Button simBtn;


    // UI State
    private GraphicsContext gc;
    private boolean isDarkMode = false;
    private boolean gridLinesEnabled = true;
    private static final double GRID_SPACING = 25.0;


    @FXML private Button logoutIcon2;
    @FXML private Button homeButton;
    @FXML private ImageView darkModeIcon;
    @FXML private Button toggleGridLinesItem;
    @FXML private Button loadButton;
    @FXML private Button saveButton;
    @FXML private Button clearBtn;

    // Elements
    @FXML public ImageView batteryIcon;
    @FXML private Line wireIcon;
    @FXML public ImageView bulbIcon;
    @FXML public ImageView switchIcon;

    private Port firstSelectedPort = null;
    private Button runBtn;



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
//        saveButton.setTooltip(darkTooltip("Save Circuit"));
//        loadButton.setTooltip(darkTooltip("Load Circuit"));
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
    private void clearBtnClick() {
        // 1. Remove all nodes from the canvas
        playgroundPane.getChildren().clear();

        // 2. (Optional) Clear additional state if needed
        System.out.println("🧼 Canvas cleared!");
    }



    @FXML
    public void saveBtnClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Circuit Layout");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(playgroundPane.getScene().getWindow());

        if (file != null) {
            try {
                saveLayoutToFile(file);
                showAlert("Success", "Layout Saved", "Circuit layout saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Save Failed", e.getMessage());
            }
        }
    }

    @FXML
    public void loadBtnClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Circuit Layout");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(playgroundPane.getScene().getWindow());

        if (file != null) {
            try {
                loadLayoutFromFile(file);
                showAlert("Success", "Layout Loaded", "Circuit layout loaded successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Load Failed", e.getMessage());
            }
        }
    }



    /**
     * Clear the playground of all components
     */
    private void clearPlayground() {
        // Remove all nodes except the grid canvas
        playgroundPane.getChildren().removeIf(node -> {
            return "dropped".equals(node.getUserData());
                });

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
    private void onComponentDragStart(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        Point2D localCoords = playgroundPane.sceneToLocal(event.getSceneX(), event.getSceneY());

        if (sourceNode instanceof Line && sourceNode == wireIcon) {
            Line newLine = new Line(0, 0, 80, 0);
            newLine.setStrokeWidth(3);
            newLine.setStroke(Color.BLACK);
            newLine.setLayoutX(localCoords.getX());
            newLine.setLayoutY(localCoords.getY());

            Wire newWire = new Wire(newLine);
            enableLineDrag(newLine);
            playgroundPane.getChildren().add(newLine);

        } else if (sourceNode instanceof ImageView sourceIcon) {
            Component newComponent = createComponentFromSource(sourceIcon, localCoords);
            if (newComponent != null) {
                ImageView view = newComponent.getView();
                view.setLayoutX(localCoords.getX() - 160);
                view.setLayoutY(localCoords.getY() - 200);
                view.setCursor(Cursor.HAND);
                view.setUserData(newComponent);
                playgroundPane.getChildren().add(view);
                enableDrag(view);

                view.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) {
                        if (view.getUserData() instanceof Component c) {
                            c.disconnect();
                            c.reset();
                            c.updateVisualState();
                            System.out.println("🔌 Component disconnected by double-click.");
                        }
                    }
                });

                for (Port port : newComponent.getPorts()) {
                    Circle portCircle = new Circle(6);
                    portCircle.setFill(Color.RED);
                    double x = view.getLayoutX() + port.getXOffset() * view.getFitWidth();
                    double y = view.getLayoutY() + port.getYOffset() * view.getFitHeight();
                    portCircle.setLayoutX(x);
                    portCircle.setLayoutY(y);
                    port.setCircle(portCircle);
                    portCircle.setUserData(port);
                    playgroundPane.getChildren().add(portCircle);
                }
            }
        }
    }


        /**
         * Enables dragging for a component node and automatically updates the position
         * of its ports and any connected wires during the drag.
         *
         * @param node The component node to make draggable.
         */

    private void enableDrag(Node node) {
        final Delta dragDelta = new Delta();

        node.setOnMousePressed(e -> {
            dragDelta.x = e.getSceneX();
            dragDelta.y = e.getSceneY();
        });

        node.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - dragDelta.x;
            double dy = e.getSceneY() - dragDelta.y;

            dragDelta.x = e.getSceneX();
            dragDelta.y = e.getSceneY();

            if (node instanceof ImageView view && view.getUserData() instanceof Component component) {
                Set<Component> connected = getAllConnectedComponents(component);
                for (Component c : connected) {
                    if (c.getView() != null) {
                        ImageView v = c.getView();
                        v.setLayoutX(v.getLayoutX() + dx);
                        v.setLayoutY(v.getLayoutY() + dy);
                    }

                    for (Port port : c.getPorts()) {
                        if (port.getCircle() != null) {
                            Circle circle = port.getCircle();
                            circle.setLayoutX(circle.getLayoutX() + dx);
                            circle.setLayoutY(circle.getLayoutY() + dy);
                        }
                    }

                    if (c instanceof Wire wire) {
                        wire.updateLinePosition();
                    }
                }
            }
        });
    }



    private static class Delta {
        double x, y;
    }

    private Component createComponentFromSource(ImageView sourceIcon, Point2D dropPosition) {
        String id = sourceIcon.getId();
        Image image = sourceIcon.getImage();

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        return switch (id) {
            case "batteryIcon" -> new Battery(imageView, 5.0); // 5V default
            case "bulbIcon" -> new Lightbulb(imageView);
            case "switchIcon" -> {
                Switch newSwitch = new Switch(imageView);

                final long[] pressStart = new long[1];

                imageView.setOnMousePressed(e -> {
                    pressStart[0] = System.currentTimeMillis();
                    e.consume();
                });

                imageView.setOnMouseReleased(e -> {
                    long duration = System.currentTimeMillis() - pressStart[0];

                    if (e.getClickCount() == 2) {
                        newSwitch.disconnect();
                        System.out.println("🔌 Switch disconnected (double-click)");
                    } else if (duration > 500) { // press-and-hold to toggle
                        newSwitch.toggle();
                        System.out.println("🔁 Switch toggled via hold (" + (newSwitch.isClosed() ? "CLOSED" : "OPEN") + ")");

                        Set<Component> connected = getAllConnectedComponents(newSwitch);

                        for (Component c : connected) c.reset();       // ✅ Important
                        for (Component c : connected) c.simulate();    // voltage simulation
                        for (Component c : connected) c.propagatePower(new HashSet<>());
                    }

                    e.consume();
                });

                imageView.setPickOnBounds(true);
                imageView.setMouseTransparent(false);

                yield newSwitch;
            }




            default -> null;
        };
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private Point2D calculatePortPosition(Component component, Port port) {
        ImageView view = component.getView();
        double imageX = view.getLayoutX();
        double imageY = view.getLayoutY();
        double width = view.getBoundsInParent().getWidth();
        double height = view.getBoundsInParent().getHeight();

        double portX = imageX + port.getXOffset() * width;
        double portY = imageY + port.getYOffset() * height;

        return new Point2D(portX, portY);
    }

    /**
     * Searches the playground for a nearby Port within a given radius.
     * Used for snapping wire endpoints to existing component ports.
     *
     * @param position    The current position of the dragging port.
     * @param snapRadius  The distance threshold for snapping.
     * @return The nearby Port if found within the radius; otherwise null.
     */
    private Port findNearbyPort(Point2D position, double snapRadius) {
        for (Node node : playgroundPane.getChildren()) {
            if (node instanceof Circle circle && circle.getUserData() instanceof Port candidate) {
                double dx = circle.getLayoutX() - position.getX();
                double dy = circle.getLayoutY() - position.getY();
                double distance = Math.hypot(dx, dy);

                if (distance <= snapRadius) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private void enableLineDrag(Line line) {
        final Delta dragStart = new Delta();

        // Start point drag
        line.setOnMousePressed(e -> {
            dragStart.x = e.getX();
            dragStart.y = e.getY();
        });

        line.setOnMouseDragged(e -> {
            double newStartX = clamp(e.getSceneX(), 0, playgroundPane.getWidth());
            double newStartY = clamp(e.getSceneY(), 0, playgroundPane.getHeight());

            line.setStartX(newStartX);
            line.setStartY(newStartY);
        });

        // You can duplicate this to allow dragging the end point as well
        // Example:
        line.setOnMouseClicked(e -> {
            line.setOnMouseDragged(ev -> {
                double newEndX = clamp(ev.getSceneX(), 0, playgroundPane.getWidth());
                double newEndY = clamp(ev.getSceneY(), 0, playgroundPane.getHeight());

                line.setEndX(newEndX);
                line.setEndY(newEndY);
            });
        });
    }
    @FXML
    public void wireClick(MouseEvent mouseEvent) {
        double x = 200;
        double y = 200;

        Line line = new Line(x, y, x + 120, y);
        line.setStrokeWidth(4);
        line.setStroke(Color.BLACK);
        line.setCursor(Cursor.HAND);

        Wire wire = new Wire(line);
        line.setUserData(wire);

        // === Create draggable port circles ===
        Circle port1 = new Circle(6, Color.RED);
        Circle port2 = new Circle(6, Color.RED);
        port1.setCursor(Cursor.HAND);
        port2.setCursor(Cursor.HAND);

        port1.setUserData(wire.getPorts().get(0));
        port2.setUserData(wire.getPorts().get(1));

        port1.setLayoutX(line.getStartX());
        port1.setLayoutY(line.getStartY());
        port2.setLayoutX(line.getEndX());
        port2.setLayoutY(line.getEndY());

        wire.getPorts().get(0).setCircle(port1);
        wire.getPorts().get(1).setCircle(port2);

        // Add visuals to the scene
        playgroundPane.getChildren().addAll(line, port1, port2);

        enableWireMove(line, port1, port2);
        enablePortDrag(wire, port1, true);
        enablePortDrag(wire, port2, false);
    }
    private void enableWireMove(Line line, Circle port1, Circle port2) {
        final Delta dragDelta = new Delta();

        line.setOnMousePressed(e -> {
            dragDelta.x = e.getSceneX();
            dragDelta.y = e.getSceneY();
        });

        line.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - dragDelta.x;
            double dy = e.getSceneY() - dragDelta.y;

            line.setStartX(line.getStartX() + dx);
            line.setStartY(line.getStartY() + dy);
            line.setEndX(line.getEndX() + dx);
            line.setEndY(line.getEndY() + dy);

            port1.setLayoutX(port1.getLayoutX() + dx);
            port1.setLayoutY(port1.getLayoutY() + dy);
            port2.setLayoutX(port2.getLayoutX() + dx);
            port2.setLayoutY(port2.getLayoutY() + dy);

            dragDelta.x = e.getSceneX();
            dragDelta.y = e.getSceneY();
        });
    }

    /**
     * Enables a port (circle) to be dragged and snapped to nearby component ports.
     * Updates the wire's endpoint as the port is moved.
     *
     * @param wire     The wire this port belongs to.
     * @param circle   The visual Circle representing the port.
     * @param isStart  True if this is the start point of the wire; false for the end.
     */
    private void enablePortDrag(Wire wire, Circle circle, boolean isStart) {
        final Delta dragDelta = new Delta();

        circle.setOnMousePressed(e -> {
            dragDelta.x = e.getX();
            dragDelta.y = e.getY();
            e.consume();
        });

        circle.setOnMouseDragged(e -> {
            double newX = e.getSceneX() - dragDelta.x;
            double newY = e.getSceneY() - dragDelta.y;
            Point2D local = playgroundPane.sceneToLocal(newX, newY);

            // Attempt to snap to a nearby port
            Port snapTarget = findNearbyPort(local, 12); // 12px snap radius
            if (snapTarget != null) {
                local = new Point2D(
                        snapTarget.getCircle().getLayoutX(),
                        snapTarget.getCircle().getLayoutY()
                );

                // Physically snap the dragged port to the nearby one
                if (isStart) {
                    wire.getPorts().get(0).connectTo(snapTarget);
                    snapTarget.connectTo(wire.getPorts().get(0));
                } else {
                    wire.getPorts().get(1).connectTo(snapTarget);
                    snapTarget.connectTo(wire.getPorts().get(1));
                }

            }

            // Move the circle
            circle.setLayoutX(local.getX());
            circle.setLayoutY(local.getY());

            // Update wire line
            if (isStart) {
                wire.getLine().setStartX(local.getX());
                wire.getLine().setStartY(local.getY());
            } else {
                wire.getLine().setEndX(local.getX());
                wire.getLine().setEndY(local.getY());
            }

            e.consume();
        });
    }
    private Set<Component> getAllConnectedComponents(Component start) {
        Set<Component> visited = new HashSet<>();
        Queue<Component> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Component current = queue.poll();
            if (visited.add(current)) {
                for (Port port : current.getPorts()) {
                    if (port.getConnectedTo() != null) {
                        Component neighbor = port.getConnectedTo().getParentComponent();
                        if (!visited.contains(neighbor)) {
                            queue.add(neighbor);
                        }

                    }
                }
            }
        }
        return visited;
    }

    @FXML
    private void onRunCircuit() {
        System.out.println("\n\n✅ Run Circuit button clicked");

        Set<Component> components = new HashSet<>();

        // 🔁 First: collect all components and reset their state
        for (Node node : playgroundPane.getChildren()) {
            if (node.getUserData() instanceof Component c) {
                c.reset();  // ✅ Clear voltages and propagation flags
                components.add(c);
                System.out.println("Reset component: " + c.getClass().getSimpleName());
            }
        }

        // 🔋 Find the battery
        Battery battery = null;
        for (Component c : components) {
            if (c instanceof Battery b) {
                battery = b;
                break;
            }
        }

        if (battery == null) {
            System.out.println("❌ No battery found. Cannot simulate.");
            return;
        }

        // Find all switches in the circuit
        List<Switch> switches = new ArrayList<>();
        for (Component c : components) {
            if (c instanceof Switch s) {
                switches.add(s);
                System.out.println("Found switch: closed=" + s.isClosed());
            }
        }

        // Evaluate if the circuit is interrupted by open switches
        boolean circuitBroken = false;
        for (Switch s : switches) {
            if (!s.isClosed()) {
                System.out.println("⚠️ Circuit contains OPEN switch!");
                circuitBroken = true;
                break;
            }
        }

        // 🔍 Only keep components that are connected
        List<Component> filtered = new ArrayList<>();
        for (Component c : components) {
            // Always include batteries and switches for simulation
            if (c instanceof Battery || c instanceof Switch) {
                filtered.add(c);
                continue;
            }

            // Check if component is connected to the circuit
            if (hasAnyConnectedPorts(c)) {
                filtered.add(c);
            } else {
                System.out.println("⛔ Skipping " + c.getClass().getSimpleName() + " – not connected");
            }
        }

        // First simulate batteries to set voltage
        for (Component c : filtered) {
            if (c instanceof Battery) {
                c.simulate();
                System.out.println("Battery simulated");
            }
        }

        // If circuit is broken by an open switch, simulate switches but skip other components
        if (circuitBroken) {
            System.out.println("⚠️ Circuit is BROKEN - simulating switches only to enforce open state");
            for (Component c : filtered) {
                if (c instanceof Switch) {
                    c.simulate();
                }
            }
        } else {
            System.out.println("✅ Circuit is CLOSED - simulating all components");

            // Simulate in correct order: wires -> switches -> other components
            for (Component c : filtered) {
                if (c instanceof Wire) {
                    c.simulate();
                }
            }

            for (Component c : filtered) {
                if (c instanceof Switch) {
                    c.simulate();
                }
            }

            for (Component c : filtered) {
                if (!(c instanceof Battery || c instanceof Wire || c instanceof Switch)) {
                    c.simulate();
                }
            }

            // Second pass to ensure proper propagation
            System.out.println("🔄 Second simulation pass");
            for (Component c : filtered) {
                c.simulate();
            }
        }

        // Final propagation pass to update visual states
        System.out.println("🔄 Final power propagation");
        Set<Component> visited = new HashSet<>();
        for (Component c : filtered) {
            if (c instanceof Battery) {
                c.propagatePower(visited);
            }
        }
    }



    private boolean isInClosedLoop(Component component, Battery battery) {
        Set<Component> visited = new HashSet<>();
        return dfsBetweenPorts(component, battery.getPorts().get(1), visited); // battery negative port
    }


    private static boolean arePortsInSameLoop(Port a, Port b) {
        Set<Component> visited = new HashSet<>();
        return dfsBetweenPorts(a.getParentComponent(), b, visited);
    }

    private static boolean dfsBetweenPorts(Component current, Port targetPort, Set<Component> visited) {
        if (!visited.add(current)) return false;

        for (Port port : current.getPorts()) {
            Port connected = port.getConnectedTo();
            if (connected != null) {
                if (connected == targetPort) return true;
                Component next = connected.getParentComponent();
                if (dfsBetweenPorts(next, targetPort, visited)) return true;
            }
        }

        return false;
    }


    
        private boolean hasAnyConnectedPorts(Component c) {
            for (Port p : c.getPorts()) {
                if (p.getConnectedTo() != null) return true;
            }
            return false;
        }

    private void saveLayoutToFile(File file) throws IOException {
        List<SerializableComponent> componentData = new ArrayList<>();
        List<SerializableWire> wireData = new ArrayList<>();
        Map<Component, String> idMap = new HashMap<>();
        int idCounter = 0;

        for (Node node : playgroundPane.getChildren()) {
            if (node.getUserData() instanceof Component c) {
                String id = "comp-" + (idCounter++);
                idMap.put(c, id);

                SerializableComponent data = new SerializableComponent();
                data.id = id;
                data.type = c.getClass().getSimpleName();

                ImageView view = c.getView();
                if (view == null) {
                    System.out.println("⚠️ Skipping component with null view: " + c.getClass().getSimpleName());
                    continue;
                }

                data.x = view.getLayoutX();
                data.y = view.getLayoutY();
                data.voltage = c.getVoltage();

                if (c instanceof Switch sw) {
                    data.isClosed = sw.isClosed();
                }

                componentData.add(data);
            }
        }


        for (Node node : playgroundPane.getChildren()) {
            if (node instanceof Line line && node.getUserData() instanceof Wire wire) {
                Port a = wire.getPorts().get(0);
                Port b = wire.getPorts().get(1);

                if (a.getConnectedTo() == null || b.getConnectedTo() == null) continue;

                Component compA = a.getConnectedTo().getParentComponent();
                Component compB = b.getConnectedTo().getParentComponent();

                SerializableWire wireEntry = new SerializableWire();
                wireEntry.startComponentId = idMap.get(compA);
                wireEntry.endComponentId = idMap.get(compB);
                wireEntry.startPortIndex = compA.getPorts().indexOf(a.getConnectedTo());
                wireEntry.endPortIndex = compB.getPorts().indexOf(b.getConnectedTo());
                wireEntry.startX = line.getStartX();
                wireEntry.startY = line.getStartY();
                wireEntry.endX = line.getEndX();
                wireEntry.endY = line.getEndY();

                wireData.add(wireEntry);
            }
        }

        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("components", componentData);
        wrapper.put("wires", wireData);

        try (FileWriter writer = new FileWriter(file)) {
            new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(wrapper, writer);
        }
    }

    private void loadLayoutFromFile(File file) throws IOException {
        playgroundPane.getChildren().clear(); // wipe canvas

        Gson gson = new Gson();
        Reader reader = new FileReader(file);

        Map<?, ?> layout = gson.fromJson(reader, Map.class);
        reader.close();

        List<?> compList = (List<?>) layout.get("components");
        List<?> wireList = (List<?>) layout.get("wires");

        Map<String, Component> idToComponent = new HashMap<>();

        for (Object o : compList) {
            Map<?, ?> data = (Map<?, ?>) o;
            String type = (String) data.get("type");
            String id = (String) data.get("id");
            double x = ((Number) data.get("x")).doubleValue();
            double y = ((Number) data.get("y")).doubleValue();

            Component c = switch (type) {
                case "Battery" -> new Battery(new ImageView(batteryIcon.getImage()), 5.0);
                case "Lightbulb" -> new Lightbulb(new ImageView(bulbIcon.getImage()));
                case "Switch" -> {
                    Switch sw = new Switch(new ImageView(switchIcon.getImage()));
                    sw.toggle(); // temporary: toggle to closed if needed
                    if (!(Boolean) data.get("isClosed")) sw.toggle(); // restore original state
                    yield sw;
                }
                default -> null;
            };

            if (c == null) continue;

            ImageView view = c.getView();
            view.setLayoutX(x);
            view.setLayoutY(y);
            view.setFitWidth(60);
            view.setFitHeight(60);
            view.setPreserveRatio(true);
            view.setUserData(c);

            playgroundPane.getChildren().add(view);
            enableDrag(view);

            for (Port port : c.getPorts()) {
                Circle circle = new Circle(6, Color.RED);
                double px = view.getLayoutX() + port.getXOffset() * view.getFitWidth();
                double py = view.getLayoutY() + port.getYOffset() * view.getFitHeight();
                circle.setLayoutX(px);
                circle.setLayoutY(py);
                circle.setUserData(port);
                port.setCircle(circle);
                playgroundPane.getChildren().add(circle);
            }

            idToComponent.put(id, c);
        }

        for (Object o : wireList) {
            Map<?, ?> data = (Map<?, ?>) o;
            String idA = (String) data.get("startComponentId");
            String idB = (String) data.get("endComponentId");

            Component compA = idToComponent.get(idA);
            Component compB = idToComponent.get(idB);

            if (compA == null || compB == null) {
                System.err.println("⚠️ Skipping wire: missing components " + idA + ", " + idB);
                continue;
            }

            int portA = ((Number) data.get("startPortIndex")).intValue();
            int portB = ((Number) data.get("endPortIndex")).intValue();

            if (compA.getPorts().size() <= portA || compB.getPorts().size() <= portB) {
                System.err.println("⚠️ Skipping wire: invalid port index");
                continue;
            }

            double sx = ((Number) data.get("startX")).doubleValue();
            double sy = ((Number) data.get("startY")).doubleValue();
            double ex = ((Number) data.get("endX")).doubleValue();
            double ey = ((Number) data.get("endY")).doubleValue();

            Line line = new Line(sx, sy, ex, ey);
            line.setStrokeWidth(4);
            line.setStroke(Color.BLACK);
            line.setCursor(Cursor.HAND);

            Wire wire = new Wire(line);
            line.setUserData(wire);

            Circle circle1 = new Circle(6, Color.RED);
            Circle circle2 = new Circle(6, Color.RED);
            circle1.setLayoutX(sx);
            circle1.setLayoutY(sy);
            circle2.setLayoutX(ex);
            circle2.setLayoutY(ey);

            Port startPort = wire.getPorts().get(0);
            Port endPort = wire.getPorts().get(1);

            startPort.setCircle(circle1);
            endPort.setCircle(circle2);
            circle1.setUserData(startPort);
            circle2.setUserData(endPort);

            // reconnect
            Port targetA = compA.getPorts().get(portA);
            Port targetB = compB.getPorts().get(portB);
            startPort.connectTo(targetA);
            endPort.connectTo(targetB);

            playgroundPane.getChildren().addAll(line, circle1, circle2);

            enableWireMove(line, circle1, circle2);
            enablePortDrag(wire, circle1, true);
            enablePortDrag(wire, circle2, false);
        }

    }


}