package org.example.circuit_project.Components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.circuit_project.SandboxController;

import java.util.HashSet;
import java.util.Set;

public class Port {
    private final Object parent; // Can be Component or Wire
    private final double xOffset, yOffset;
    private Circle circle;
    private Port connectedTo;
    private double voltage = 0;
    private boolean isWireEnd = false;

    public Port(Object parent, double xOffset, double yOffset) {
        this.parent = parent;
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        // Create the visual circle right away
        this.circle = new Circle(6); // Default size
        this.circle.setStroke(Color.RED);
        this.circle.setFill(Color.TRANSPARENT);
        this.circle.setStrokeWidth(2);
    }

    public void markAsWirePort() {
        this.isWireEnd = true;

        if (this.circle != null) {
            this.circle.setRadius(4);  // wire tips are smaller
        }

        updateVisualState();  // apply styling now
    }

    public void updateVisualState() {
        if (circle == null) return;

        boolean isConnected = (connectedTo != null);

        if (isWireEnd) {
            // WIRE TIP: solid small dot (no border)
            circle.setRadius(4); // just to be sure
            circle.setFill(isConnected ? Color.LIMEGREEN : Color.RED);
            circle.setStroke(null);
        } else {
            // COMPONENT PORT: larger hollow
            circle.setRadius(6);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(isConnected ? Color.LIMEGREEN : Color.RED);
            circle.setStrokeWidth(2);
        }
    }

    public Object getParent() {
        return parent;
    }

    public double getXOffset() {
        return xOffset;
    }

    public double getYOffset() {
        return yOffset;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public Port getConnectedTo() {
        return connectedTo;
    }

    // Replace the existing connectTo method in Port.java with this:
    public void connectTo(Port other) {
        // Store old connection for change detection
        Port oldConnection = this.connectedTo;

        // Remember important information before disconnecting
        boolean isDisconnection = (other == null && oldConnection != null);
        Component oldComponent = isDisconnection && oldConnection != null ?
                oldConnection.getParentComponent() : null;

        // Disconnect existing connection first
        if (this.connectedTo != null) {
            Port old = this.connectedTo;
            this.connectedTo = null;
            if (old.connectedTo == this) {
                old.connectedTo = null;
            }
        }

        // Connect to new port if not null
        if (other != null) {
            this.connectedTo = other;

            if (other.connectedTo != this) {
                other.connectTo(this);
            }

            System.out.println(this.parent.getClass().getSimpleName() + " port connected to " + other.parent.getClass().getSimpleName());
        } else {
            System.out.println("Port disconnected");
        }

        updateVisualState();
        if (other != null) other.updateVisualState();

        // Handle disconnect specifically for lightbulbs
        if (isDisconnection) {
            // Find all lightbulbs and force them to reset
            if (parent instanceof Component parentComponent) {
                // If this is a lightbulb being disconnected, reset it immediately
                if (parentComponent instanceof Lightbulb) {
                    parentComponent.reset();
                    parentComponent.updateVisualState();
                }

                // If we were disconnected from a lightbulb, reset it too
                if (oldComponent instanceof Lightbulb) {
                    oldComponent.reset();
                    oldComponent.updateVisualState();
                }

                // Find controller and run simulation if auto is enabled
                if (isAutoSimulationEnabled()) {
                    SandboxController controller = findController();
                    if (controller != null) {
                        controller.runCircuitSimulation();
                    }
                }
            }
        } else {
            // Only trigger simulation on the "last" connectTo call to avoid recursion
            // and only if the connection actually changed
            if (oldConnection != other && (other == null || other.getConnectedTo() == this)) {
                triggerAutomaticSimulation();
            }
        }
    }

    // Helper method to find controller
    private SandboxController findController() {
        if (parent instanceof Component parentComponent) {
            if (parentComponent.getView() != null &&
                    parentComponent.getView().getScene() != null &&
                    parentComponent.getView().getScene().getUserData() instanceof SandboxController) {

                return (SandboxController) parentComponent.getView().getScene().getUserData();
            }

            // Try to find through connected components
            for (Port port : parentComponent.getPorts()) {
                if (port.isConnected()) {
                    Component connected = port.getConnectedTo().getParentComponent();
                    if (connected.getView() != null &&
                            connected.getView().getScene() != null &&
                            connected.getView().getScene().getUserData() instanceof SandboxController) {

                        return (SandboxController) connected.getView().getScene().getUserData();
                    }
                }
            }
        }
        return null;
    }

    // Add these new methods to Port.java
    public void triggerAutomaticSimulation() {
        // Check if auto-simulation should run
        if (!isAutoSimulationEnabled()) {
            return;
        }

        // Find all connected components
        Set<Component> components = new HashSet<>();
        findConnectedComponents(getParentComponent(), components);

        System.out.println("Auto-simulating " + components.size() + " components");

        // Reset all components first
        for (Component c : components) {
            c.reset();
        }

        // First simulate batteries
        for (Component c : components) {
            if (c instanceof Battery) {
                c.simulate();
            }
        }

        // Then wires
        for (Component c : components) {
            if (c instanceof Wire) {
                c.simulate();
            }
        }

        // Then switches
        for (Component c : components) {
            if (c instanceof Switch) {
                c.simulate();
            }
        }

        // Then all other components
        for (Component c : components) {
            if (!(c instanceof Battery || c instanceof Wire || c instanceof Switch)) {
                c.simulate();
            }
        }

        // Final propagation of power for visual updates
        Set<Component> visited = new HashSet<>();
        for (Component c : components) {
            if (c instanceof Battery) {
                c.propagatePower(visited);
            }
        }
    }

    private boolean isAutoSimulationEnabled() {
        // Try to access the controller's setting
        if (parent instanceof Component) {
            Component comp = (Component) parent;
            if (comp.getView() != null && comp.getView().getScene() != null) {
                Object controller = comp.getView().getScene().getUserData();
                if (controller instanceof SandboxController) {
                    return ((SandboxController) controller).isAutoSimulateEnabled();
                }
            }
        }

        // Default to false if we can't find controller (safer option)
        return false;
    }

    private void findConnectedComponents(Component start, Set<Component> visited) {
        if (!visited.add(start)) return;

        for (Port port : start.getPorts()) {
            if (port.isConnected()) {
                Component neighbor = port.getConnectedTo().getParentComponent();
                findConnectedComponents(neighbor, visited);
            }
        }
    }

    public boolean isConnected() {
        return connectedTo != null;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
        System.out.println("Port " + parent.getClass().getSimpleName() + " set to " + voltage + "V");
    }

    public Component getParentComponent() {
        return (Component) parent;
    }
}
