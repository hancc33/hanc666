package com.zhumengmiao.repository;

import com.zhumengmiao.entity.VideoScript;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoScriptRepository extends JpaRepository<VideoScript, Long> {
    List<VideoScript> findByVideoId(Long videoId);
}
