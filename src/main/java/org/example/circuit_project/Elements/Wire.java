package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "wire")
public class Wire extends Component {
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private double resistance;

    public Wire() {} // Required by JAXB

    public Wire(double startX, double startY, double endX, double endY) {
        super((startX + endX) / 2, (startY + endY) / 2); // Position at center of wire
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.resistance = 0.01; // Default minimal resistance in ohms
    }

    public Wire(double startX, double startY, double endX, double endY, double resistance) {
        super((startX + endX) / 2, (startY + endY) / 2); // Position at center of wire
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.resistance = resistance;
    }

    @XmlElement
    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
        updatePosition();
    }

    @XmlElement
    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
        updatePosition();
    }

    @XmlElement
    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
        updatePosition();
    }

    @XmlElement
    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
        updatePosition();
    }

    @XmlElement
    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    // Calculate the length of the wire
    public double getLength() {
        double dx = endX - startX;
        double dy = endY - startY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Update the component's position to be at the center of the wire
    private void updatePosition() {
        setX((startX + endX) / 2);
        setY((startY + endY) / 2);
    }
}