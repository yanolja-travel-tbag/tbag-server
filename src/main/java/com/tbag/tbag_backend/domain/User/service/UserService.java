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
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferredGenreRepository userPreferredGenreRepository;
    private final GenreRepository genreRepository;

    @Value("${kakao.admin-key}")
    private String serviceAppAdminKey;

    @Value("${kakao.unlink-url}")
    private String unlinkUrl;

    @Transactional
    public void updateUserRegistrationStatus(Integer userId, List<PreferredGenreRequest> preferredGenres) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND,"User not found"));

        for (PreferredGenreRequest preferredGenre : preferredGenres) {
            for (Long genreId : preferredGenre.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND,"Genre not found"));
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
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND,"User not found"));

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

    @Transactional
    public void deactivateUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND,"User not found"));
        revoke(user);
    }

    @Transactional
    public void revoke(User user) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + serviceAppAdminKey);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", user.getSocialId().toString());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(unlinkUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                user.updateActivated(false);
                userRepository.save(user);
            } else {
                throw new CustomException(ErrorCode.OAUTH_BAD_REQUEST, "cannot disconnect OAuth with id : " + user.getSocialId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.OAUTH_BAD_REQUEST, e.getMessage());
        }
    }
}
