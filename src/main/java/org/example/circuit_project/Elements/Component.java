package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({Battery.class, Lightbulb.class, Wire.class})
public abstract class Component {
    private String id;
    private double x;
    private double y;

    public Component() {
        // Default constructor required for JAXB
    }

    public Component(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @XmlID
    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @XmlElement
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}