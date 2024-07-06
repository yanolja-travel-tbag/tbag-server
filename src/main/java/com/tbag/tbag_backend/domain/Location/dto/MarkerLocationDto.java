package com.tbag.tbag_backend.domain.Location.dto;

import com.tbag.tbag_backend.common.Trans;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class MarkerLocationDto {
    private Long locationId;
    @Trans
    private String placeName;
    @Trans
    private String locationString;
    private String placeType;
    private Double latitude;
    private Double longitude;
    private LocationImageDto image;
}


