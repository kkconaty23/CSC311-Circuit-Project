package org.example.circuit_project.Components;

import javafx.scene.shape.Line;
import java.util.List;
import java.util.Set;

/**
 * Represents a wire component that connects two ports in a circuit.
 * A wire transmits voltage between two ends if both are connected.
 */
public class Wire extends Component {
    private final Line line;
    private final Port end1;
    private final Port end2;

    /**
     * Constructs a Wire component with a visual Line object.
     *
     * @param line the JavaFX Line used to visually represent the wire
     */
    public Wire(Line line) {
        super(null); // Wires don't use an ImageView
        this.line = line;
        this.end1 = new Port(this, 0, 0);
        this.end2 = new Port(this, 1, 0);
    }

    /**
     * Returns the JavaFX Line associated with this wire.
     *
     * @return the Line object representing the wire visually
     */
    public Line getLine() {
        return line;
    }

    /**
     * Updates the Line's start and end positions based on connected port locations.
     */
    public void updateLinePosition() {
        if (end1.getCircle() != null) {
            line.setStartX(end1.getCircle().getLayoutX());
            line.setStartY(end1.getCircle().getLayoutY());
        }
        if (end2.getCircle() != null) {
            line.setEndX(end2.getCircle().getLayoutX());
            line.setEndY(end2.getCircle().getLayoutY());
        }
    }

    /**
     * Disconnects both ends of the wire from any connected ports.
     * Resets internal voltages and updates visuals.
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
     * Simulates voltage transmission through the wire based on connections.
     * Voltage is passed from the end that has it to the other end.
     */
    @Override
    public void simulate() {
        if (end1.isConnected() && end2.isConnected()) {
            double voltageA = end1.getConnectedTo().getVoltage();
            double voltageB = end2.getConnectedTo().getVoltage();

            boolean aHasVoltage = voltageA != 0;
            boolean bHasVoltage = voltageB != 0;

            double chosenVoltage = 0;
            if (aHasVoltage && !bHasVoltage) {
                chosenVoltage = voltageA;
            } else if (!aHasVoltage && bHasVoltage) {
                chosenVoltage = voltageB;
            } else if (aHasVoltage && bHasVoltage) {
                chosenVoltage = voltageA; // Arbitrary pick — both should match
            }

            end1.setVoltage(chosenVoltage);
            end2.setVoltage(chosenVoltage);

            System.out.println("Wire simulate: A=" + voltageA + ", B=" + voltageB + " => Setting both to " + chosenVoltage);
        } else {
            end1.setVoltage(0);
            end2.setVoltage(0);
            System.out.println("Wire simulate: Not fully connected. Zeroing ports.");
        }
    }

    /**
     * Resets the voltage on both ports and clears any internal voltage state.
     */
    public void reset() {
        for (Port port : getPorts()) {
            port.setVoltage(0);
        }
        setVoltage(0); // For components that store internal voltage
    }

    /**
     * Returns both ports (ends) of the wire.
     *
     * @return a list containing the two wire ends
     */
    @Override
    public List<Port> getPorts() {
        return List.of(end1, end2);
    }

    /**
     * Propagates power through both ends of the wire to connected components.
     *
     * @param visited a set of already visited components to avoid infinite loops
     */
    @Override
    public void propagatePower(Set<Component> visited) {
        if (!visited.add(this)) return;

        if (end1.isConnected()) {
            Object p = end1.getConnectedTo().getParent();
            if (p instanceof Component c) c.propagatePower(visited);
        }
        if (end2.isConnected()) {
            Object p = end2.getConnectedTo().getParent();
            if (p instanceof Component c) c.propagatePower(visited);
        }
    }

    /**
     * Determines if the wire is currently carrying any voltage.
     *
     * @return true if either end has a non-zero voltage
     */
    @Override
    public boolean isPowered() {
        return end1.getVoltage() > 0 || end2.getVoltage() > 0;
    }
}
