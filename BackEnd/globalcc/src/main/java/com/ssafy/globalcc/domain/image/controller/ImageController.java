package com.ssafy.globalcc.domain.image.controller;

import com.ssafy.globalcc.aop.ApiResponse;
import com.ssafy.globalcc.domain.image.dto.FileDto;
import com.ssafy.globalcc.domain.image.exception.NoSuchFileException;
import com.ssafy.globalcc.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) {
        String url = imageService.uploadImage(image);
        return new ResponseEntity<>(ApiResponse.success(url,"이미지 업로드 성공"), HttpStatus.OK);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> getImages(@PathVariable String fileName) {
        FileDto file = imageService.getImage(fileName);
        return ResponseEntity.ok().contentType(file.getType()).body(file.getResource());
    }

    @ExceptionHandler(NoSuchFileException.class)
    public ResponseEntity<?> handleException(NoSuchFileException ex) {
        log.error("error at file name : {}",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.fail("해당 이미지가 없습니다.","요청한 이미지 없음"));
    }
}
