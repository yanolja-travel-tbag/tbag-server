package com.tbag.tbag_backend.domain.User.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.domain.Auth.Enum.SocialType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "social_type")
    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    @Column(name = "social_id")
    private Long socialId;

    @Column(nullable = false)
    private String nickname;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "marketing_agree")
    private Boolean marketingAgree = false;

    @Column(name = "is_activated")
    private Boolean isActivated = true;

    @Column(name = "is_registered")
    private Boolean isRegistered = false;

    @JsonIgnore
    @Column(name = "nick_change")
    private LocalDate nickChange;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;

    @Builder
    public User(String nickname, Long socialId, SocialType socialType) {
        this.nickname = nickname;
        this.socialId = socialId;
        this.socialType = socialType;
    }


    public User updateMarketingAgree(Boolean marketingAgree) {
        this.marketingAgree = marketingAgree;

        return this;
    }

    public User updateLastAccessed(LocalDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;

        return this;
    }

    public User updateNickname(String nickname, LocalDate nickChange) {
        this.nickname = nickname;
        this.nickChange = nickChange;

        return this;
    }

    public User updateActivated(Boolean activated) {
        this.isActivated = activated;

        return this;
    }

    public User updateSocialId(Long socialId) {
        this.socialId = socialId;

        return this;
    }

}
