package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Battery class extends component for position
 * has its own voltage
 */
@XmlRootElement(name = "battery")
public class Battery extends Component {
    private double voltage;

    /**
     * default Battery constructor JAXB req
     */
    public Battery() {} // Required by JAXB

    /**
     * Battery constructor
     * @param x
     * @param y
     * @param voltage
     */
    public Battery(double x, double y, double voltage) {
        super(x, y);
        this.voltage = voltage;
    }

    /**
     * get Voltage method of a battery
     * @return
     */
    @XmlElement
    public double getVoltage() {
        return voltage;
    }

    /**
     * set voltage method for batteries
     * @param voltage
     */
    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }
}
