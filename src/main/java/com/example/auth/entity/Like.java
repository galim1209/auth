package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"}))
@Setter
@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    private Long targetId;

    @CreationTimestamp  // Hibernate가 자동으로 생성시간 설정
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum TargetType {
        POST, COMMENT
    }
}


