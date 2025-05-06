// === Lightbulb.java ===
package org.example.circuit_project.Components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.circuit_project.CircuitUtils;

import java.util.HashSet;
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
        Port inputTo = input.getConnectedTo();
        Port outputTo = output.getConnectedTo();

        // 🔒 Step 1: Require both connections to be physically present
        if (inputTo == null || outputTo == null) {
            input.setVoltage(0);
            output.setVoltage(0);
            setVoltage(0);
            System.out.println("🚫 Lightbulb simulate: one or both ports NOT connected");
            return;
        }

        // 🔒 Step 2: Ensure both are connected in the same loop
        if (!CircuitUtils.arePortsInSameLoop(inputTo, outputTo)) {
            input.setVoltage(0);
            output.setVoltage(0);
            setVoltage(0);
            System.out.println("🚫 Lightbulb simulate: ports not in same loop");
            return;
        }

        // ⚡ Step 3: Check for voltage difference
        double voltageA = inputTo.getVoltage();
        double voltageB = outputTo.getVoltage();

        System.out.println("🔍 Bulb input connected to voltage: " + voltageA);
        System.out.println("🔍 Bulb output connected to voltage: " + voltageB);

        if (voltageA != voltageB) {
            input.setVoltage(voltageA);
            output.setVoltage(voltageB);
            setVoltage(Math.abs(voltageA - voltageB));
            System.out.println("💡 Lightbulb activated in closed loop with voltage: " + getVoltage());
        } else {
            input.setVoltage(0);
            output.setVoltage(0);
            setVoltage(0);
            System.out.println("⚠️ Loop exists but no voltage difference");
        }
    }






    private boolean arePortsInSameLoop(Port a, Port b) {
        Set<Component> visited = new HashSet<>();
        return dfsBetweenPorts(a.getParentComponent(), b, visited);
    }

    private boolean dfsBetweenPorts(Component current, Port targetPort, Set<Component> visited) {
        if (!visited.add(current)) return false;

        for (Port port : current.getPorts()) {
            Port connected = port.getConnectedTo();
            if (connected != null) {
                if (connected == targetPort) return true;
                Component next = connected.getParentComponent();
                if (dfsBetweenPorts(next, targetPort, visited)) return true;
            }
        }

        return false;
    }
    public void reset() {
        for (Port port : getPorts()) {
            port.setVoltage(0);
        }
        setVoltage(0); // For components that store internal voltage
        isPowered();
        updateVisualState();
    }
    @Override
    public void disconnect() {
        for (Port port : getPorts()) {
            if (port.getConnectedTo() != null) {
                port.getConnectedTo().connectTo(null); // safely breaks the other side
            }
            port.connectTo(null); // safely breaks this side


            reset();
            updateVisualState();
        }
    }











    @Override
    public List<Port> getPorts() {
        return List.of(input, output);
    }

    @Override
    public boolean isPowered() {
        return input.isConnected() && output.isConnected() && getVoltage() > 0;
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