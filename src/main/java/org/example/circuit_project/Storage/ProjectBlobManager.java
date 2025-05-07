/**
 * This class integrates project data with Azure Blob Storage
 */
package org.example.circuit_project.Storage;

import org.example.circuit_project.Project.Project;
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

        System.out.println("ProjectBlobManager.saveProject called for project ID: " + project.getId());
        System.out.println("Blob reference: " + project.getBlobReference());
        System.out.println("Project data length: " + projectData.length() + " characters");

        try {
            // Create temporary file with project data
            tempFile = File.createTempFile("circuit_", ".json");
            System.out.println("Created temp file: " + tempFile.getAbsolutePath());

            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(projectData);
                writer.flush();
                System.out.println("Wrote data to temp file, size: " + tempFile.length() + " bytes");
            }

            // Upload to blob storage using project ID as name
            String blobName = project.getBlobReference();
            if (blobName == null || blobName.isEmpty()) {
                System.err.println("Error: Blob reference is null or empty");
                return false;
            }

            System.out.println("Attempting to upload to blob storage with name: " + blobName);
            try {
                blobStorage.uploadFile(blobName, tempFile.getAbsolutePath());
                System.out.println("Blob upload completed successfully");
            } catch (Exception e) {
                System.err.println("Error uploading to blob storage: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            // Update project in database
            System.out.println("Attempting to update project in database");
            try {
                success = dbConnection.updateProject(project);
                System.out.println("Database update result: " + success);
            } catch (Exception e) {
                System.err.println("Error updating project in database: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

        } catch (IOException e) {
            System.err.println("Error saving project to blob storage: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error during project save: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up temp file
            if (tempFile != null && tempFile.exists()) {
                System.out.println("Deleting temp file");
                boolean deleted = tempFile.delete();
                System.out.println("Temp file deleted: " + deleted);
            }
        }

        System.out.println("ProjectBlobManager.saveProject returning: " + success);
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

        System.out.println("ProjectBlobManager.loadProject called for project ID: " + project.getId());
        System.out.println("Blob reference: " + project.getBlobReference());

        try {
            // Create temp file for download
            tempFile = File.createTempFile("circuit_download_", ".json");
            System.out.println("Created temp file for download: " + tempFile.getAbsolutePath());

            String blobName = project.getBlobReference();
            if (blobName == null || blobName.isEmpty()) {
                System.err.println("Error: Blob reference is null or empty");
                return null;
            }

            // Download from blob storage
            System.out.println("Attempting to download from blob storage with name: " + blobName);
            try {
                blobStorage.downloadFile(blobName, tempFile.getAbsolutePath());
                System.out.println("Blob download completed successfully");
            } catch (Exception e) {
                System.err.println("Error downloading from blob storage: " + e.getMessage());
                e.printStackTrace();
                return null;
            }

            // Read file contents
            System.out.println("Reading downloaded file");
            projectData = Files.readString(Path.of(tempFile.getAbsolutePath()));
            System.out.println("Read " + (projectData != null ? projectData.length() : 0) + " characters from file");

        } catch (Exception e) {
            System.err.println("Error loading project from blob storage: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up temp file
            if (tempFile != null && tempFile.exists()) {
                System.out.println("Deleting temp file");
                boolean deleted = tempFile.delete();
                System.out.println("Temp file deleted: " + deleted);
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
