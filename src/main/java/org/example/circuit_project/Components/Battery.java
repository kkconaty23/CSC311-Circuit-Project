package org.example.circuit_project.Components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.Set;

public class Battery extends Component {
    private final Port positive;
    private final Port negative;
    private final double outputVoltage;

    public Battery(ImageView view, double voltage) {
        super(view);
        this.outputVoltage = voltage;
        this.positive = new Port(this, 0.1, 0.0);
        this.negative = new Port(this, 0.9, 0.0);

        if (view != null) {
            view.setImage(new Image(getClass().getResource("/org/example/circuit_project/images/battery.png").toExternalForm()));
        }
    }


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
    }






    @Override
    public List<Port> getPorts() {
        return List.of(positive, negative);
    }

    @Override
    public boolean isPowered() {
        return true;
    }

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
