package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "hashtags")
@Setter
@Getter
@NoArgsConstructor
@DynamicInsert
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private Integer postCount;

    @CreationTimestamp  // Hibernate가 자동으로 생성시간 설정
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp    // Hibernate가 자동으로 업데이트 시간을 수정해줌
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
