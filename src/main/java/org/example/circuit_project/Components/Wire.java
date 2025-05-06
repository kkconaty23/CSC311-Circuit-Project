package org.example.circuit_project.Components;

import javafx.scene.shape.Line;
import java.util.List;
import java.util.Set;

public class Wire extends Component {
    private final Line line;
    private final Port end1;
    private final Port end2;

    public Wire(Line line) {
        super(null); // Wires don't use an ImageView
        this.line = line;
        this.end1 = new Port(this, 0, 0);
        this.end2 = new Port(this, 1, 0);
    }

    public Line getLine() {
        return line;
    }

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

            System.out.println("🔌 Wire simulate: A=" + voltageA + ", B=" + voltageB + " => Setting both to " + chosenVoltage);
        } else {
            end1.setVoltage(0);
            end2.setVoltage(0);
            System.out.println("❌ Wire simulate: Not fully connected. Zeroing ports.");
        }
    }


    public void reset() {
        for (Port port : getPorts()) {
            port.setVoltage(0);
        }
        setVoltage(0); // For components that store internal voltage
    }


    @Override
    public List<Port> getPorts() {
        return List.of(end1, end2);
    }

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

    @Override
    public boolean isPowered() {
        return end1.getVoltage() > 0 || end2.getVoltage() > 0;
    }
}
