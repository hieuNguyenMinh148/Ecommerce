package com.ecommerce.library.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class ImageUpload {
    private final String UPLOAD_FOLDER = "E:\\Java_InteliJ\\Ecommerce\\Admin\\src\\main\\resources\\static\\img\\image-product";

    public boolean uploadImage(MultipartFile product_image) {
        boolean isUpload = false;
        try {
            Files.copy(product_image.getInputStream(), Paths.get(UPLOAD_FOLDER + File.separator, product_image.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            isUpload = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isUpload;
    }
}
