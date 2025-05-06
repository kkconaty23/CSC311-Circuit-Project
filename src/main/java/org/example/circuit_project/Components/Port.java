package org.example.circuit_project.Components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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

            System.out.println("🔗 " + this.parent.getClass().getSimpleName() + " port connected to " + other.parent.getClass().getSimpleName());
        } else {
            System.out.println("🔌 Port disconnected");
        }

        updateVisualState();
        if (other != null) other.updateVisualState();
    }

    public boolean isConnected() {
        return connectedTo != null;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
        System.out.println("🔋 Port " + parent.getClass().getSimpleName() + " set to " + voltage + "V");
    }

    public Component getParentComponent() {
        return (Component) parent;
    }
}
