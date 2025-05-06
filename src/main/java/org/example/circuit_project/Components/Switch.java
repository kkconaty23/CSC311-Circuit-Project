package org.example.circuit_project.Components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.Set;

public class Switch extends Component {
    private final Port input;
    private final Port output;
    private boolean isClosed;

    public Switch(ImageView view) {
        super(view);
        this.input = new Port(this, 0.1, 0.5);
        this.output = new Port(this, 0.9, 0.5);
        this.isClosed = false;
    }

    public void toggle() {
        isClosed = !isClosed;
        System.out.println("🔁 Switch toggled: " + (isClosed ? "CLOSED" : "OPEN"));
        updateVisualState();
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void reset() {
        for (Port port : getPorts()) {
            port.setVoltage(0);
        }
        setVoltage(0); // For components that store internal voltage
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
        System.out.println("Disconnecting Switch: isClosed = " + isClosed + ", ports: " + getPorts().size());
    }

    @Override
    public void simulate() {
        // Always start with zero voltage
        input.setVoltage(0);
        output.setVoltage(0);
        setVoltage(0);

        if (!input.isConnected() || !output.isConnected()) {
            System.out.println("⛔ Switch not fully connected – no simulation");
            return;
        }

        // Find which port has voltage coming in (could be either input or output depending on circuit)
        double inputVoltage = 0;
        if (input.getConnectedTo() != null) {
            inputVoltage = input.getConnectedTo().getVoltage();
            input.setVoltage(inputVoltage);
        }

        if (!isClosed) {
            System.out.println("⛔ Switch is OPEN – blocking voltage: " + (inputVoltage > 0 ? "Found " + inputVoltage + "V" : "No voltage"));
            return; // Don't pass voltage when switch is open
        }

        // When closed, pass voltage from input to output
        if (inputVoltage > 0) {
            output.setVoltage(inputVoltage);
            setVoltage(inputVoltage);
            System.out.println("✅ Switch CLOSED – passing voltage: " + inputVoltage);
        } else {
            System.out.println("✅ Switch CLOSED – but no voltage to pass");
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
        if (!visited.add(this)) return; // 👈 Prevents loops

        // Critical fix: Only propagate power through the switch if it's closed
        if (!isClosed) {
            System.out.println("⛔ Switch is OPEN – blocking power propagation");
            updateVisualState();
            return;
        }

        // Only propagate if switch has power itself
        if (getVoltage() <= 0) {
            System.out.println("⛔ Switch has no voltage to propagate");
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

    public void updateVisualState() {
        if (view == null) return;

        String img = isClosed
                ? "/org/example/circuit_project/images/SwitchClosed.png"
                : "/org/example/circuit_project/images/switch.png";

        System.out.println("🔄 Updating switch view to: " + img);
        view.setImage(new Image(getClass().getResource(img).toExternalForm()));
    }
}