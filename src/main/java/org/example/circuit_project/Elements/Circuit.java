package org.example.circuit_project.Elements;

import java.util.List;

//HERE TO REFER?
//Everything has a particular order and format and follows java conventions. So, please follow this

/**
 * The circuit class acts a bottom layer for any circuit sandbox instance. It will hold all components and keep track
 * of connections between components. Each circuit will have a user defined name and can be unique identified by its
 * circuit ID.
 */
public class Circuit {
    private int id;
    private String name;
    private List<Component> componentList;
    private List<Connection> connectionList;

    /**
     * Constructor for circuit object.
     *
     * @param id             unique ID for circuit
     * @param name           user created name for circuit instance
     * @param componentList  list of all components for a given circuit instance
     * @param connectionList list of all component connections for a given circuit instance
     */
    public Circuit(int id, String name, List<Component> componentList, List<Connection> connectionList) {
        this.id = id;
        this.name = name;
        this.componentList = componentList;
        this.connectionList = connectionList;
    }

    /**
     * Accessor method to acquire unique circuit ID
     *
     * @return the unique identifier for this circuit
     */
    public int getId() {
        return id;
    }

    /**
     * Mutator method to assign a unique circuit ID
     *
     * @param id unique identifying ID for this circuit
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Accessor method to acquire user defined circuit name
     *
     * @return the name of the circuit
     */
    public String getName() {
        return name;
    }

    /**
     * Mutator method to assign a circuit a user defined name
     *
     * @param name user defined name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Accessor method to acquire the list of components in this circuit
     *
     * @return list of all components in this circuit
     */
    public List<Component> getComponentList() {
        return componentList;
    }

    /**
     * Mutator method to create a list of components for a given circuit
     *
     * @param componentList list of components in a circuit
     */
    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    /**
     * Accessor method to acquire list of connections for this circuit
     *
     * @return list of connections in this circuit
     */
    public List<Connection> getConnectionList() {
        return connectionList;
    }

    /**
     * Mutator method to create a list of component connections for a given circuit
     *
     * @param connectionList list of connections for this circuit
     */
    public void setConnectionList(List<Connection> connectionList) {
        this.connectionList = connectionList;
    }


}
