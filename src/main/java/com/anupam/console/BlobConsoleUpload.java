package com.anupam.console;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.nimbusds.oauth2.sdk.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class BlobConsoleUpload {
    public static void main(String[] args) {
        System.out.println("Azure Blob Storage upload starting...");

        // Get connection string from environment
        var connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        if (StringUtils.isBlank(connectionString)) {
            System.out.println("Missing AZURE_STORAGE_CONNECTION_STRING");
            return;
        }

        try {
            // Create clients
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("myjavablobcontainer");
            containerClient.createIfNotExists();

            // Path to local data folder
            Path localPath = Paths.get("src/main/resources/data/");

            // Upload all files recursively
            Files.walk(localPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> uploadFile(containerClient, localPath, file));

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void uploadFile(BlobContainerClient containerClient,
                                   Path rootPath,
                                   Path filePath) {
        try {
            // Create relative path for blob name
            String blobName = rootPath.relativize(filePath).toString();
            System.out.println("blobName: " + blobName);
            // Create blob client and upload
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.uploadFromFile(filePath.toString(), true);

            System.out.println("Uploaded: " + blobName);
        } catch (Exception e) {
            System.out.println("Failed to upload " + filePath + ": " + e.getMessage());
        }


    }
}