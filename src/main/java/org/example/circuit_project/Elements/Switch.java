package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Switch class extends component for position
 * has its own state (open or closed)
 */
@XmlRootElement(name = "switch")
public class Switch extends Component {
    private boolean closed; // true = closed circuit, false = open circuit

    /**
     * default Switch constructor JAXB req
     */
    public Switch() {} // Required by JAXB

    /**
     * Switch constructor
     * @param x X-coordinate position
     * @param y Y-coordinate position
     * @param closed Initial state of the switch (true = closed, false = open)
     */
    public Switch(double x, double y, boolean closed) {
        super(x, y);
        this.closed = closed;
    }

    /**
     * Check if switch is closed
     * @return true if switch is closed (conducting), false if open (not conducting)
     */
    @XmlElement
    public boolean isClosed() {
        return closed;
    }

    /**
     * Set switch state
     * @param closed true to close the switch, false to open it
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Toggle the switch state
     * @return new state of the switch after toggling
     */
    public boolean toggle() {
        this.closed = !this.closed;
        return this.closed;
    }
}