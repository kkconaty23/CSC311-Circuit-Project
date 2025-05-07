// === Lightbulb.java ===
package org.example.circuit_project.Components;

import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.example.circuit_project.CircuitUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a Lightbulb component in the circuit simulator.
 *
 * The Lightbulb requires two connected ports forming a complete circuit loop,
 * and a voltage difference across those ports to be considered "powered".
 * When powered, its visual state changes to a lit bulb.
 */
public class Lightbulb extends Component {
    private final Port input;
    private final Port output;

    /**
     * Constructs a new Lightbulb component with an image view.
     *
     * @param view the ImageView representing the bulb
     */
    public Lightbulb(ImageView view) {
        super(view);
        this.input = new Port(this, 0.4, 1.05);
        this.output = new Port(this, 0.6, 1.05);
    }

    /**
     * Simulates the lightbulb logic:
     * - Requires both input and output ports to be connected
     * - Ensures ports are in the same loop
     * - Powers the bulb only if voltage difference exists between ports
     */
    @Override
    public void simulate() {
        Port inputTo = input.getConnectedTo();
        Port outputTo = output.getConnectedTo();

        //Require both connections to be physically present
        if (inputTo == null || outputTo == null) {
            input.setVoltage(0);
            output.setVoltage(0);
            setVoltage(0);
            System.out.println("Lightbulb simulate: one or both ports NOT connected");
            return;
        }

        //Ensure both are connected in the same loop
        if (!CircuitUtils.arePortsInSameLoop(inputTo, outputTo)) {
            input.setVoltage(0);
            output.setVoltage(0);
            setVoltage(0);
            System.out.println("Lightbulb simulate: ports not in same loop");
            return;
        }

        //Step 3: Check for voltage difference
        double voltageA = inputTo.getVoltage();
        double voltageB = outputTo.getVoltage();

        System.out.println("Bulb input connected to voltage: " + voltageA);
        System.out.println("Bulb output connected to voltage: " + voltageB);

        if (voltageA != voltageB) {
            input.setVoltage(voltageA);
            output.setVoltage(voltageB);
            setVoltage(Math.abs(voltageA - voltageB));
            System.out.println("Lightbulb activated in closed loop with voltage: " + getVoltage());
        } else {
            input.setVoltage(0);
            output.setVoltage(0);
            setVoltage(0);
            System.out.println("Loop exists but no voltage difference");
        }
    }

    /**
     * Resets the bulb’s voltage state and updates its visual.
     */
    public void reset() {
        for (Port port : getPorts()) {
            port.setVoltage(0);
        }
        setVoltage(0); // For components that store internal voltage
        isPowered();
        updateVisualState();
    }

    /**
     * Disconnects both input and output ports and resets the visual and voltage state.
     */
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

    /**
     * Returns the ports of this bulb: input and output.
     *
     * @return a list containing both ports
     */
    @Override
    public List<Port> getPorts() {
        return List.of(input, output);
    }

    /**
     * Checks whether the bulb is powered (both ports connected and voltage difference exists).
     *
     * @return true if powered; false otherwise
     */
    @Override
    public boolean isPowered() {
        return input.isConnected() && output.isConnected() && getVoltage() > 0;
    }

    /**
     * Propagates power recursively from this bulb to downstream components.
     *
     * @param visited the set of components already visited
     */
    @Override
    public void propagatePower(Set<Component> visited) {
        if (!visited.add(this)) return;
        updateVisualState();
        if (output.isConnected()) {
            output.getConnectedTo().getParentComponent().propagatePower(visited);


        }
    }

    /**
     * Updates the visual appearance of the bulb (lit or unlit) based on power state.
     */
    @Override
    public void updateVisualState() {
        if (view == null) return;

        String imagePath = isPowered()
                ? "/org/example/circuit_project/images/litLightbulb.png"
                : "/org/example/circuit_project/images/unlitLightbulb.png";

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