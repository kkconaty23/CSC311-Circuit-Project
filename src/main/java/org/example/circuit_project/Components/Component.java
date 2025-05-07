// === Component.java ===
package org.example.circuit_project.Components;

import javafx.scene.image.ImageView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract base class for all circuit components in the simulator.
 *
 * Components can include batteries, lightbulbs, switches, wires, etc.
 * Each component can have an associated {@link ImageView} for its visual representation,
 * voltage values for simulation, and one or more {@link Port} connections to other components.
 *
 * Subclasses must implement simulation logic, power checks, and port management.
 */
public abstract class Component {

    protected ImageView view;
    protected double voltage = 0;

    /**
     * Constructs a new Component with an optional image view.
     *
     * @param view The ImageView representing the component, may be null.
     */
    public Component(ImageView view) {
        this.view = view;
        if (view != null) {
            view.setUserData(this);
        }
    }

    /**
     * Gets the image view representing this component.
     *
     * @return the image view or null if not applicable
     */
    public ImageView getView() {
        return view;
    }

    /**
     * Gets the current voltage level for this component.
     *
     * @return the voltage in volts
     */
    public double getVoltage() {
        return voltage;
    }

    /**
     * Sets the voltage level for this component.
     *
     * @param voltage the voltage in volts
     */
    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    /**
     * Runs the component’s internal simulation logic.
     * This may include voltage propagation and interaction with connected components.
     */
    public abstract void simulate();

    /**
     * Returns the list of ports that belong to this component.
     *
     * @return a list of {@link Port} objects
     */
    public abstract List<Port> getPorts();

    /**
     * Indicates whether this component is currently powered (e.g., receiving voltage).
     *
     * @return true if powered, false otherwise
     */
    public abstract boolean isPowered();

    /**
     * Begins recursive propagation of power signals from this component.
     * Uses a default empty visited set to track visited components.
     */
    public void propagatePower() {
        propagatePower(new HashSet<>());
    }

    /**
     * Propagates power through the circuit starting from this component.
     * Avoids cycles by using a visited set.
     *
     * @param visited the set of components already visited
     */
    public abstract void propagatePower(Set<Component> visited);

    /**
     * Compares by identity rather than state.
     * Components are only equal if they are the same instance.
     *
     * @param o the object to compare
     * @return true if the objects are the same instance
     */
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    /**
     * Hash code based on object identity.
     *
     * @return identity-based hash code
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    /**
     * Resets all voltage-related state for this component and its ports.
     */
    public void reset() {
        for (Port port : getPorts()) {
            port.setVoltage(0);
        }
        setVoltage(0); // For components that store internal voltage
    }

    /**
     * Disconnects this component from all its connected ports.
     * Clears connections and voltage propagation.
     */
    public void disconnect() {
        boolean hadConnections = false;
        Port triggerPort = null;

        for (Port port : getPorts()) {
            Port connected = port.getConnectedTo();
            if (connected != null) {
                hadConnections = true;

                // Keep a reference to one connected port for auto-simulation
                triggerPort = connected;

                // Disconnect the ports
                connected.connectTo(null);
                port.connectTo(null);
            }
        }

        // Reset the component state
        reset();
        updateVisualState();

        // If auto-simulation is enabled, trigger it from the other component
        if (triggerPort != null) {
            triggerPort.triggerAutomaticSimulation();
        }
    }
    /**
     * Updates the visual state of the component.
     * Default implementation does nothing. Override in subclasses.
     */
    public void updateVisualState() {
        // Default: do nothing
    }



}