package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "lightbulb")
public class Lightbulb extends Component {
    private double resistance;
    private boolean isOn;

    public Lightbulb() {} // Required by JAXB

    public Lightbulb(double x, double y) {
        super(x, y);
        this.resistance = 10.0; // Default resistance in ohms
        this.isOn = false;
    }

    public Lightbulb(double x, double y, double resistance) {
        super(x, y);
        this.resistance = resistance;
        this.isOn = false;
    }

    @XmlElement
    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    @XmlElement
    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    // Method to toggle the light on/off
    public void toggle() {
        isOn = !isOn;
    }
}