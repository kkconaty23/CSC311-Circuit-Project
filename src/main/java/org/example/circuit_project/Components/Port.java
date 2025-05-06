package org.example.circuit_project.Components;

import javafx.scene.shape.Circle;

public class Port {
    private final Object parent; // Can be Component or Wire
    private final double xOffset, yOffset;
    private Circle circle;
    private Port connectedTo;
    private double voltage = 0;

    public Port(Object parent, double xOffset, double yOffset) {
        this.parent = parent;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
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

            // Prevent infinite recursion or duplicate linking
            if (other.connectedTo != this) {
                other.connectTo(this);
            }

            System.out.println("🔗 " + this.parent.getClass().getSimpleName() + " port connected to " + other.parent.getClass().getSimpleName());
        } else {
            System.out.println("🔌 Port disconnected");
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
        System.out.println("🔋 Port " + parent.getClass().getSimpleName() + " set to " + voltage + "V");
    }



    public Component getParentComponent() {
        return (Component) parent;
    }

}