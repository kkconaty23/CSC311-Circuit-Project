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
        this.circle.setRadius(4);  // Smaller for wire tips
        updateVisualState();
    }

    public void updateVisualState() {
        if (isConnected()) {
            circle.setStroke(Color.DARKGREEN);
            circle.setFill(isWireEnd ? Color.LIMEGREEN : Color.TRANSPARENT);
        } else {
            circle.setStroke(Color.RED);
            circle.setFill(isWireEnd ? Color.RED : Color.TRANSPARENT);
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
