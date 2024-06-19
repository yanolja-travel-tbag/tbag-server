package com.tbag.tbag_backend.domain.Location.service;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import com.tbag.tbag_backend.domain.Content.*;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentLocationService {

    private final ContentLocationRepository contentLocationRepository;
    private final LocationImageRepository locationImageRepository;
    private final ContentDetailRepository contentDetailsRepository;
    private final ContentGenreRepository contentGenreRepository;
    private final ContentArtistRepository contentArtistRepository;

    @Value("${tmdb.base-image-url}")
    private String imageBaseUrl;

    public ContentLocationDetailDto getContentLocationById(Long id) {
        Optional<ContentLocation> locationOptional = contentLocationRepository.findById(id);
        if (locationOptional.isPresent()) {
            ContentLocation location = locationOptional.get();
            return mapToContentLocationDetailDto(location);
        } else {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Location not found");
        }
    }


    private ContentLocationDetailDto mapToContentLocationDetailDto(ContentLocation location) {
        LocationImage image = locationImageRepository.findFirstByContentLocationOrderByIdAsc(location);
        Content content = location.getContent();
        ContentDetails contentDetails = contentDetailsRepository.findById(content.getId()).orElse(null);
        List<LocalizedNameDto> contentGenres = new ArrayList<>();
        List<String> contentImages = new ArrayList<>();

        if ("artist".equalsIgnoreCase(content.getMediaType())) {
//            List<ContentArtist> contentArtists = contentArtistRepository.findByContent(content);
//            contentArtists.forEach(contentArtist -> {
//                contentImages.add(contentArtist.getArtist().getImageUrl());
//            });
        } else {
            if (contentDetails != null) {

                contentGenres = contentGenreRepository.findByContentId(content.getId()).stream()
                        .map(genre -> mapToLocalizedNameDto(genre.getGenre().getNameEng(), genre.getGenre().getNameKor()))
                        .collect(Collectors.toList());

                if (contentDetails.getPosterPath() != null) {
                    contentImages.add(imageBaseUrl+contentDetails.getPosterPath());
                }
                if (contentDetails.getBackdropPath() != null) {
                    contentImages.add(imageBaseUrl+contentDetails.getBackdropPath());
                }
            }
        }

        return ContentLocationDetailDto.builder()
                .id(location.getId())
                .placeName(mapToLocalizedNameDto(location.getPlaceNameEng(), location.getPlaceName()))
                .placeDescription(mapToLocalizedNameDto(location.getPlaceDescriptionEng(), location.getPlaceDescription()))
                .businessHours(mapToLocalizedNameDto(location.getBusinessHoursEng(), location.getBusinessHours()))
                .holiday(mapToLocalizedNameDto(location.getHolidayEng(), location.getHoliday()))
                .locationString(mapToLocalizedNameDto(location.getLocationStringEng(), location.getLocationString()))
                .placeType(location.getPlaceType())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .phoneNumber(location.getPhoneNumber())
                .createdAt(location.getCreatedAt())
                .viewCount(location.getViewCount())
                .image(image != null ? mapToLocationImageDto(image) : null)
                .contentTitle(mapToLocalizedNameDto(content.getTitleEng(), content.getTitle()))
                .contentGenres(contentGenres)
                .contentImages(contentImages)
                .build();
    }


    private LocalizedNameDto mapToLocalizedNameDto(String eng, String kor) {
        return LocalizedNameDto.builder()
                .eng(eng)
                .kor(kor)
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

}
