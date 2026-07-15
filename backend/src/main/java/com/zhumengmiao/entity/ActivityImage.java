package com.zhumengmiao.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(length = 50)
    private String category;

    private Integer sortOrder;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (sortOrder == null) sortOrder = 0;
    }
}
