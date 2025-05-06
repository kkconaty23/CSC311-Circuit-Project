package org.example.circuit_project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a circuit project with its associated metadata
 */
public class Project {
    private final String id;
    private String name;
    private String description;
    private final String userId;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String blobReference;

    /**
     * Constructor for creating a Project object
     *
     * @param id            Unique identifier for the project
     * @param name          Project name
     * @param description   Project description
     * @param userId        ID of the user who owns the project
     * @param createdAt     Date and time when the project was created
     * @param lastModified  Date and time when the project was last modified
     * @param blobReference Reference to the project data in blob storage
     */
    public Project(String id, String name, String description, String userId,
                   LocalDateTime createdAt, LocalDateTime lastModified, String blobReference) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.blobReference = blobReference;
    }

    /**
     * Constructor for creating a new project
     *
     * @param name        Project name
     * @param description Project description
     * @param userId      ID of the user who owns the project
     */
    public Project(String name, String description, String userId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.blobReference = this.id + ".circuit";
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.lastModified = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.lastModified = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getBlobReference() {
        return blobReference;
    }

    public void setBlobReference(String blobReference) {
        this.blobReference = blobReference;
    }

    @Override
    public String toString() {
        return name + " (Last modified: " + lastModified.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ")";
    }
}