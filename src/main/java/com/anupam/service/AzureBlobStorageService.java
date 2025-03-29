package com.anupam.service;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AzureBlobStorageService {

    @Autowired
    private BlobContainerClient blobContainerClient;

    public String uploadFile(String blobName, MultipartFile file) throws IOException {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);
        return blobClient.getBlobUrl(); // Return the name of the uploaded blob
    }

    public void deleteFile(String blobName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        blobClient.delete();
    }

    public List<String> getAllImagesUrlBlobs() {
        List<String> urls = new ArrayList<>();
        blobContainerClient.listBlobs().forEach(blobItem -> {
            BlobClient blobClient = blobContainerClient.getBlobClient(blobItem.getName());
            urls.add(blobClient.getBlobUrl());
        });
        return urls;
    }
}

