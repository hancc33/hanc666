package com.zhumengmiao.controller;

import com.zhumengmiao.dto.ApiResponse;
import com.zhumengmiao.entity.Video;
import com.zhumengmiao.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicVideoController {

    private final VideoRepository videoRepository;

    @PostMapping("/videos")
    public ApiResponse<Video> saveVideo(@RequestBody Video video) {
        return ApiResponse.success(videoRepository.save(video));
    }
}
