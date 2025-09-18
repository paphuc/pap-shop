package com.pap_shop.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        try {
            System.out.println("Uploading file: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), 
                ObjectUtils.asMap(
                    "folder", "pap-shop/products",
                    "resource_type", "image"
                ));
            
            System.out.println("Upload result: " + uploadResult);
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            System.err.println("Cloudinary upload error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to upload to Cloudinary: " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) throws IOException {
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("Cloudinary delete error: " + e.getMessage());
            throw new IOException("Failed to delete from Cloudinary: " + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        return "pap-shop/products/" + fileName.split("\\.")[0];
    }
}