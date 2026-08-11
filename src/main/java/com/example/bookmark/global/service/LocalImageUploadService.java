package com.example.bookmark.global.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class LocalImageUploadService {

    // yml에 file.dir 설정을 못 읽더라도 uploads/ 폴더를 기본값으로 사용 (Placeholder 에러 방지)
    @Value("${file.dir:uploads/}")
    private String uploadDir;

    /**
     * FE에서 넘어온 MultipartFile 리스트를 로컬 디렉터리에 저장하고 URL 리스트로 반환
     */
    public List<String> uploadImages(List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();

        // 업로드 폴더가 없으면 자동 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            // 파일명 중복 방지를 위한 UUID 생성
            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String savedFilename = UUID.randomUUID().toString() + extension;

            Path targetPath = Paths.get(uploadDir).resolve(savedFilename).toAbsolutePath();

            try {
                file.transferTo(targetPath.toFile());
                // WebConfig의 addResourceHandlers 경로와 매핑되는 접근 URL 생성
                String accessUrl = "http://localhost:8080/uploads/" + savedFilename;
                imageUrls.add(accessUrl);
            } catch (IOException e) {
                log.error("로컬 파일 저장 실패: {}", e.getMessage(), e);
                throw new RuntimeException("이미지 파일 저장 중 오류가 발생했습니다.", e);
            }
        }

        return imageUrls;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".png";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}