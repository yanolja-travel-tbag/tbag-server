package com.tbag.tbag_backend.domain.Location;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImage;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageRepository;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentLocationService {

    private final ContentLocationRepository contentLocationRepository;
    private final LocationImageRepository locationImageRepository;

    public List<ContentLocationDto> getTop5ByViewCount(String mediaType) {
        return contentLocationRepository.findTop5ByContentMediaTypeOrderByViewCountDesc(mediaType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ContentLocationDto> getTop5ByCreatedAt(String mediaType) {
        return contentLocationRepository.findTop5ByContentMediaTypeOrderByCreatedAtDesc(mediaType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void incrementViewCount(Long id) {
        ContentLocation contentLocation = contentLocationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Invalid content location ID: " + id));
        contentLocation.updateViewCount();
        contentLocationRepository.save(contentLocation);
    }

    private ContentLocationDto convertToDto(ContentLocation contentLocation) {
        LocationImage locationImage = locationImageRepository.findFirstByContentLocationOrderByIdAsc(contentLocation);
        LocationImageDto imageDto = null;
        if (locationImage != null) {
            imageDto = convertImageToDto(locationImage);
        }

        return ContentLocationDto.builder()
                .id(contentLocation.getId())
                .placeName(LocalizedNameDto.builder()
                        .eng(contentLocation.getPlaceNameEng())
                        .kor(contentLocation.getPlaceName())
                        .build())
                .placeDescription(LocalizedNameDto.builder()
                        .eng(contentLocation.getPlaceDescriptionEng())
                        .kor(contentLocation.getPlaceDescription())
                        .build())
                .businessHours(LocalizedNameDto.builder()
                        .eng(contentLocation.getBusinessHoursEng())
                        .kor(contentLocation.getBusinessHours())
                        .build())
                .holiday(LocalizedNameDto.builder()
                        .eng(contentLocation.getHolidayEng())
                        .kor(contentLocation.getHoliday())
                        .build())
                .locationString(LocalizedNameDto.builder()
                        .eng(contentLocation.getLocationStringEng())
                        .kor(contentLocation.getLocationString())
                        .build())
                .placeType(contentLocation.getPlaceType())
                .latitude(contentLocation.getLatitude())
                .longitude(contentLocation.getLongitude())
                .phoneNumber(contentLocation.getPhoneNumber())
                .createdAt(contentLocation.getCreatedAt())
                .viewCount(contentLocation.getViewCount())
                .image(imageDto)
                .build();
    }

    private LocationImageDto convertImageToDto(LocationImage locationImage) {
        return LocationImageDto.builder()
                .imageUrl(locationImage.getImageUrl())
                .thumbnailUrl(locationImage.getThumbnailUrl())
                .sizeHeight(locationImage.getSizeHeight())
                .sizeWidth(locationImage.getSizeWidth())
                .build();
    }

    @Cacheable(value = "contentLocations")
    public List<MapContentLocationDto> getContentLocations(String mediaType) {
        List<MapContentLocationProjection> projections = contentLocationRepository.findByMediaType(mediaType);
        return projections.stream().map(this::mapToSimpleDto).collect(Collectors.toList());
    }

    private MapContentLocationDto mapToSimpleDto(MapContentLocationProjection projection) {
        return MapContentLocationDto.builder()
                .id(projection.getId())
                .contentTitle(projection.getContentTitle())
                .type(projection.getContentMediaType())
                .latitude(projection.getLatitude())
                .longitude(projection.getLongitude())
                .build();
    }

    public ContentLocationDto getContentLocationById(Long id) {
        Optional<ContentLocation> locationOptional = contentLocationRepository.findById(id);
        if (locationOptional.isPresent()) {
            ContentLocation location = locationOptional.get();
            return mapToContentLocationDto(location);
        } else {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Location not found");
        }
    }

    private ContentLocationDto mapToContentLocationDto(ContentLocation location) {
        LocationImage image = locationImageRepository.findFirstByContentLocationOrderByIdAsc(location);

        return ContentLocationDto.builder()
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
