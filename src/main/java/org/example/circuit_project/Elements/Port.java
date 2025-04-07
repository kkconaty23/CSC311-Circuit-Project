package org.example.circuit_project.Elements;
//port for each component
//im tired now
//each port will have an ID, and relative X and Y (relative to the component)
public class Port {
    private int portID;

    public Port(int portID) {
        this.portID = portID;
    }
    public int getPortID() {
        return portID;
    }
    public void setPortID(int portID) {
        this.portID = portID;
    }
}
