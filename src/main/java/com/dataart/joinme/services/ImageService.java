package com.dataart.joinme.services;

import com.dataart.joinme.configurations.UploadPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageService {
    private final AwsService awsService;
    private final UploadPaths uploadPaths;

    private final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    public ImageService(AwsService awsService, UploadPaths uploadPaths) {
        this.awsService = awsService;
        this.uploadPaths = uploadPaths;
    }

    public String upload(MultipartFile image) {
        checkIfImageFolderExists();
        String originalFilename = image.getOriginalFilename();
        String newFileName = UUID.randomUUID() + "_" + originalFilename;
        File newFile = new File(uploadPaths.getPath() + "/" + originalFilename);
        saveToAppMemory(image, newFileName, newFile);
        return awsService.uploadToAws(newFileName, newFile);
    }

    private void checkIfImageFolderExists() {
        String path = uploadPaths.getPath();
        File file = new File(path);
        if (!Files.exists(Path.of(path))) {
            file.mkdirs();
            logger.debug("Upload path {} created", path);
        }
    }

    private void saveToAppMemory(MultipartFile image, String originalFileName, File storeDestination) {
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(image.getBytes());
            ImageIO.write(ImageIO.read(inputStream), getFileType(originalFileName), storeDestination);
            logger.debug("Image {} saved to {}", originalFileName, storeDestination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileType(String originalFileName) {
        String result = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        logger.debug("File type is determinated: {}", result);
        return result;
    }
}
