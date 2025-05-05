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
        updateVisualState(); // 🔽 Add this right after flipping the state
    }

    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void simulate() {
        if (input.isConnected()) {
            double voltage = input.getConnectedTo().getVoltage();
            setVoltage(voltage);
            output.setVoltage(voltage);
            System.out.println("Bulb simulate: input=" + input.getVoltage() + ", output=" + output.getVoltage());
        } else {
            setVoltage(0); // Make sure this resets when disconnected
            output.setVoltage(0);
            System.out.println("Bulb simulate: input NOT connected");
        }

        updateVisualState(); // 🔧 YOU NEED THIS TO REFLECT CHANGES
    }


    @Override
    public List<Port> getPorts() {
        return List.of(input, output);
    }

    @Override
    public boolean isPowered() {
        return isClosed && input.getVoltage() > 0;
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
        if (isPowered()) {
            view.setImage(new Image(getClass().getResource("/org/example/circuit_project/images/SwitchClosed.png").toExternalForm()));
        } else {
            view.setImage(new Image(getClass().getResource("/org/example/circuit_project/images/switch.png").toExternalForm()));
        }
    }

}
