package org.example.circuit_project.Components;

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
        this.positive = new Port(this, 0.5, -0.2);
        this.negative = new Port(this, 0.5, 1.2);
    }

    @Override
    public void simulate() {
        positive.setVoltage(outputVoltage);    // Full voltage out
        negative.setVoltage(0);                // Ground
        System.out.println("Battery simulate: positive=" + outputVoltage + ", negative=0");
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
