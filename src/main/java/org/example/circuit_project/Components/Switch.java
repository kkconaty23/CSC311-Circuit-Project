package org.example.circuit_project.Components;

import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.List;
import java.util.Set;

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
        isClosed = !isClosed;
        System.out.println("Switch toggled: " + (isClosed ? "CLOSED" : "OPEN"));
        updateVisualState();
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