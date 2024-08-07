package com.tbag.tbag_backend.domain.User.service;

import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.domain.Artist.Artist;
import com.tbag.tbag_backend.domain.Artist.repository.ArtistRepository;
import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.dto.PreferredArtistRequest;
import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.dto.UserPreferredArtistDto;
import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.entity.UserPreferredArtist;
import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.entity.UserPreferredArtistId;
import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.repository.UserPreferredArtistRepository;
import com.tbag.tbag_backend.domain.Auth.Enum.SocialType;
import com.tbag.tbag_backend.domain.Genre.Genre;
import com.tbag.tbag_backend.domain.Genre.repository.GenreRepository;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto.PreferredGenreRequest;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto.UserPreferredGenreDto;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity.UserPreferredGenre;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity.UserPreferredGenreId;
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

import javax.print.attribute.standard.Media;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferredGenreRepository userPreferredGenreRepository;
    private final UserPreferredArtistRepository userPreferredArtistRepository;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;


    @Value("${kakao.admin-key}")
    private String serviceAppAdminKey;

    @Value("${kakao.unlink-url}")
    private String unlinkUrl;

    @Transactional
    public void updateUserRegistrationStatus(Integer userId, List<PreferredGenreRequest> preferredGenres, List<PreferredArtistRequest> preferredArtists, Principal principal) {
        if (userId != Integer.parseInt(principal.getName())){
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST,"토큰 인증 정보와 userId 일치하지 않음");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND,"User not found"));

        if (user.getIsRegistered()){
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST,"이미 회원가입한 유저입니다.");
        }

        savePreferredGenre(userId, preferredGenres, user);

        for (PreferredArtistRequest preferredArtist : preferredArtists) {
            for (Long artistId : preferredArtist.getArtistIds()) {
                Artist artist = artistRepository.findById(artistId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND,"Artist not found"));
                UserPreferredArtistId userPreferredArtistId = new UserPreferredArtistId(userId, artistId);
                UserPreferredArtist userPreferredArtist = UserPreferredArtist.builder()
                        .id(userPreferredArtistId)
                        .user(user)
                        .artist(artist)
                        .build();
                userPreferredArtistRepository.save(userPreferredArtist);
            }
        }

        user.updateIsRegistered(true);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserInfo(Principal principal) {
        Integer userId = Integer.parseInt(principal.getName());
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND,"User not found"));

        Map<String, List<UserPreferredGenreDto>> preferredGenres = userPreferredGenreRepository.findByUser(user).stream()
                .map(upg -> UserPreferredGenreDto.builder()
                        .mediaType(com.tbag.tbag_backend.domain.Content.MediaType.valueOf(upg.getId().getMediaType().toUpperCase()).getName(Locale.US).toLowerCase())
                        .genreId(upg.getGenre().getId())
                        .genreName(upg.getGenre().getGenreName())
                        .build())
                .collect(Collectors.groupingBy(UserPreferredGenreDto::getMediaType));

        List<UserPreferredArtistDto> preferredArtists = userPreferredArtistRepository.findByUser(user).stream()
                .map(upa -> UserPreferredArtistDto.builder()
                        .artistId(upa.getArtist().getId())
                        .artistName(upa.getArtist().getArtistNameKey())
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
                .preferredArtists(preferredArtists)
                .build();
    }


    @Transactional
    public void deactivateUser(Integer userId, Principal principal) {
        if (userId != Integer.parseInt(principal.getName())){
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST,"토큰 인증 정보와 userId 일치하지 않음");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND,"User not found"));
        revoke(user);
    }

    @Transactional
    public void revoke(User user) {

        if (user.getSocialType().equals(SocialType.KAKAO)){
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
        else {
            // TODO : Google unlink 추가
            user.updateActivated(false);
            userRepository.save(user);
        }
    }

    public void updateNickname(Integer userId, String newNickname, Principal principal) {
        if (userId != Integer.parseInt(principal.getName())){
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST,"토큰 인증 정보와 userId 일치하지 않음");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found"));

        user.updateNickname(newNickname, LocalDate.now());
        userRepository.save(user);
    }

    @Transactional
    public void updatePreferredGenres(Integer userId, List<PreferredGenreRequest> newPreferredGenres, Principal principal) {
        if (userId != Integer.parseInt(principal.getName())){
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST,"토큰 인증 정보와 userId 일치하지 않음");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found"));

        userPreferredGenreRepository.deleteByUser(user);

        savePreferredGenre(userId, newPreferredGenres, user);
    }

    @Transactional
    void savePreferredGenre(Integer userId, List<PreferredGenreRequest> newPreferredGenres, User user) {
        for (PreferredGenreRequest preferredGenre : newPreferredGenres) {
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
    }
}
