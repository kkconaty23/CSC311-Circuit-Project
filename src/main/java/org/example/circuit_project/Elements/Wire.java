package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Wire class extendes components
 */
@XmlRootElement(name = "wire")
public class Wire extends Component {
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private double resistance;

    /**
     * default required by JAXB
     */
    public Wire() {}

    /**
     * Wire constructor method
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public Wire(double startX, double startY, double endX, double endY) {
        super((startX + endX) / 2, (startY + endY) / 2); // Position at center of wire
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.resistance = 0.01; // Default minimal resistance in ohms
    }

    /**
     * get X position
     * @return
     */
    @XmlElement
    public double getStartX() {
        return startX;
    }

    /**
     * set X position
     * @param startX
     */
    public void setStartX(double startX) {
        this.startX = startX;
        updatePosition();
    }

    /**
     * get Y posiiton
     * @return
     */
    @XmlElement
    public double getStartY() {
        return startY;
    }

    /**
     * set Y position
     * @param startY
     */
    public void setStartY(double startY) {
        this.startY = startY;
        updatePosition();
    }

    /**
     * get X end
     * @return
     */
    @XmlElement
    public double getEndX() {
        return endX;
    }

    /**
     * set X end
     * @param endX
     */
    public void setEndX(double endX) {
        this.endX = endX;
        updatePosition();
    }

    /**
     * get Y end position
     * @return
     */
    @XmlElement
    public double getEndY() {
        return endY;
    }

    /**
     * set Y end position
     * @param endY
     */
    public void setEndY(double endY) {
        this.endY = endY;
        updatePosition();
    }

    /**
     * get wire resistance
     * @return
     */
    @XmlElement
    public double getResistance() {
        return resistance;
    }

    /**
     * set wire resistance
     * @param resistance
     */
    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    /**
     * get the length of a wire
     * @return
     */
    public double getLength() {
        double dx = endX - startX;
        double dy = endY - startY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * update position of a wire based on length
     */
    private void updatePosition() {
        setX((startX + endX) / 2);
        setY((startY + endY) / 2);
    }
}