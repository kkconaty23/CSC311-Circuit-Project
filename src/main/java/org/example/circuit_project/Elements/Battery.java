package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "battery")
public class Battery extends Component {
    private double voltage;

    public Battery() {} // Required by JAXB

    public Battery(double x, double y, double voltage) {
        super(x, y);
        this.voltage = voltage;
    }

    @XmlElement
    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }
}
