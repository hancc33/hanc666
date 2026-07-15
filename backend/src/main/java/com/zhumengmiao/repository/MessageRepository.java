package com.zhumengmiao.repository;

import com.zhumengmiao.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByOrderByCreateTimeDesc();
    Page<Message> findByTitleContainingOrContentContainingOrUsernameContaining(
            String title, String content, String username, Pageable pageable);
}
