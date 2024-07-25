package com.ssafy.globalcc.domain.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FileDto {
    private MediaType type;
    private Resource resource;

}
