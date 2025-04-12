package org.example.circuit_project.Elements;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

/**
 * circuit class base of the builds people make
 * has an ID, Name, Lists of the components added
 */
@XmlRootElement(name = "circuit")
public class Circuit {
    private long id;
    private String name;
    private List<Battery> batteries = new ArrayList<>();
    private List<Lightbulb> lightbulbs = new ArrayList<>();
    private List<Wire> wires = new ArrayList<>();

    /**
     * default constructor JAXB req
     */
    public Circuit() {
        // Default constructor required for JAXB
    }

    /**
     * getter for ID
     * @return
     */
    @XmlElement(name = "id")
    public long getId() {
        return id;
    }

    /**
     * setter for ID
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * getter for name
     * @return
     */
    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    /**
     * setter for name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * allows you to get hte list of batteries being used in circuit
     * @return
     */
    @XmlElementWrapper(name = "batteries")
    @XmlElement(name = "battery")
    public List<Battery> getBatteries() {
        return batteries;
    }

    /**
     * setter for list of batteries
     * @param batteries
     */
    public void setBatteries(List<Battery> batteries) {
        this.batteries = batteries;
    }

    /**
     * add battery method
     * @param battery
     */
    public void addBattery(Battery battery) {
        this.batteries.add(battery);
    }

    /**
     * allows you to get hte list of lightbulbs being used in circuit
     * @return
     */
    @XmlElementWrapper(name = "lightbulbs")
    @XmlElement(name = "lightbulb")
    public List<Lightbulb> getLightbulbs() {
        return lightbulbs;
    }

    /**
     * setter for list of bulbs
     * @param lightbulbs
     */
    public void setLightbulbs(List<Lightbulb> lightbulbs) {
        this.lightbulbs = lightbulbs;
    }

    /**
     * add bulbs to ciruict
     * @param lightbulb
     */
    public void addLightbulb(Lightbulb lightbulb) {
        this.lightbulbs.add(lightbulb);
    }

    /**
     * allows you to get hte list of wires being used in circuit
     * @return
     */
    @XmlElementWrapper(name = "wires")
    @XmlElement(name = "wire")
    public List<Wire> getWires() {
        return wires;
    }

    /**
     * setter for list of wires
     */
    public void setWires(List<Wire> wires) {
        this.wires = wires;
    }

    /**add wires to circuit
     * *
     *
     * @param wire
     */
    public void addWire(Wire wire) {
        this.wires.add(wire);
    }
}