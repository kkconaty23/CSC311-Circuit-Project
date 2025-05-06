package org.example.circuit_project.Storage;

import com.azure.storage.blob.*;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.core.util.BinaryData;

import java.io.File;

public class BlobDbOpps {

    // Update these with your actual Azure Storage account information
    private static final String ACCOUNT_NAME = "circuitstorage311";
    private static final String ACCOUNT_KEY = "eKZNSPse4XL8v1ALKoHHLxqq3IKUhvl4WfmCfZL6np9420FLt6RGTWpyqvX/uH4q+2IVmmbzzS00+ASt67rvfw=="; // Replace with your actual key
    private static final String ENDPOINT = "https://" + ACCOUNT_NAME + ".blob.core.windows.net";
    private static final String CONTAINER_NAME = "circuitprojects";

    private final BlobServiceClient blobServiceClient;
    private final BlobContainerClient containerClient;

    public BlobDbOpps() {
        try {
            // Create a SharedKeyCredential
            StorageSharedKeyCredential credential = new StorageSharedKeyCredential(ACCOUNT_NAME, ACCOUNT_KEY);

            // Create a BlobServiceClient
            blobServiceClient = new BlobServiceClientBuilder()
                    .endpoint(ENDPOINT)
                    .credential(credential)
                    .buildClient();

            // Get a reference to the container
            containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);

            // Create container if it doesn't exist
            if (!containerClient.exists()) {
                containerClient.create();
                System.out.println("Created container: " + CONTAINER_NAME);
            }
        } catch (Exception e) {
            System.err.println("Error initializing Azure Blob Storage: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Azure Blob Storage", e);
        }
    }

    public void uploadFile(String blobName, String localFilePath) {
        try {
            // Get a reference to the blob
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            // Check if file exists
            File file = new File(localFilePath);
            if (!file.exists()) {
                throw new RuntimeException("File does not exist: " + localFilePath);
            }

            // Upload the file
            blobClient.uploadFromFile(localFilePath, true); // true = overwrite

            System.out.println("Uploaded to Azure Blob: " + blobName);
        } catch (Exception e) {
            System.err.println("Error uploading to Azure Blob Storage: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload to Azure Blob Storage: " + e.getMessage(), e);
        }
    }

    public void downloadFile(String blobName, String downloadPath) {
        try {
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            // Check if blob exists
            if (!blobClient.exists()) {
                throw new RuntimeException("Blob does not exist in container: " + blobName);
            }

            // Download the blob
            blobClient.downloadToFile(downloadPath, true);

            System.out.println("Downloaded from Azure Blob: " + blobName);
        } catch (Exception e) {
            System.err.println("Error downloading from Azure Blob Storage: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to download from Azure Blob Storage: " + e.getMessage(), e);
        }
    }
    public boolean deleteBlob(String blobName) {
        try {
            // Get a reference to the blob
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            // Check if blob exists
            if (!blobClient.exists()) {
                System.out.println("Blob " + blobName + " does not exist.");
                return false;
            }

            // Delete the blob
            blobClient.delete();
            System.out.println("Deleted from Azure Blob: " + blobName);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting from Azure Blob Storage: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}