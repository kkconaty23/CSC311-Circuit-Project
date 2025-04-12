package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "circuit")
public class Circuit {
    private long id;
    private String name;
    private List<Battery> batteries = new ArrayList<>();
    private List<Lightbulb> lightbulbs = new ArrayList<>();
    private List<Wire> wires = new ArrayList<>();

    public Circuit() {
        // Default constructor required for JAXB
    }

    @XmlElement(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "batteries")
    @XmlElement(name = "battery")
    public List<Battery> getBatteries() {
        return batteries;
    }

    public void setBatteries(List<Battery> batteries) {
        this.batteries = batteries;
    }

    public void addBattery(Battery battery) {
        this.batteries.add(battery);
    }

    @XmlElementWrapper(name = "lightbulbs")
    @XmlElement(name = "lightbulb")
    public List<Lightbulb> getLightbulbs() {
        return lightbulbs;
    }

    public void setLightbulbs(List<Lightbulb> lightbulbs) {
        this.lightbulbs = lightbulbs;
    }

    public void addLightbulb(Lightbulb lightbulb) {
        this.lightbulbs.add(lightbulb);
    }

    @XmlElementWrapper(name = "wires")
    @XmlElement(name = "wire")
    public List<Wire> getWires() {
        return wires;
    }

    public void setWires(List<Wire> wires) {
        this.wires = wires;
    }

    public void addWire(Wire wire) {
        this.wires.add(wire);
    }
}