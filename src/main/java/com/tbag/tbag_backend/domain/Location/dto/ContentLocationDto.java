package com.tbag.tbag_backend.domain.Location.dto;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import lombok.Builder;
import lombok.Getter;



@Getter
@Builder
public class ContentLocationDto {
    private Long id;
    private LocalizedNameDto placeName;
    private LocalizedNameDto businessHours;
    private LocalizedNameDto locationString;
    private String placeType;
    private Long viewCount;
    private LocationImageDto image;
}

