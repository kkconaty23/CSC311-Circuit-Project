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
        if (this.connectedTo == other) return; // prevent duplicate connections
        this.connectedTo = other;
        other.connectedTo = this;
        System.out.println("🔗 " + this.parent.getClass().getSimpleName() + " port connected to " + other.parent.getClass().getSimpleName());
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