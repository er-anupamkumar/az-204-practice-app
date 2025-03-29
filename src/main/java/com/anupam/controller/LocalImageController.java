package com.anupam.controller;

import com.anupam.model.Image;
import com.anupam.repository.ImageRepository;
import com.anupam.service.LocalStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class LocalImageController {
    private final LocalStorageService localStorageService;
    private final ImageRepository imageRepo;

    public LocalImageController(LocalStorageService localStorageService, ImageRepository imageRepo) {
        this.localStorageService = localStorageService;
        this.imageRepo = imageRepo;
    }

    @GetMapping("/local-upload")
    public String showUploadForm(Model model) {
        model.addAttribute("image", new Image());
        return "local-upload";
    }

    @PostMapping("/local-upload")
    public String handleUpload(@ModelAttribute Image image,
                               @RequestParam("file") MultipartFile file) {
        String filename = null;
        try {
            filename = localStorageService.store(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        image.setFilePath(filename);
        image.setUploadDate(LocalDateTime.now());
        imageRepo.save(image);
        return "redirect:/local-gallery";
    }

    @GetMapping("/local-gallery")
    public String showGallery(Model model) {
        model.addAttribute("images", imageRepo.findAll());
        return "local-gallery";
    }

    @GetMapping("/local-delete/{id}")
    public String deleteImage(@PathVariable Long id) {
        Image image = imageRepo.findById(id).orElseThrow(() -> new RuntimeException("Image not found"));
        localStorageService.delete(image.getFilePath()); // Delete file from storage
        imageRepo.deleteById(id); // Delete image record from the database
        return "redirect:/local-gallery";
    }

}