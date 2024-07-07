package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.common.Translate;
import com.tbag.tbag_backend.domain.Artist.Artist;
import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.entity.UserPreferredArtist;
import com.tbag.tbag_backend.domain.Content.contentArtist.ContentArtist;
import com.tbag.tbag_backend.domain.Genre.Genre;
import com.tbag.tbag_backend.domain.Genre.repository.GenreRepository;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity.UserPreferredGenre;
import com.tbag.tbag_backend.domain.Location.dto.ContentLocationSearchDto;
import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImage;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageRepository;
import com.tbag.tbag_backend.domain.Location.repository.ContentLocationRepository;
import com.tbag.tbag_backend.domain.User.entity.User;
import com.tbag.tbag_backend.domain.User.repository.UserRepository;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tbag.tbag_backend.domain.Location.service.ContentLocationService.mapToLocationImageDto;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentLocationRepository contentLocationRepository;
    private final ContentDetailRepository contentDetailsRepository;
    private final ContentGenreRepository contentGenreRepository;
    private final GenreRepository genreRepository;
    private final LocationImageRepository locationImageRepository;
    private final ContentArtistRepository contentArtistRepository;
    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;
    @Value("${tmdb.base-image-url}")
    private String imageBaseUrl;

    public Page<ContentSearchDto> searchContent(String keyword, Pageable pageable) {
        Page<Content> contents = contentRepository.findByTitleContainingAndMediaTypeNot(keyword, MediaType.ARTIST, pageable);
        return contents.map(content -> getContentSearchDto(content));
    }

    @Transactional
    public void updateViewCount(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Content not found with id: " + contentId));
        content.setViewCount(content.getViewCount() + 1);
        contentRepository.save(content);
    }

    public Page<ContentSearchDto> getHistoryContents(Pageable pageable, Integer userId, Principal principal) {
        if (userId != Integer.parseInt(principal.getName())){
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST,"토큰 인증 정보와 userId 일치하지 않음");
        }

        String key = "requestedContentIds:"+userId;
        Set<String> contentIds = redisTemplate.boundSetOps(key).members();

        if (contentIds == null || contentIds.isEmpty()) {
            return Page.empty();
        }

        List<Long> idList = contentIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        int total = idList.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);

        if (start > total) {
            return Page.empty();
        }

        List<Long> pagedIdList = idList.subList(start, end);

        List<Content> contents = contentRepository.findAllById(pagedIdList);

        List<ContentSearchDto> contentSearchDtos = contents.stream()
                .map(this::getContentSearchDto)
                .collect(Collectors.toList());

        return new PageImpl<>(contentSearchDtos, pageable, total);
    }

    @Translate
    public ContentDetailedDto getContentById(Long contentId, Integer userId) {
        Content content = contentRepository.findById(contentId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Content not found with id:" + contentId));
        saveContentIdToRedis(contentId, userId);

        return getContentDetailedDto(content);
    }

    private void saveContentIdToRedis(Long contentId, Integer userId) {
        String key = "requestedContentIds:"+userId;
        BoundSetOperations<String, String> setOps = redisTemplate.boundSetOps(key);
        setOps.add(contentId.toString());
    }

    private ContentSearchDto getContentSearchDto(Content content) {

        ContentDetails contentDetails = contentDetailsRepository.findById(content.getId()).orElse(null);

        List<String> contentImages = new ArrayList<>();

        List<ContentSearchDto.MemberDto> members = null;

        if (content.isMediaTypeArtist()) {

            Artist artist = contentArtistRepository.findOneByContentId(content.getId()).getArtist();

            members = artist.getArtistMembers().stream()
                    .map(artistMember -> ContentSearchDto.MemberDto.builder()
                            .name(artistMember.getArtistMemberNameKey())
                            .build())
                    .limit(3)
                    .collect(Collectors.toList());

            contentImages.add(artist.getProfileImage());

        } else {
            members = content.getContentActors().stream()
                    .map(contentActor -> ContentSearchDto.MemberDto.builder()
                            .name(contentActor.getActor().getActorNameKey())
                            .build())
                    .limit(3)
                    .collect(Collectors.toList());


            if (contentDetails.getPosterPath() != null) {
                contentImages.add(imageBaseUrl + contentDetails.getPosterPath());
            }
            if (contentDetails.getBackdropPath() != null) {
                contentImages.add(imageBaseUrl + contentDetails.getBackdropPath());
            }
        }

        return ContentSearchDto.builder()
                .contentId(content.getId())
                .title(content.getContentTitleKey())
                .mediaType(content.getMediaType())
                .viewCount(content.getViewCount())
                .members(members)
                .contentImages(contentImages)
                .build();
    }


    private ContentDetailedDto getContentDetailedDto(Content content) {

        ContentDetails contentDetails = contentDetailsRepository.findById(content.getId()).orElse(null);

        List<String> contentImages = new ArrayList<>();
        List<String> contentGenres = null;

        List<ContentDetailedDto.MemberDto> members = null;

        if (content.isMediaTypeArtist()) {

            Artist artist = contentArtistRepository.findOneByContentId(content.getId()).getArtist();

            members = artist.getArtistMembers().stream()
                    .map(artistMember -> ContentDetailedDto.MemberDto.builder()
                            .name(artistMember.getArtistMemberNameKey())
                            .stageName(artistMember.getArtistMemberNameKey())
                            .profilePath(artistMember.getProfileImage())
                            .build())
                    .collect(Collectors.toList());

            contentImages.add(artist.getProfileImage());

        } else {
            members = content.getContentActors().stream()
                    .map(contentActor -> ContentDetailedDto.MemberDto.builder()
                            .name(contentActor.getActor().getActorNameKey())
                            .stageName(contentActor.getContentActorCharacterKey())
                            .profilePath(imageBaseUrl + contentActor.getActor().getProfilePath())
                            .build())
                    .collect(Collectors.toList());


            contentGenres = getGenresAndImages(content, contentDetails, contentImages, contentGenreRepository, imageBaseUrl);
        }

        return ContentDetailedDto.builder()
                .contentId(content.getId())
                .title(content.getContentTitleKey())
                .mediaType(content.getMediaType())
                .viewCount(content.getViewCount())
                .genres(contentGenres)
                .members(members)
                .contentImages(contentImages)
                .build();
    }

    @NotNull
    public static List<String> getGenresAndImages(Content content, ContentDetails contentDetails, List<String> contentImages, ContentGenreRepository contentGenreRepository, String imageBaseUrl) {
        List<String> contentGenres;
        contentGenres = contentGenreRepository.findByContentId(content.getId()).stream()
                .map(genre -> genre.getGenre().getGenreName())
                .collect(Collectors.toList());

        if (contentDetails.getPosterPath() != null) {
            contentImages.add(imageBaseUrl + contentDetails.getPosterPath());
        }
        if (contentDetails.getBackdropPath() != null) {
            contentImages.add(imageBaseUrl + contentDetails.getBackdropPath());
        }
        return contentGenres;
    }

    public Page<ContentLocationSearchDto> getRelatedLocations(Long id, int page, int size, String sort) {
        Pageable pageable;
        if ("viewCount".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        Page<ContentLocation> locationsPage = contentLocationRepository.findByContentId(id, pageable);

        return locationsPage.map(this::mapToContentLocationSearchDto);
    }

    private ContentLocationSearchDto mapToContentLocationSearchDto(ContentLocation location) {
        return mapToContentLocationSearchDto(location, null);
    }

    private ContentLocationSearchDto mapToContentLocationSearchDto(ContentLocation location, Integer userId) {
        LocationImage image = locationImageRepository.findFirstByContentLocationOrderByIdAsc(location);
        Content content = location.getContent();

        boolean isInSchedule = location.isInSchedule(userId);

        return ContentLocationSearchDto.builder()
                .locationId(location.getId())
                .placeName(location.getContentLocationPlaceNameKey())
                .placeType(location.getPlaceType())
                .mediaType(content.getMediaType())
                .viewCount(location.getViewCount())
                .image(image != null ? mapToLocationImageDto(image) : null)
                .contentTitle(content.getContentTitleKey())
                .isInSchedule(isInSchedule)
                .build();
    }

    public Page<ContentSimpleDto> getFilteredContent(String mediaType, Long genreId, Pageable pageable) {
        Page<Content> contents = null;

        if (mediaType.equals("artist")) {
            contents = contentRepository.findByMediaTypeOrderByViewCountDesc(MediaType.valueOf(mediaType), pageable);
        } else {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "genre not found"));
            contents = contentRepository.findByMediaTypeAndGenreOrderByViewCountDesc(MediaType.valueOf(mediaType.toUpperCase()), genre, pageable);
        }

        return contents.map(content -> getFilteredContentDto(content));
    }

    private ContentSimpleDto getFilteredContentDto(Content content) {

        String mediaType = content.getMediaType(Locale.ENGLISH);

        String image;

        if (mediaType.toLowerCase().equals("artist")) {
            ContentArtist contentArtist = contentArtistRepository.findOneByContentId(content.getId());
            image = contentArtist.getArtist().getProfileImage();
        } else {
            ContentDetails contentDetails = contentDetailsRepository.findById(content.getId()).orElse(null);
            image = imageBaseUrl + contentDetails.getPosterPath();
        }

        return ContentSimpleDto.builder()
                .contentId(content.getId())
                .contentTitle(content.getContentTitleKey())
                .contentViewCount(content.getViewCount())
                .contentImage(image)
                .build();
    }

    public List<ContentSimpleDto> getTop5ByViewCount(String mediaType) {
        List<Content> contents = contentRepository.findTop5ByMediaTypeOrderByViewCountDesc(MediaType.valueOf(mediaType.toUpperCase()));
        return contents.stream()
                .map(content -> getFilteredContentDto(content))
                .collect(Collectors.toList());
    }

    public List<ContentSimpleDto> getRecommendedContents(Principal principal, int limit) {
        Integer userId = Integer.parseInt(principal.getName());
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found"));

        List<Genre> preferredGenres = user.getUserPreferredGenres().stream()
                .map(UserPreferredGenre::getGenre)
                .collect(Collectors.toList());

        List<Artist> preferredArtists = user.getUserPreferredArtists().stream()
                .map(UserPreferredArtist::getArtist)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(0, limit);

        List<Content> recommendedContents = contentRepository.findTopNByGenresOrArtistsOrderByViewCountDesc(preferredGenres, preferredArtists, pageable);

        int remainingCount = limit - recommendedContents.size();
        if (remainingCount > 0) {
            Pageable remainingPageable = PageRequest.of(0, remainingCount);
            List<Content> additionalContents = contentRepository.findAllOrderByViewCountDesc(remainingPageable);
            recommendedContents.addAll(additionalContents);
        }

        return recommendedContents.stream()
                .map(content -> getFilteredContentDto(content))
                .collect(Collectors.toList());
    }

    public List<ContentSimpleDto> getRecommendedContents(int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        List<Content> recommendedContents = contentRepository.findAllOrderByViewCountDesc(pageable);

        return recommendedContents.stream()
                .map(content -> getFilteredContentDto(content))
                .collect(Collectors.toList());
    }

}