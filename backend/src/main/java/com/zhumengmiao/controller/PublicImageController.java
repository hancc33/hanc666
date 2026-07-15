package com.zhumengmiao.controller;

import com.zhumengmiao.dto.ApiResponse;
import com.zhumengmiao.entity.ActivityImage;
import com.zhumengmiao.repository.ActivityImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicImageController {

    private final ActivityImageRepository imageRepository;

    @PostMapping("/images")
    public ApiResponse<ActivityImage> saveImage(@RequestBody ActivityImage image) {
        return ApiResponse.success(imageRepository.save(image));
    }
}
