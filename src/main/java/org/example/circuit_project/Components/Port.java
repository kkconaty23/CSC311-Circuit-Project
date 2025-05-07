package org.example.circuit_project.Components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Represents a connection point on a Component or Wire in the circuit.
 * Ports can be visually rendered as circles and can be connected to other ports to form a circuit.
 */
public class Port {
    private final Object parent; // Can be Component or Wire
    private final double xOffset, yOffset;
    private Circle circle;
    private Port connectedTo;
    private double voltage = 0;
    private boolean isWireEnd = false;

    /**
     * Creates a new Port with a parent and relative offset.
     *
     * @param parent  the parent object (Component or Wire)
     * @param xOffset the relative horizontal offset (0.0 to 1.0) for visual positioning
     * @param yOffset the relative vertical offset (0.0 to 1.0) for visual positioning
     */
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

    /**
     * Marks this port as a wire end.
     * Changes visual size and fill style to indicate wire-specific behavior.
     */
    public void markAsWirePort() {
        this.isWireEnd = true;

        if (this.circle != null) {
            this.circle.setRadius(4);  // wire tips are smaller
        }

        updateVisualState();  // apply styling now
    }

    /**
     * Updates the port’s visual styling based on connection status and type (wire/component).
     */
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

    /**
     * @return the parent object this port belongs to (Component or Wire)
     */
    public Object getParent() {
        return parent;
    }

    /**
     * @return the horizontal offset relative to the parent component’s image
     */
    public double getXOffset() {
        return xOffset;
    }

    /**
     * @return the vertical offset relative to the parent component’s image
     */
    public double getYOffset() {
        return yOffset;
    }

    /**
     * @return the visual circle representing this port
     */
    public Circle getCircle() {
        return circle;
    }

    /**
     * Sets a new circle to represent the port visually.
     *
     * @param circle the new JavaFX Circle node
     */
    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    /**
     * @return the port this port is connected to, or null if not connected
     */
    public Port getConnectedTo() {
        return connectedTo;
    }

    /**
     * Connects this port to another, automatically ensuring bidirectional links and visual updates.
     * Existing connections are safely removed.
     *
     * @param other the port to connect to
     */
    public void connectTo(Port other) {
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
    }

    /**
     * @return true if this port is connected to another port
     */
    public boolean isConnected() {
        return connectedTo != null;
    }

    /**
     * @return the current voltage at this port
     */
    public double getVoltage() {
        return voltage;
    }

    /**
     * Sets the voltage of this port and logs it.
     *
     * @param voltage the new voltage to apply
     */
    public void setVoltage(double voltage) {
        this.voltage = voltage;
        System.out.println("Port " + parent.getClass().getSimpleName() + " set to " + voltage + "V");
    }

    /**
     * @return the parent Component this port is part of (cast from Object)
     */
    public Component getParentComponent() {
        return (Component) parent;
    }
}
