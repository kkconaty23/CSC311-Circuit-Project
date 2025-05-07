package org.example.circuit_project.Components;

import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.*;

public class Switch extends Component {
    private final Port input;
    private final Port output;
    private boolean isClosed;

    public Switch(ImageView view) {
        super(view);
        this.input = new Port(this, 0.01, 0.55);
        this.output = new Port(this, 0.99, 0.55);
        this.isClosed = false;
    }


    public void toggle() {
        // Change state
        isClosed = !isClosed;
        System.out.println("Switch toggled: " + (isClosed ? "CLOSED" : "OPEN"));
        updateVisualState();

        // Find all components in the circuit
        Battery battery = null;
        Lightbulb bulb = null;
        Set<Component> allComponents = getAllConnectedComponents();

        // Find battery and bulb
        for (Component c : allComponents) {
            if (c instanceof Battery) {
                battery = (Battery) c;
            } else if (c instanceof Lightbulb) {
                bulb = (Lightbulb) c;
            }
        }

        // Reset the entire circuit
        for (Component c : allComponents) {
            c.reset();
        }

        // Only proceed if we have a battery
        if (battery != null) {
            // Battery always operates first
            battery.simulate();

            // Pass current through wires and switch
            for (Component c : allComponents) {
                if (c instanceof Wire) {
                    c.simulate();
                }
            }

            // The switch itself (already updated state)
            this.simulate();

            // More wire updates to propagate voltage past the switch
            for (Component c : allComponents) {
                if (c instanceof Wire) {
                    c.simulate();
                }
            }

            // Finally run the bulb
            if (bulb != null) {
                bulb.simulate();
            }

            // Run everything one more time for consistency
            for (Component c : allComponents) {
                c.simulate();
            }

            // Visual updates through power propagation
            Set<Component> visited = new HashSet<>();
            battery.propagatePower(visited);
        } else {
            // Fallback to standard port-based auto-simulation
            if (input.isConnected() || output.isConnected()) {
                input.triggerAutomaticSimulation();
            }
        }
    }

    // Helper method to find the battery in connected circuit
    private Battery findBatteryInCircuit() {
        Set<Component> visited = new HashSet<>();
        Queue<Component> queue = new LinkedList<>();
        queue.add(this);

        while (!queue.isEmpty()) {
            Component current = queue.poll();
            if (visited.add(current)) {
                if (current instanceof Battery) {
                    return (Battery) current;
                }

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
        return null;
    }

    // Helper method to get all connected components
    private Set<Component> getAllConnectedComponents() {
        Set<Component> visited = new HashSet<>();
        Queue<Component> queue = new LinkedList<>();
        queue.add(this);

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

    public boolean isClosed() {
        return isClosed;
    }

    public void reset() {
        input.setVoltage(0);
        output.setVoltage(0);
        setVoltage(0);
    }


    @Override
    public void disconnect() {
        // Store a reference to a connected port before disconnecting
        Port triggerPort = null;
        for (Port port : getPorts()) {
            if (port.isConnected()) {
                triggerPort = port.getConnectedTo();
                break;
            }
        }

        // Disconnect all ports
        for (Port port : getPorts()) {
            Port other = port.getConnectedTo();
            if (other != null) {
                other.connectTo(null);
            }
            port.connectTo(null);
        }

        reset();
        updateVisualState();
        System.out.println("Disconnecting Switch: isClosed = " + isClosed + ", ports: " + getPorts().size());

        // Trigger auto-simulation if a port was connected
        if (triggerPort != null) {
            triggerPort.triggerAutomaticSimulation();
        }
    }

    @Override
    public void simulate() {
        // Don't do anything unless both ports are connected
        if (!input.isConnected() || !output.isConnected()) {
            reset(); // ensure it doesn't carry ghost voltage
            updateVisualState();
            System.out.println("Switch not fully connected – skipping simulation");
            return;
        }

        // Get voltage from connected input port
        double inputVoltage = 0;
        if (input.getConnectedTo() != null) {
            inputVoltage = input.getConnectedTo().getVoltage();
            input.setVoltage(inputVoltage);
        }

        if (!isClosed) {
            reset(); // reset output and internal voltage if open
            System.out.println("Switch is OPEN – blocking voltage: " + (inputVoltage > 0 ? "Found " + inputVoltage + "V" : "No voltage"));
            updateVisualState();
            return;
        }

        // If closed and voltage is present, pass it
        if (inputVoltage > 0) {
            output.setVoltage(inputVoltage);
            setVoltage(inputVoltage);
            System.out.println("Switch CLOSED – passing voltage: " + inputVoltage);
        } else {
            output.setVoltage(0);
            setVoltage(0);
            System.out.println("Switch CLOSED – but no voltage to pass");
        }

        updateVisualState();
    }


    @Override
    public List<Port> getPorts() {
        return List.of(input, output);
    }

    @Override
    public boolean isPowered() {
        // A switch is only powered when closed AND both ports have voltage
        if (!isClosed) {
            return false;
        }
        return (input.getVoltage() > 0 && output.getVoltage() > 0);
    }

    @Override
    public void propagatePower(Set<Component> visited) {
        if (!visited.add(this)) return;

        //Only propagate power through the switch if it's closed
        if (!isClosed) {
            System.out.println("Switch is OPEN – blocking power propagation");
            updateVisualState();
            return;
        }

        // Only propagate if switch has power itself
        if (getVoltage() <= 0) {
            System.out.println("Switch has no voltage to propagate");
            updateVisualState();
            return;
        }

        if (input.isConnected()) {
            Object parent = input.getConnectedTo().getParent();
            if (parent instanceof Component c) {
                c.propagatePower(visited);
            }
        }

        if (output.isConnected()) {
            Object parent = output.getConnectedTo().getParent();
            if (parent instanceof Component c) {
                c.propagatePower(visited);
            }
        }

        updateVisualState();
    }

    @Override
    public void updateVisualState() {
        if (view == null) return;

        String imagePath = isClosed
                ? "/org/example/circuit_project/images/SwitchClosed.png"
                : "/org/example/circuit_project/images/switch.png";

        Image newImage = new Image(getClass().getResource(imagePath).toExternalForm());

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), view);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            view.setImage(newImage);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), view);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

}