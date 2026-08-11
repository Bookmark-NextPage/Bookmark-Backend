package com.example.bookmark.global.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Component
public class LocalStorageService implements StorageService {

    @Value("${file.dir:uploads/}")
    private String uploadDir;

    @Override
    public String uploadFromUrl(String sourceImageUrl) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String fileName = UUID.randomUUID() + ".png";
            Path targetLocation = uploadPath.resolve(fileName);

            if (sourceImageUrl.startsWith("data:image")) {
                String base64Data = sourceImageUrl.substring(sourceImageUrl.indexOf(",") + 1);
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                try (InputStream in = new ByteArrayInputStream(imageBytes)) {
                    Files.copy(in, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                URL url = new URL(sourceImageUrl);
                try (InputStream in = url.openStream()) {
                    Files.copy(in, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            return "http://localhost:8080/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("로컬 이미지 저장 실패", e);
        }
    }
}