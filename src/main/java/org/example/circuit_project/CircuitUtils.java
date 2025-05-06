package org.example.circuit_project;

import org.example.circuit_project.Components.Component;
import org.example.circuit_project.Components.Port;

import java.util.HashSet;
import java.util.Set;

public class CircuitUtils {

    public static boolean arePortsInSameLoop(Port a, Port b) {
        Set<Port> visited = new HashSet<>();
        return dfsThroughComponents(a, b, visited);
    }

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
