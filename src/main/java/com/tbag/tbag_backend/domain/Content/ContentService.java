package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.common.Translate;
import com.tbag.tbag_backend.common.TranslationService;
import com.tbag.tbag_backend.domain.Artist.Artist;
import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.entity.UserPreferredArtist;
import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.repository.UserPreferredArtistRepository;
import com.tbag.tbag_backend.domain.Content.contentArtist.ContentArtist;
import com.tbag.tbag_backend.domain.Genre.Genre;
import com.tbag.tbag_backend.domain.Genre.repository.GenreRepository;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity.UserPreferredGenre;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.repository.UserPreferredGenreRepository;
import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDetailDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final TranslationService translationService;
    private final UserRepository userRepository;
    private final UserPreferredGenreRepository userPreferredGenreRepository;
    private final UserPreferredArtistRepository userPreferredArtistRepository;
    @Value("${tmdb.base-image-url}")
    private String imageBaseUrl;

    public Page<ContentSearchDto> searchContent(String keyword, Pageable pageable) {
        Page<Content> contents = contentRepository.findByTitleContainingAndMediaTypeNot(keyword, "artist", pageable);

        return contents.map(content -> getContentSearchDto(content));
    }

    @Translate
    public ContentSearchDto getContentById(Long contentId) {
        Content content = contentRepository.findById(contentId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Content not found"));

        return getContentSearchDto(content);
    }

    private ContentSearchDto getContentSearchDto(Content content) {
        content = translationService.getTranslatedEntity(content);

        ContentDetails contentDetails = contentDetailsRepository.findById(content.getId()).orElse(null);

        List<String> contentImages = new ArrayList<>();
        List<String> contentGenres = null;

        List<ContentSearchDto.MemberDto> members = null;

        if (content.getMediaType().equals("artist")) {

            Artist artist = contentArtistRepository.findOneByContentId(content.getId()).getArtist();

            members = artist.getArtistMembers().stream()
                    .map(artistMember -> {
                        artistMember = translationService.getTranslatedEntity(artistMember);
                        return ContentSearchDto.MemberDto.builder()
                                .name(artistMember.getName())
                                .stageName(artistMember.getName())
                                .profilePath(artistMember.getProfileImage())
                                .build();
                    })
                    .collect(Collectors.toList());

            contentImages.add(artist.getProfileImage());

        } else {
            members = content.getContentActors().stream()
                    .map(contentActor -> {
                        contentActor = translationService.getTranslatedEntity(contentActor);
                        return ContentSearchDto.MemberDto.builder()
                                .name(contentActor.getActor().getName())
                                .stageName(contentActor.getCharacter())
                                .profilePath(imageBaseUrl + contentActor.getActor().getProfilePath())
                                .build();
                    })
                    .collect(Collectors.toList());


            contentGenres = getGenresAndImages(content, contentDetails, contentImages, contentGenreRepository, imageBaseUrl);
        }

        content = translationService.getTranslatedEntity(content);


        return ContentSearchDto.builder()
                .contentId(content.getId())
                .title(content.getTitle())
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
                .map(genre -> genre.getGenre().getName())
                .collect(Collectors.toList());

        if (contentDetails.getPosterPath() != null) {
            contentImages.add(imageBaseUrl + contentDetails.getPosterPath());
        }
        if (contentDetails.getBackdropPath() != null) {
            contentImages.add(imageBaseUrl + contentDetails.getBackdropPath());
        }
        return contentGenres;
    }

    public Page<ContentLocationDetailDto> getRelatedLocations(Long id, int page, int size, String sort) {
        Pageable pageable;
        if ("viewCount".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        Page<ContentLocation> locationsPage = contentLocationRepository.findByContentId(id, pageable);

        return locationsPage.map(this::mapToContentLocationDetailDto);
    }

    private ContentLocationDetailDto mapToContentLocationDetailDto(ContentLocation location) {
        LocationImage image = locationImageRepository.findFirstByContentLocationOrderByIdAsc(location);
        Content content = location.getContent();
        ContentDetails contentDetails = contentDetailsRepository.findById(content.getId()).orElse(null);
        List<String> contentGenres = new ArrayList<>();
        List<String> contentImages = new ArrayList<>();

        if ("artist".equalsIgnoreCase(content.getMediaType())) {
            ContentArtist contentArtist = contentArtistRepository.findOneByContentId(content.getId());
            if (contentArtist.getArtist().getProfileImage() != null) {
                contentImages.add(contentArtist.getArtist().getProfileImage());
            }
        } else {
            if (contentDetails != null) {
                contentGenres = contentGenreRepository.findByContentId(content.getId()).stream()
                        .map(genre -> genre.getGenre().getName())
                        .collect(Collectors.toList());

                if (contentDetails.getPosterPath() != null) {
                    contentImages.add(imageBaseUrl + contentDetails.getPosterPath());
                }
                if (contentDetails.getBackdropPath() != null) {
                    contentImages.add(imageBaseUrl + contentDetails.getBackdropPath());
                }
            }
        }

        return ContentLocationDetailDto.builder()
                .locationId(location.getId())
                .placeName(location.getPlaceName())
                .placeDescription(location.getPlaceDescription())
                .businessHours(location.getBusinessHours())
                .holiday(location.getHoliday())
                .locationString(location.getLocationString())
                .placeType(location.getPlaceType())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .phoneNumber(location.getPhoneNumber())
                .createdAt(location.getCreatedAt())
                .viewCount(location.getViewCount())
                .image(image != null ? mapToLocationImageDto(image) : null)
                .contentTitle(content.getTitle())
                .contentGenres(contentGenres)
                .contentImages(contentImages)
                .build();
    }

    private LocationImageDto mapToLocationImageDto(LocationImage image) {
        return LocationImageDto.builder()
                .imageUrl(image.getImageUrl())
                .thumbnailUrl(image.getThumbnailUrl())
                .sizeHeight(image.getSizeHeight())
                .sizeWidth(image.getSizeWidth())
                .build();
    }

    public Page<ContentSimpleDto> getFilteredContent(String mediaType, Long genreId, Pageable pageable) {
        Page<Content> contents = null;

        if (mediaType.equals("artist")) {
            contents = contentRepository.findByMediaTypeOrderByViewCountDesc(mediaType, pageable);
        } else {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "genre not found"));
            contents = contentRepository.findByMediaTypeAndGenreOrderByViewCountDesc(mediaType, genre, pageable);
        }

        return contents.map(content -> getFilteredContentDto(content, mediaType));
    }

    private ContentSimpleDto getFilteredContentDto(Content content, String mediaType) {

        content = translationService.getTranslatedEntity(content);
        String image;

        if (mediaType.equals("artist")) {
            ContentArtist contentArtist = contentArtistRepository.findOneByContentId(content.getId());
            image = contentArtist.getArtist().getProfileImage();
        } else {
            ContentDetails contentDetails = contentDetailsRepository.findById(content.getId()).orElse(null);
            image = imageBaseUrl + contentDetails.getPosterPath();
        }

        return ContentSimpleDto.builder()
                .contentId(content.getId())
                .contentTitle(content.getTitle())
                .contentViewCount(content.getViewCount())
                .contentImage(image)
                .build();
    }

    public List<ContentSimpleDto> getTop5ByViewCount(String mediaType) {
        List<Content> contents = contentRepository.findTop5ByMediaTypeOrderByViewCountDesc(mediaType);
        return contents.stream()
                .map(content -> getFilteredContentDto(content, mediaType))
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
                .map(content -> getFilteredContentDto(content, content.getMediaType()))
                .collect(Collectors.toList());
    }

}