package com.zhumengmiao.repository;

import com.zhumengmiao.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {
    List<Video> findAllByOrderByPublishTimeDesc();
    List<Video> findByCategoryOrderByPublishTimeDesc(String category);
}
