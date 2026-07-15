package com.zhumengmiao.controller;

import com.zhumengmiao.dto.ApiResponse;
import com.zhumengmiao.entity.ActivityImage;
import com.zhumengmiao.repository.ActivityImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageController {

    private final ActivityImageRepository imageRepository;

    @GetMapping("/public/images")
    public ApiResponse<List<ActivityImage>> getImages() {
        return ApiResponse.success(imageRepository.findAllByOrderBySortOrderAscCreateTimeDesc());
    }

    @GetMapping("/admin/images")
    public ApiResponse<List<ActivityImage>> getAllImages() {
        return ApiResponse.success(imageRepository.findAll());
    }

    @PostMapping("/admin/images")
    public ApiResponse<ActivityImage> createImage(@RequestBody ActivityImage image) {
        return ApiResponse.success(imageRepository.save(image));
    }

    @PutMapping("/admin/images/{id}")
    public ApiResponse<ActivityImage> updateImage(@PathVariable Long id, @RequestBody ActivityImage image) {
        ActivityImage existing = imageRepository.findById(id).orElseThrow(() -> new RuntimeException("图片不存在"));
        if (image.getTitle() != null) existing.setTitle(image.getTitle());
        if (image.getDescription() != null) existing.setDescription(image.getDescription());
        if (image.getImageUrl() != null) existing.setImageUrl(image.getImageUrl());
        if (image.getCategory() != null) existing.setCategory(image.getCategory());
        if (image.getSortOrder() != null) existing.setSortOrder(image.getSortOrder());
        return ApiResponse.success(imageRepository.save(existing));
    }

    @DeleteMapping("/admin/images/{id}")
    public ApiResponse<Void> deleteImage(@PathVariable Long id) {
        imageRepository.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
