package com.tbag.tbag_backend.domain.User.dto;

import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.dto.UserPreferredArtistDto;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto.UserPreferredGenreDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Builder;

@Getter
@Setter
@Builder
public class UserDto {

    private Integer userId;
    private String nickname;
    private LocalDateTime createdAt;
    private Boolean marketingAgree;
    private Boolean isActivated;
    private LocalDate nickChange;
    private LocalDateTime lastAccessed;
    private String socialId;
    private String socialType;
    private Boolean isRegistered;
    private Map<String, List<UserPreferredGenreDto>> preferredGenres;
    private List<UserPreferredArtistDto> preferredArtists;
}