// === Switch.java ===
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
        input.setVoltage(0);
        output.setVoltage(0);
        setVoltage(0);

        if (!input.isConnected() || !output.isConnected()) {
            System.out.println("⛔ Switch not fully connected – no simulation");
            return;
        }

        if (!isClosed) {
            System.out.println("⛔ Switch is OPEN – blocking voltage");
            return;
        }

        // ✅ Pull the voltage *from the connected port* and assign it to input
        double incomingVoltage = input.getConnectedTo().getVoltage();
        input.setVoltage(incomingVoltage); // <== ADD THIS!
        output.setVoltage(incomingVoltage);
        setVoltage(incomingVoltage);

        System.out.println("✅ Switch CLOSED – passing voltage: " + incomingVoltage);

        updateVisualState();
    }








    @Override
    public List<Port> getPorts() {
        return List.of(input, output);
    }

    @Override
    public boolean isPowered() {
        return isClosed && input.getVoltage() > 0 && output.getVoltage() > 0;
    }


    @Override
    public void propagatePower(Set<Component> visited) {
        if (!visited.add(this)) return; // 👈 Prevents loops

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
