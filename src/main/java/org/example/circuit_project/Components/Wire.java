package org.example.circuit_project.Components;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import org.example.circuit_project.Sandbox.SandboxController;

import java.util.*;

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
        // Disconnect all ports
        for (Port port : getPorts()) {
            Port other = port.getConnectedTo();
            if (other != null) {
                // Find controller before disconnecting
                SandboxController controller = null;
                Component otherComponent = other.getParentComponent();
                if (otherComponent != null && otherComponent.getView() != null &&
                        otherComponent.getView().getScene() != null &&
                        otherComponent.getView().getScene().getUserData() instanceof SandboxController) {

                    controller = (SandboxController) otherComponent.getView().getScene().getUserData();
                }

                // Disconnect
                other.connectTo(null);
                port.connectTo(null);

                // If auto-simulate is enabled, force a full reset and re-simulation
                if (controller != null && controller.isAutoSimulateEnabled()) {
                    controller.resetAllComponents();
                    controller.runCircuitSimulation();
                    break; // Only need to do this once
                }
            }
        }

        // Reset this wire
        reset();
    }

    private boolean shouldAutoSimulate() {
        for (Component component : getAllConnected(this)) {
            if (component.getView() != null &&
                    component.getView().getScene() != null &&
                    component.getView().getScene().getUserData() instanceof SandboxController) {
                return ((SandboxController) component.getView().getScene().getUserData())
                        .isAutoSimulateEnabled();
            }
        }
        return false;
    }

    private Battery findBatteryIn(Set<Component> components) {
        for (Component c : components) {
            if (c instanceof Battery) return (Battery)c;
        }

        // Also search for batteries connected to these components
        for (Component c : components) {
            for (Port p : c.getPorts()) {
                if (p.isConnected()) {
                    Component other = p.getConnectedTo().getParentComponent();
                    if (other instanceof Battery) return (Battery)other;
                }
            }
        }
        return null;
    }

    private Set<Component> getAllConnected(Component start) {
        Set<Component> visited = new HashSet<>();
        Queue<Component> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Component current = queue.poll();
            if (visited.add(current)) {
                for (Port port : current.getPorts()) {
                    if (port.isConnected()) {
                        Component neighbor = port.getConnectedTo().getParentComponent();
                        if (!visited.contains(neighbor)) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        return visited;
    }

    // Helper to check if auto-simulation is enabled
    private boolean isAutoSimulationEnabled() {
        // Try to find a component that can access the scene
        for (Port port : getPorts()) {
            Component parentComponent = port.getParentComponent();
            if (parentComponent != null && parentComponent.getView() != null) {
                ImageView view = parentComponent.getView();
                if (view.getScene() != null &&
                        view.getScene().getUserData() instanceof SandboxController) {

                    SandboxController controller =
                            (SandboxController) view.getScene().getUserData();
                    return controller.isAutoSimulateEnabled();
                }
            }
        }
        return false;
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

            System.out.println("Wire simulate: A=" + voltageA + ", B=" + voltageB + " => Setting both to " + chosenVoltage);
        } else {
            end1.setVoltage(0);
            end2.setVoltage(0);
            System.out.println("Wire simulate: Not fully connected. Zeroing ports.");
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