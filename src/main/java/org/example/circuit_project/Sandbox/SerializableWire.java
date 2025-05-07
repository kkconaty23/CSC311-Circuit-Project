package org.example.circuit_project.Sandbox;


/**
 * A lightweight data structure for serializing and deserializing wire connections
 * in a circuit project, typically used when saving to or loading from JSON.
 * <p>
 * This class captures the minimum information needed to reconstruct a wire:
 * <ul>
 *     <li>{@code startComponentId} and {@code endComponentId} to identify connected components</li>
 *     <li>{@code startPortIndex} and {@code endPortIndex} to identify specific ports</li>
 *     <li>{@code startX}, {@code startY}, {@code endX}, {@code endY} for line placement</li>
 * </ul>
 */
public class SerializableWire {

    /** Default constructor required for deserialization libraries (e.g., Gson) */
    public SerializableWire() {
    }
    public String startComponentId;
    public int startPortIndex;
    public String endComponentId;
    public int endPortIndex;
    public double startX, startY, endX, endY;
}
