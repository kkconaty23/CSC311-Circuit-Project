package org.example.circuit_project;

/**
 * A lightweight data container used for serializing and deserializing circuit components
 * to/from persistent storage formats such as JSON.
 * <p>
 * This class holds minimal metadata needed to reconstruct a component:
 * <ul>
 *     <li>{@code id} - Unique identifier</li>
 *     <li>{@code type} - Component type (e.g., "Battery", "Lightbulb")</li>
 *     <li>{@code x}, {@code y} - Position coordinates in the UI</li>
 *     <li>{@code voltage} - Voltage level (used for batteries)</li>
 *     <li>{@code isClosed} - For switches, whether the circuit is closed</li>
 * </ul>
 */
class SerializableComponent {

    /** Default constructor required for deserialization (e.g., Gson, Jackson) */
    public SerializableComponent() {
    }
    public String id, type;
    public double x, y, voltage;
    public boolean isClosed;
}
