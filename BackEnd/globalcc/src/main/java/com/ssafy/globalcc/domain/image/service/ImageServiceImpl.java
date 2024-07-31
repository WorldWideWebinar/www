package com.ssafy.globalcc.domain.image.service;

import com.ssafy.globalcc.domain.image.dto.FileDto;
import com.ssafy.globalcc.domain.image.exception.NoSuchFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService{

    @Value("${file.uploads-dir}")
    private String UPLOAD_DIR;

    @Value("${file.uploads-url}")
    private String UPLOAD_URL;

    @Override
    public String uploadImage(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + ext;

        try {
            Path path = Paths.get(UPLOAD_DIR, newFileName);
            if(!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
            String url = UPLOAD_URL + newFileName;
            return url;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileDto getImage(String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(!resource.exists()) throw new NoSuchFileException(fileName);
            MediaType type = MediaType.parseMediaType(Files.probeContentType(filePath));
            return new FileDto(type,resource);
        } catch (IOException e) {
            throw new NoSuchFileException(fileName);
        }
    }
}
