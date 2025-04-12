package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSeeAlso;

/**
 * component base class that all other components extend
 * Main uses is giving components IDs and saving their X, Y coordinates
 */
@XmlSeeAlso({Battery.class, Lightbulb.class, Wire.class})
public abstract class Component {
    private String id;
    private double x;
    private double y;

    /**
     * JAXB which is used for XML file loading, requires empty default constructor
     */
    public Component() {
        // Default constructor required for JAXB
    }

    /**
     * Component constructor
     * @param x
     * @param y
     */
    public Component(double x, double y) {
        this.x = x;
        this.y = y;
    }


    /**
     * method to get the ID of a component
     * @return
     */
    @XmlID
    @XmlAttribute
    public String getId() {
        return id;
    }

    /**
     * method to set the component ID
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * method to get X coordinate
     * @return
     */
    @XmlElement
    public double getX() {
        return x;
    }

    /**
     * method to set X coordinate
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * method to get Y coordinate
     * @return
     */
    @XmlElement
    public double getY() {
        return y;
    }

    /**
     * method to set Y coordinate
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }
}