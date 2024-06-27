package com.tbag.tbag_backend.domain.User.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.entity.UserPreferredArtist;
import com.tbag.tbag_backend.domain.Auth.Enum.SocialType;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity.UserPreferredGenre;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


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
    private String socialId;

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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserPreferredGenre> userPreferredGenres;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserPreferredArtist> userPreferredArtists;

    @Builder
    public User(String nickname, String socialId, SocialType socialType) {
        this.nickname = nickname;
        this.socialId = socialId;
        this.socialType = socialType;
    }

    @Builder
    public User(Integer id, String nickname, LocalDateTime createdAt, Boolean marketingAgree, Boolean isActivated,
                LocalDate nickChange, LocalDateTime lastAccessed, String socialId, String socialType, Boolean isRegistered) {
        this.id = id;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.marketingAgree = marketingAgree;
        this.isActivated = isActivated;
        this.nickChange = nickChange;
        this.lastAccessed = lastAccessed;
        this.socialId = socialId;
        this.socialType = SocialType.valueOf(socialType);
        this.isRegistered = isRegistered;
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

    public User updateSocialId(String socialId) {
        this.socialId = socialId;

        return this;
    }

    public void updateIsRegistered(Boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

}
