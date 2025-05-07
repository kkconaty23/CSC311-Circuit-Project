package org.example.circuit_project.Project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a circuit project containing metadata such as name, description, timestamps,
 * user association, and a reference to blob storage for circuit data.
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

    /** @return The unique project ID
     *
     * */
    public String getId() {
        return id;
    }

    /**
     * @return The project name
     *
     * */
    public String getName() {
        return name;
    }

    /**
     * Sets the project name and updates the last modified timestamp.
     *
     * @param name New project name
     */
    public void setName(String name) {
        this.name = name;
        this.lastModified = LocalDateTime.now();
    }
    /** @return The project description
     *
     * */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the project description and updates the last modified timestamp.
     *
     * @param description New project description
     */
    public void setDescription(String description) {
        this.description = description;
        this.lastModified = LocalDateTime.now();
    }

    /** @return The user ID associated with this project
     *
     * */
    public String getUserId() {
        return userId;
    }


    /** @return The creation timestamp
     *
     * */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    /** @return The last modified timestamp
     *
     * */
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    /**
     * Manually sets the last modified timestamp (used when restoring from DB).
     *
     * @param lastModified Timestamp to set
     */
    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    /** @return The blob storage reference for this project
     * */
    public String getBlobReference() {
        return blobReference;
    }

    /**
     * Sets the blob storage reference.
     *
     * @param blobReference Blob reference filename or path
     */
    public void setBlobReference(String blobReference) {
        this.blobReference = blobReference;
    }

    /**
     * @return String representation of the project, including name and last modified date
     */
    @Override
    public String toString() {
        return name + " (Last modified: " + lastModified.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ")";
    }
}