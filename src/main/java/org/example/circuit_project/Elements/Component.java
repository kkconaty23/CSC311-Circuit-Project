package org.example.circuit_project.Elements;


import java.util.List;

// each component will be a  type (battery, wire, lightbulb, fuse, switch, resistor)
//component will have the following:
//- a unique ID
//- an x(int) and y(int) coordinate to show its location on the circuit board
//- rotate(float) to determine its rotation
//- properties (voltage, resistance)
//- a list of ports
public class Component {
    private int id;
    private String name = "default";
    private int xCoord;
    private int yCoord;
    private float rotate;
    private String properties;
    private List<Port> portList;

    public Component(int id, String name, int xCoord, int yCoord, float rotate, String properties, List<Port> portList) {

        //NOTE:Unique idea on separate machine
        this.id = id;


        this.name = name;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.rotate = rotate;
        this.properties = properties;
        this.portList = portList;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getxCoord() {
        return xCoord;
    }
    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }
    public int getyCoord() {
        return yCoord;
    }
    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }
    public float getRotate() {
        return rotate;
    }
    public void setRotate(float rotate) {
        this.rotate = rotate;
    }
    public String getProperties() {
        return properties;
    }
    public void setProperties(String properties) {
        this.properties = properties;
    }
    public List<Port> getPortList() {
        return portList;
    }
    public void setPortList(List<Port> portList) {
        this.portList = portList;
    }
}
