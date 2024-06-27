package com.tbag.tbag_backend.domain.Location.service;

import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDto;
import com.tbag.tbag_backend.domain.Location.dto.MapContentLocationDto;
import com.tbag.tbag_backend.domain.Location.dto.MarkerLocationDto;
import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImage;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageRepository;
import com.tbag.tbag_backend.domain.Location.projection.MapContentLocationProjection;
import com.tbag.tbag_backend.domain.Location.repository.ContentLocationRepository;
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
public class PublicContentLocationService {

    private final ContentLocationRepository contentLocationRepository;
    private final LocationImageRepository locationImageRepository;

    public List<ContentLocationDto> getTop5ByViewCount(String mediaType) {
        return contentLocationRepository.findTop5ByContentMediaTypeOrderByViewCountDesc(mediaType).stream()
                .map(this::mapToContentLocationDto)
                .collect(Collectors.toList());
    }

    public List<ContentLocationDto> getTop5ByCreatedAt(String mediaType) {
        return contentLocationRepository.findTop5ByContentMediaTypeOrderByCreatedAtDesc(mediaType).stream()
                .map(this::mapToContentLocationDto)
                .collect(Collectors.toList());
    }

    public void incrementViewCount(Long id) {
        ContentLocation contentLocation = contentLocationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Invalid content location ID: " + id));
        contentLocation.updateViewCount();
        contentLocationRepository.save(contentLocation);
    }


    @Cacheable(value = "contentLocations")
    public List<MapContentLocationDto> getContentLocations(String mediaType) {
        List<MapContentLocationProjection> projections = contentLocationRepository.findByMediaType(mediaType);
        return projections.stream().map(this::mapToMapContentLocationDto).collect(Collectors.toList());
    }

    public MarkerLocationDto getContentLocationById(Long id) {
        Optional<ContentLocation> locationOptional = contentLocationRepository.findById(id);
        if (locationOptional.isPresent()) {
            ContentLocation location = locationOptional.get();
            return mapToMarkerLocationDto(location);
        } else {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Location not found");
        }
    }


    private ContentLocationDto mapToContentLocationDto(ContentLocation contentLocation) {
        LocationImage locationImage = locationImageRepository.findFirstByContentLocationOrderByIdAsc(contentLocation);
        LocationImageDto imageDto = null;
        if (locationImage != null) {
            imageDto = mapToLocationImageDto(locationImage);
        }

        return ContentLocationDto.builder()
                .locationId(contentLocation.getId())
                .placeName(contentLocation.getPlaceName())
                .businessHours(contentLocation.getBusinessHours())
                .locationString(contentLocation.getLocationString())
                .placeType(contentLocation.getPlaceType())
                .viewCount(contentLocation.getViewCount())
                .image(imageDto)
                .build();
    }

    private MapContentLocationDto mapToMapContentLocationDto(MapContentLocationProjection projection) {
        return MapContentLocationDto.builder()
                .locationId(projection.getId())
                .contentTitle(projection.getContentTitle())
                .contentMediaType(projection.getContentMediaType())
                .latitude(projection.getLatitude())
                .longitude(projection.getLongitude())
                .build();
    }

    private MarkerLocationDto mapToMarkerLocationDto(ContentLocation location) {
        LocationImage image = locationImageRepository.findFirstByContentLocationOrderByIdAsc(location);

        return MarkerLocationDto.builder()
                .locationId(location.getId())
                .placeName(location.getPlaceName())
                .locationString(location.getLocationString())
                .placeType(location.getPlaceType())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .image(image != null ? mapToLocationImageDto(image) : null)
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
