package org.example.circuit_project.Sandbox;

import org.example.circuit_project.Components.Component;
import org.example.circuit_project.Components.Port;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class providing helper methods for analyzing circuit connectivity.
 */
public class CircuitUtils {

    /**
     * Checks whether two ports belong to the same electrical loop,
     * meaning there is a path between them through connected wires and components.
     *
     * @param a the starting port
     * @param b the target port to check for a path
     * @return true if the two ports are in the same loop; false otherwise
     */
    public static boolean arePortsInSameLoop(Port a, Port b) {
        Set<Port> visited = new HashSet<>();
        return dfsThroughComponents(a, b, visited);
    }

    /**
     * Performs a depth-first search (DFS) from one port to another, following:
     * - direct port-to-port connections (e.g., via wires)
     * - sibling ports on the same component (e.g., switches, bulbs)
     *
     * @param current the current port being visited
     * @param target the target port being searched for
     * @param visited a set of ports that have already been visited to avoid cycles
     * @return true if a path to the target port is found; false otherwise
     */
    private static boolean dfsThroughComponents(Port current, Port target, Set<Port> visited) {
        if (current == null || !visited.add(current)) return false;
        if (current == target) return true;

        // Follow wire connection (port-to-port)
        Port directlyConnected = current.getConnectedTo();
        if (directlyConnected != null && dfsThroughComponents(directlyConnected, target, visited)) {
            return true;
        }

        // Follow to other ports on the same component
        Component parent = current.getParentComponent();
        for (Port sibling : parent.getPorts()) {
            if (sibling != current && dfsThroughComponents(sibling, target, visited)) {
                return true;
            }
        }

        return false;
    }
}
