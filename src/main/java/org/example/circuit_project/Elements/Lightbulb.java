package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Lightbulb class extends component for its XY coordinates
 * contains an isOn method which we can use to enable light once connected
 */
@XmlRootElement(name = "lightbulb")
public class Lightbulb extends Component {
    private double resistance;
    private boolean isOn;

    /**
     * Default constructor JAXB req
     */
    public Lightbulb() {}

    /**
     * constructor for light bulbs with default resistance
     * @param x
     * @param y
     */
    public Lightbulb(double x, double y) {
        super(x, y);
        this.resistance = 10.0; // Default resistance in ohms
        this.isOn = false;
    }


    /**
     * method used to  get the resistance of a bulb
     * @return
     */
    @XmlElement
    public double getResistance() {
        return resistance;
    }

    /**
     * method usd to set resistance
     * @param resistance
     */
    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    /**
     * method used to check if a bulb is on or not
     * @return
     */
    @XmlElement
    public boolean isOn() {
        return isOn;
    }

    /**
     * method to set bulb on
     * @param on
     */
    public void setOn(boolean on) {
        isOn = on;
    }

    /**
     * method to toggle lightbulb on and off will be used when connecting circuits
     */
    public void toggle() {
        isOn = !isOn;
    }
}