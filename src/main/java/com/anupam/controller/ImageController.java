package com.anupam.controller;

import com.anupam.model.Image;
import com.anupam.repository.ImageRepository;
import com.anupam.service.AzureBlobStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class ImageController {

    @Autowired
    private AzureBlobStorageService azureBlobStorageService;

    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        model.addAttribute("image", new Image());
        return "upload";
    }

    @PostMapping("/upload")
    public String handleUpload(@ModelAttribute Image image, @RequestParam("file") MultipartFile file) throws IOException {
        String imageName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = azureBlobStorageService.uploadFile(imageName, file);
        image.setFilePath(filePath);
        image.setName(imageName);// Save the blob name in the database
        image.setUploadDate(LocalDateTime.now());
        imageRepository.save(image);
        return "redirect:/gallery";
    }

    @GetMapping("/delete/{id}")
    public String deleteImage(@PathVariable Long id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new RuntimeException("Image not found"));
        azureBlobStorageService.deleteFile(image.getName()); // Delete from Azure Blob Storage
        imageRepository.deleteById(id); // Delete from database
        return "redirect:/gallery";
    }

    @GetMapping("/gallery")
    public String showGallery(Model model) {
        model.addAttribute("images", imageRepository.findAll());
        return "gallery";
    }
}
