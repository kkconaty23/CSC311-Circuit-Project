// === Component.java ===
package org.example.circuit_project.Components;

import javafx.scene.image.ImageView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Component {
    protected ImageView view;
    protected double voltage = 0;

    public Component(ImageView view) {
        this.view = view;
        if (view != null) {
            view.setUserData(this);
        }
    }

    public ImageView getView() {
        return view;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public abstract void simulate();
    public abstract List<Port> getPorts();
    public abstract boolean isPowered();
    public void propagatePower() {
        propagatePower(new HashSet<>());
    }

    public abstract void propagatePower(Set<Component> visited);

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
    public void reset() {
        for (Port port : getPorts()) {
            port.setVoltage(0);
        }
        setVoltage(0); // For components that store internal voltage
    }
    public void disconnect() {
        for (Port port : getPorts()) {
            Port connected = port.getConnectedTo();
            if (connected != null) {
                connected.connectTo(null);
                port.connectTo(null);
            }
        }
    }
    // Optional override in subclasses
    public void updateVisualState() {
        // Default: do nothing
    }



}