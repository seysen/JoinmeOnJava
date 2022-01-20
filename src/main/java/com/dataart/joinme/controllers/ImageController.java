package com.dataart.joinme.controllers;

import com.dataart.joinme.services.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public String add(@RequestParam MultipartFile image) {
        logger.debug("Request to add image {}", image.getOriginalFilename());
        String uploadUrl = imageService.upload(image);
        logger.debug("Upload image response: {}", uploadUrl);
        return uploadUrl;
    }
}
