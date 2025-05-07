package org.example.circuit_project.Components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.Set;

/**
 * Represents a battery component in the circuit simulator.
 *
 * The battery has two ports: a positive terminal and a negative terminal.
 * When both ports are connected, it applies a fixed output voltage across them.
 *
 * The battery does not rely on incoming voltage; it acts as a power source and is always considered "powered".
 */
public class Battery extends Component {
    private final Port positive;
    private final Port negative;
    private final double outputVoltage;

    /**
     * Constructs a Battery with the given image view and output voltage.
     *
     * @param view the ImageView representing the battery's visual
     * @param voltage the fixed voltage the battery supplies when connected
     */
    public Battery(ImageView view, double voltage) {
        super(view);
        this.outputVoltage = voltage;
        this.positive = new Port(this, 0.1, 0.0);
        this.negative = new Port(this, 0.9, 0.0);

        if (view != null) {
            view.setImage(new Image(getClass().getResource("/org/example/circuit_project/images/battery.png").toExternalForm()));
        }
    }

    /**
     * Simulates the battery behavior.
     * If both terminals are connected, it sets the positive port to the output voltage
     * and the negative port to 0V (ground). Otherwise, resets to 0V.
     */
    @Override
    public void simulate() {
        if (positive.isConnected() && negative.isConnected()) {
            positive.setVoltage(outputVoltage);    // Full voltage out
            negative.setVoltage(0);                // Ground
            setVoltage(outputVoltage);
            System.out.println("Battery simulate: positive=" + outputVoltage + ", negative=0");
        } else {
            // Reset if not fully connected
            positive.setVoltage(0);
            negative.setVoltage(0);
            setVoltage(0);
            System.out.println("Battery simulate: one or both terminals not connected – no output");
        }
    }
    /**
     * Resets all internal and port voltages to 0V.
     */
    public void reset() {
        for (Port port : getPorts()) {
            port.setVoltage(0);
        }
        setVoltage(0);
    }

    /**
     * Disconnects the battery from any connected components.
     * This clears all port connections and resets internal voltage state.
     */
    @Override
    public void disconnect() {
        for (Port port : getPorts()) {
            Port other = port.getConnectedTo();
            if (other != null) {
                other.connectTo(null);
            }
            port.connectTo(null);
        }

        reset();
        updateVisualState();
    }

    /**
     * Returns the list of ports belonging to this battery.
     *
     * @return a list containing the positive and negative ports
     */
    @Override
    public List<Port> getPorts() {
        return List.of(positive, negative);
    }

    /**
     * Always returns true since the battery is inherently a power source.
     *
     * @return true
     */
    @Override
    public boolean isPowered() {
        return true;
    }

    /**
     * Propagates power to all components connected to the positive and negative ports.
     * Uses a visited set to prevent infinite recursion.
     *
     * @param visited the set of components already visited during propagation
     */
    @Override
    public void propagatePower(Set<Component> visited) {
        if (!visited.add(this)) return;
        if (positive.isConnected()) {
            Object p = positive.getConnectedTo().getParent();
            if (p instanceof Component c) c.propagatePower(visited);
        }
        if (negative.isConnected()) {
            Object p = negative.getConnectedTo().getParent();
            if (p instanceof Component c) c.propagatePower(visited);
        }
    }
}
