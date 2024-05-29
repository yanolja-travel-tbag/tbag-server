package com.tbag.tbag_backend.domain.User.service;

import com.tbag.tbag_backend.domain.Genre.Genre;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto.PreferredGenreRequest;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto.UserPreferredGenreDto;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity.UserPreferredGenre;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity.UserPreferredGenreId;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.repository.GenreRepository;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.repository.UserPreferredGenreRepository;
import com.tbag.tbag_backend.domain.User.dto.UserDto;
import com.tbag.tbag_backend.domain.User.entity.User;
import com.tbag.tbag_backend.domain.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferredGenreRepository userPreferredGenreRepository;
    private final GenreRepository genreRepository;

    @Transactional
    public void updateUserRegistrationStatus(Integer userId, List<PreferredGenreRequest> preferredGenres) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        for (PreferredGenreRequest preferredGenre : preferredGenres) {
            for (Long genreId : preferredGenre.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new IllegalArgumentException("Genre not found"));
                UserPreferredGenreId userPreferredGenreId = new UserPreferredGenreId(userId, preferredGenre.getMediaType(), genreId);
                UserPreferredGenre userPreferredGenre = UserPreferredGenre.builder()
                        .id(userPreferredGenreId)
                        .user(user)
                        .genre(genre)
                        .build();
                userPreferredGenreRepository.save(userPreferredGenre);
            }
        }

        user.updateIsRegistered(true);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserInfo(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserPreferredGenreDto> preferredGenres = userPreferredGenreRepository.findByUser(user).stream()
                .map(upg -> UserPreferredGenreDto.builder()
                        .mediaType(upg.getId().getMediaType())
                        .genreId(upg.getGenre().getId())
                        .genreName(upg.getGenre().getName())
                        .build())
                .collect(Collectors.toList());

        return UserDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .marketingAgree(user.getMarketingAgree())
                .isActivated(user.getIsActivated())
                .nickChange(user.getNickChange())
                .lastAccessed(user.getLastAccessed())
                .socialId(user.getSocialId())
                .socialType(user.getSocialType().name())
                .isRegistered(user.getIsRegistered())
                .preferredGenres(preferredGenres)
                .build();
    }
}
