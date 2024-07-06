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
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    @Translate
    public ContentSearchDto getContentById(Long contentId) {
        Content content = contentRepository.findById(contentId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Content not found with id:" + contentId));

        return getContentSearchDto(content);
    }

    private ContentSearchDto getContentSearchDto(Content content) {

        ContentDetails contentDetails = contentDetailsRepository.findById(content.getId()).orElse(null);

        List<String> contentImages = new ArrayList<>();
        List<String> contentGenres = null;

        List<ContentSearchDto.MemberDto> members = null;

        if (content.getMediaType().equals(MediaType.ARTIST)) {

            Artist artist = contentArtistRepository.findOneByContentId(content.getId()).getArtist();

            members = artist.getArtistMembers().stream()
                    .map(artistMember -> ContentSearchDto.MemberDto.builder()
                            .name(artistMember.getArtistMemberNameKey())
                            .stageName(artistMember.getArtistMemberNameKey())
                            .profilePath(artistMember.getProfileImage())
                            .build())
                    .collect(Collectors.toList());

            contentImages.add(artist.getProfileImage());

        } else {
            members = content.getContentActors().stream()
                    .map(contentActor -> ContentSearchDto.MemberDto.builder()
                            .name(contentActor.getActor().getActorNameKey())
                            .stageName(contentActor.getContentActorCharacterKey())
                            .profilePath(imageBaseUrl + contentActor.getActor().getProfilePath())
                            .build())
                    .collect(Collectors.toList());


            contentGenres = getGenresAndImages(content, contentDetails, contentImages, contentGenreRepository, imageBaseUrl);
        }

        return ContentSearchDto.builder()
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

        if (content.getMediaType().equals(MediaType.ARTIST)) {
            ContentArtist contentArtist = contentArtistRepository.findOneByContentId(content.getId());
            if (contentArtist.getArtist().getProfileImage() != null) {
                contentImages.add(contentArtist.getArtist().getProfileImage());
            }
        } else {
            if (contentDetails != null) {
                contentGenres = contentGenreRepository.findByContentId(content.getId()).stream()
                        .map(genre -> genre.getGenre().getGenreName())
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
                .placeName(location.getContentLocationPlaceNameKey())
                .placeDescription(location.getContentLocationPlaceDescriptionKey())
                .businessHours(location.getContentLocationBusinessHoursKey())
                .holiday(location.getContentLocationHolidayKey())
                .locationString(location.getContentLocationLocationStringKey())
                .placeType(location.getPlaceType())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .phoneNumber(location.getPhoneNumber())
                .createdAt(location.getCreatedAt())
                .viewCount(location.getViewCount())
                .image(image != null ? mapToLocationImageDto(image) : null)
                .contentTitle(content.getContentTitleKey())
                .contentGenres(contentGenres)
                .contentImages(contentImages)
                .mediaType(content.getMediaType())
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
            contents = contentRepository.findByMediaTypeOrderByViewCountDesc(MediaType.valueOf(mediaType), pageable);
        } else {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "genre not found"));
            contents = contentRepository.findByMediaTypeAndGenreOrderByViewCountDesc(MediaType.valueOf(mediaType.toUpperCase()), genre, pageable);
        }

        return contents.map(content -> getFilteredContentDto(content, mediaType));
    }

    private ContentSimpleDto getFilteredContentDto(Content content, String mediaType) {

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
                .map(content -> getFilteredContentDto(content, content.getMediaType(Locale.ENGLISH)))
                .collect(Collectors.toList());
    }

    public List<ContentSimpleDto> getRecommendedContents(int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        List<Content> recommendedContents = contentRepository.findAllOrderByViewCountDesc(pageable);

        return recommendedContents.stream()
                .map(content -> getFilteredContentDto(content, content.getMediaType()))
                .collect(Collectors.toList());
    }

}