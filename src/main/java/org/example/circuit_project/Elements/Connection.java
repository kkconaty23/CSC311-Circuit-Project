package org.example.circuit_project.Elements;
//Each component needs to connect to another(or else whats the point). Ex. a battery(2 connections ports) will connect to a
//lightbulb(also 2) via a wire(2 ports) SOOOOOOOO each component here will have ports with connections
//  in between other ports. so YEAH, this class will take care of that
//Each connection should have the following:
//- a unique id
//- sourceComponentID( the ID of the component that this connection is coming from)
//- sourcePortID (the ID of the port where theconnection is coming from)
//- targetComponentID( same thing as source but, yanno, for the target)
//- targetPortID( that stuff ^^ up there but target)

public class Connection {
    private int connectionID;
    private int sourceComponentID;
    private int sourcePortID;
    private int targetComponentID;
    private int targetPortID;

    public Connection(int connectionID, int sourceComponentID, int sourcePortID, int targetComponentID, int targetPortID) {
        this.connectionID = connectionID;
        this.sourceComponentID = sourceComponentID;
        this.sourcePortID = sourcePortID;
        this.targetComponentID = targetComponentID;
        this.targetPortID = targetPortID;
    }
    public int getConnectionID() {
        return connectionID;
    }
    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }
    public int getSourceComponentID() {
        return sourceComponentID;
    }
    public void setSourceComponentID(int sourceComponentID) {
        this.sourceComponentID = sourceComponentID;
    }
    public int getSourcePortID() {
        return sourcePortID;
    }
    public void setSourcePortID(int sourcePortID) {
        this.sourcePortID = sourcePortID;
    }
    public int getTargetComponentID() {
        return targetComponentID;
    }
    public void setTargetComponentID(int targetComponentID) {
        this.targetComponentID = targetComponentID;
    }
    public int getTargetPortID() {
        return targetPortID;
    }
    public void setTargetPortID(int targetPortID) {
        this.targetPortID = targetPortID;
    }
}
