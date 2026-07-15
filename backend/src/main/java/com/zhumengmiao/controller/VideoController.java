package com.zhumengmiao.controller;

import com.zhumengmiao.dto.ApiResponse;
import com.zhumengmiao.dto.PageResult;
import com.zhumengmiao.entity.Video;
import com.zhumengmiao.entity.VideoScript;
import com.zhumengmiao.repository.VideoRepository;
import com.zhumengmiao.repository.VideoScriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VideoController {

    private final VideoRepository videoRepository;
    private final VideoScriptRepository videoScriptRepository;

    @GetMapping("/public/videos")
    public ApiResponse<List<Video>> getVideos() {
        return ApiResponse.success(videoRepository.findAllByOrderByPublishTimeDesc());
    }

    @GetMapping("/public/videos/{id}")
    public ApiResponse<Video> getVideo(@PathVariable Long id) {
        return ApiResponse.success(videoRepository.findById(id).orElseThrow(() -> new RuntimeException("视频不存在")));
    }

    @GetMapping("/public/videos/{id}/scripts")
    public ApiResponse<List<VideoScript>> getVideoScripts(@PathVariable Long id) {
        return ApiResponse.success(videoScriptRepository.findByVideoId(id));
    }

    @GetMapping("/admin/videos")
    public ApiResponse<PageResult<Video>> getVideosPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {

        Specification<Video> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + keyword + "%"));
            }
            if (category != null && !category.isEmpty()) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Video> videoPage = videoRepository.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishTime")));

        PageResult<Video> result = PageResult.<Video>builder()
                .content(videoPage.getContent())
                .totalElements(videoPage.getTotalElements())
                .totalPages(videoPage.getTotalPages())
                .currentPage(page)
                .pageSize(size)
                .build();

        return ApiResponse.success(result);
    }

    @PostMapping("/admin/videos")
    public ApiResponse<Video> createVideo(@RequestBody Video video) {
        return ApiResponse.success(videoRepository.save(video));
    }

    @PutMapping("/admin/videos/{id}")
    public ApiResponse<Video> updateVideo(@PathVariable Long id, @RequestBody Video video) {
        Video existing = videoRepository.findById(id).orElseThrow(() -> new RuntimeException("视频不存在"));
        if (video.getTitle() != null) existing.setTitle(video.getTitle());
        if (video.getDescription() != null) existing.setDescription(video.getDescription());
        if (video.getVideoUrl() != null) existing.setVideoUrl(video.getVideoUrl());
        if (video.getCoverUrl() != null) existing.setCoverUrl(video.getCoverUrl());
        if (video.getCategory() != null) existing.setCategory(video.getCategory());
        if (video.getPublishTime() != null) existing.setPublishTime(video.getPublishTime());
        return ApiResponse.success(videoRepository.save(existing));
    }

    @DeleteMapping("/admin/videos/{id}")
    public ApiResponse<Void> deleteVideo(@PathVariable Long id) {
        videoScriptRepository.findByVideoId(id).forEach(s -> videoScriptRepository.deleteById(s.getId()));
        videoRepository.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }

    @PostMapping("/admin/videos/{id}/scripts")
    public ApiResponse<VideoScript> createScript(@PathVariable Long id, @RequestBody VideoScript script) {
        Video video = videoRepository.findById(id).orElseThrow(() -> new RuntimeException("视频不存在"));
        script.setVideo(video);
        return ApiResponse.success(videoScriptRepository.save(script));
    }

    @DeleteMapping("/admin/scripts/{id}")
    public ApiResponse<Void> deleteScript(@PathVariable Long id) {
        videoScriptRepository.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
