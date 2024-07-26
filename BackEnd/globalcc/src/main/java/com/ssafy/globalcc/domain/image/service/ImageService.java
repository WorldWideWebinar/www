package com.ssafy.globalcc.domain.image.service;


import com.ssafy.globalcc.domain.image.dto.FileDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(MultipartFile file);

    FileDto getImage(String filename);

}
