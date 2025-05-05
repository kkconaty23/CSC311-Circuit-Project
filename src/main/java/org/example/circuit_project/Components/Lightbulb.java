// === Lightbulb.java ===
package org.example.circuit_project.Components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.Set;

public class Lightbulb extends Component {
    private final Port input;
    private final Port output;

    public Lightbulb(ImageView view) {
        super(view);
        this.input = new Port(this, 0.2, 0.5);
        this.output = new Port(this, 0.8, 0.5);
    }

    @Override
    public void simulate() {
        if (input.isConnected()) {
            double voltage = input.getConnectedTo().getVoltage();
            System.out.println("🔌 Lightbulb connected to: " + input.getConnectedTo().getParent().getClass().getSimpleName());
            System.out.println("⚡ Voltage received from connected port: " + voltage);
            input.setVoltage(voltage);
            setVoltage(voltage);
            output.setVoltage(voltage);
            System.out.println("Bulb simulate: input=" + input.getVoltage() + ", output=" + output.getVoltage());
        } else {
            input.setVoltage(0);
            output.setVoltage(0);
            setVoltage(0);
            System.out.println("Bulb simulate: input NOT connected");
        }
        updateVisualState();
    }




    @Override
    public List<Port> getPorts() {
        return List.of(input, output);
    }

    @Override
    public boolean isPowered() {
        return input.getVoltage() > 0;
    }


    @Override
    public void propagatePower(Set<Component> visited) {
        if (!visited.add(this)) return;
        updateVisualState();
        if (output.isConnected()) {
            output.getConnectedTo().getParentComponent().propagatePower(visited);


        }
    }



    public void updateVisualState() {
        if (view == null) return;
        if (isPowered()) {
            System.out.println("Lightbulb has been propagated");
            view.setImage(new Image(getClass().getResource("/org/example/circuit_project/images/litLightbulb.png").toExternalForm()));
        } else {
            System.out.println("Lightbulb has not been propagated");
            view.setImage(new Image(getClass().getResource("/org/example/circuit_project/images/unlitLightbulb.png").toExternalForm()));
        }
    }


}