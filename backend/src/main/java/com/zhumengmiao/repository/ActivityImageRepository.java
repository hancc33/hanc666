package com.zhumengmiao.repository;

import com.zhumengmiao.entity.ActivityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityImageRepository extends JpaRepository<ActivityImage, Long> {
    List<ActivityImage> findAllByOrderBySortOrderAscCreateTimeDesc();
    List<ActivityImage> findByCategoryOrderBySortOrderAsc(String category);
}
