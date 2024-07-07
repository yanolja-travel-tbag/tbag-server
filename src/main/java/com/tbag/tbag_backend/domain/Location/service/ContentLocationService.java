package com.tbag.tbag_backend.domain.Location.service;

import com.tbag.tbag_backend.domain.Content.*;
import com.tbag.tbag_backend.domain.Content.contentArtist.ContentArtist;
import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDetailDto;
import com.tbag.tbag_backend.domain.Location.repository.ContentLocationRepository;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImage;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageRepository;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentLocationService {

    private final ContentLocationRepository contentLocationRepository;
    private final LocationImageRepository locationImageRepository;
    private final ContentDetailRepository contentDetailsRepository;
    private final ContentGenreRepository contentGenreRepository;
    private final ContentArtistRepository contentArtistRepository;
    private final RedisTemplate redisTemplate;

    @Value("${tmdb.base-image-url}")
    private String imageBaseUrl;

    public ContentLocationDetailDto getContentLocationById(Long id, Integer userId) {
        Optional<ContentLocation> locationOptional = contentLocationRepository.findById(id);
        if (locationOptional.isPresent()) {
            ContentLocation location = locationOptional.get();
            saveContentIdToRedis(id, userId);

            return mapToContentLocationDetailDto(location, userId);
        } else {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Location not found");
        }
    }

    private void saveContentIdToRedis(Long locationId, Integer userId) {
        String key = "requestedContentLocationIds:" + userId;
        BoundSetOperations<String, String> setOps = redisTemplate.boundSetOps(key);
        setOps.add(locationId.toString());
    }

    private ContentLocationDetailDto mapToContentLocationDetailDto(ContentLocation location) {
        return mapToContentLocationDetailDto(location, null);
    }

    private ContentLocationDetailDto mapToContentLocationDetailDto(ContentLocation location, Integer userId) {
        LocationImage image = locationImageRepository.findFirstByContentLocationOrderByIdAsc(location);
        Content content = location.getContent();
        ContentDetails contentDetails = contentDetailsRepository.findById(content.getId()).orElse(null);
        List<String> contentGenres = new ArrayList<>();
        List<String> contentImages = new ArrayList<>();

        if (content.isMediaTypeArtist()) {
            ContentArtist contentArtist = contentArtistRepository.findOneByContentId(content.getId());
            if (contentArtist.getArtist().getProfileImage() != null) {
                contentImages.add(contentArtist.getArtist().getProfileImage());
            }
        } else {
            if (contentDetails != null) {
                contentGenres = ContentService.getGenresAndImages(content, contentDetails, contentImages, contentGenreRepository, imageBaseUrl);
            }
        }

        boolean isInSchedule = location.isInSchedule(userId);

        return ContentLocationDetailDto.builder()
                .locationId(location.getId())
                .placeName(location.getContentLocationPlaceNameKey())
                .placeDescription(location.getContentLocationPlaceDescriptionKey())
                .businessHours(location.getContentLocationBusinessHoursKey())
                .holiday(location.getContentLocationHolidayKey())
                .locationString(location.getContentLocationLocationStringKey())
                .placeType(location.getPlaceType())
                .mediaType(content.getMediaType())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .phoneNumber(location.getPhoneNumber())
                .createdAt(location.getCreatedAt())
                .viewCount(location.getViewCount())
                .image(image != null ? mapToLocationImageDto(image) : null)
                .contentTitle(content.getContentTitleKey())
                .contentGenres(contentGenres)
                .contentImages(contentImages)
                .isInSchedule(isInSchedule)
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

    public Page<ContentLocationDetailDto> getRecommendedContentLocations(Long id, int page, int size) {
        ContentLocation currentLocation = contentLocationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Location not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<ContentLocation> locationsPage = contentLocationRepository
                .findRecommendedLocations(currentLocation.getContent().getId(), currentLocation.getPlaceType(), id, pageable);

        return locationsPage.map(this::mapToContentLocationDetailDto);
    }

    public Page<ContentLocationDetailDto> searchContentLocations(String keyword, int page, int size) {
        String trimmedKeyword = keyword.trim();
        Pageable pageable = PageRequest.of(page, size);

        Page<ContentLocation> locations = contentLocationRepository.findByKeyword(trimmedKeyword, pageable);

        return locations.map(this::mapToContentLocationDetailDto);
    }

    public Page<ContentLocationDetailDto> getHistoryContentLocationss(Pageable pageable, Integer userId, Principal principal) {
        if (userId != Integer.parseInt(principal.getName())){
            throw new CustomException(ErrorCode.AUTH_BAD_REQUEST,"토큰 인증 정보와 userId 일치하지 않음");
        }

        String key = "requestedContentLocationIds:"+userId;
        Set<String> contentLocationIds = redisTemplate.boundSetOps(key).members();

        if (contentLocationIds == null || contentLocationIds.isEmpty()) {
            return Page.empty();
        }

        List<Long> idList = contentLocationIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<ContentLocation> contents = contentLocationRepository.findAllById(idList);
        List<ContentLocationDetailDto> contentSearchDtos = contents.stream()
                .map(this::mapToContentLocationDetailDto)
                .collect(Collectors.toList());

        int start = Math.min((int)pageable.getOffset(), contentSearchDtos.size());
        int end = Math.min((start + pageable.getPageSize()), contentSearchDtos.size());

        List<ContentLocationDetailDto> pagedContent = contentSearchDtos.subList(start, end);

        return new PageImpl<>(pagedContent, pageable, contentSearchDtos.size());
    }
}