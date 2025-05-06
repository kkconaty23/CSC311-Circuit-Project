/**
 * This class integrates project data with Azure Blob Storage
 */
package org.example.circuit_project.Storage;

import org.example.circuit_project.Project;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectBlobManager {
    private final BlobDbOpps blobStorage;
    private final DbOpps dbConnection;

    public ProjectBlobManager() {
        this.blobStorage = new BlobDbOpps();
        this.dbConnection = new DbOpps();
    }

    /**
     * Saves project data to blob storage and updates database
     * @param project The project to save
     * @param projectData String representation of circuit data
     * @return true if successful, false otherwise
     */
    public boolean saveProject(Project project, String projectData) {
        boolean success = false;
        File tempFile = null;

        try {
            // Create temporary file with project data
            tempFile = File.createTempFile("circuit_", ".tmp");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(projectData);
            }

            // Upload to blob storage using project ID as name
            String blobName = project.getBlobReference();
            blobStorage.uploadFile(blobName, tempFile.getAbsolutePath());

            // Update project in database
            success = dbConnection.updateProject(project);

        } catch (IOException e) {
            System.err.println("Error saving project to blob storage: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up temp file
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        return success;
    }

    /**
     * Loads project data from blob storage
     * @param project The project to load
     * @return Project data as string, or null if failed
     */
    public String loadProject(Project project) {
        String projectData = null;
        File tempFile = null;

        try {
            // Create temp file for download
            tempFile = File.createTempFile("circuit_download_", ".tmp");
            String blobName = project.getBlobReference();

            // Download from blob storage
            blobStorage.downloadFile(blobName, tempFile.getAbsolutePath());

            // Read file contents
            projectData = Files.readString(Path.of(tempFile.getAbsolutePath()));

        } catch (Exception e) {
            System.err.println("Error loading project from blob storage: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up temp file
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        return projectData;
    }

    /**
     * Deletes a project from both blob storage and database
     * @param projectId The ID of the project to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteProject(String projectId) {
        // First get the project to find its blob reference
        Project project = dbConnection.getProjectById(projectId);
        if (project == null) {
            return false;
        }

        try {
            // Delete from database
            boolean dbDeleted = dbConnection.deleteProject(projectId);
            if (!dbDeleted) {
                return false;
            }

            // Delete from blob storage
            if (blobStorage.deleteBlob(project.getBlobReference())) {
                return true;
            } else {
                System.err.println("Warning: Project deleted from database but not from blob storage");
                return true; // Return true since DB deletion succeeded
            }
        } catch (Exception e) {
            System.err.println("Error deleting project: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
