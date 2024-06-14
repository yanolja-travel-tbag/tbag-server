package com.tbag.tbag_backend.domain.Location;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;


@Getter
@Builder
public class ContentLocationDto {
    private Long id;
    private LocalizedNameDto placeName;
    private LocalizedNameDto placeDescription;
    private LocalizedNameDto businessHours;
    private LocalizedNameDto holiday;
    private LocalizedNameDto locationString;
    private String placeType;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Long viewCount;
    private LocationImageDto image;
}


