package com.tbag.tbag_backend.domain.Location.dto;

import com.tbag.tbag_backend.common.Trans;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import lombok.Builder;
import lombok.Getter;



@Getter
@Builder
public class ContentLocationDto {
    private Long locationId;
    @Trans
    private String placeName;
    @Trans
    private String businessHours;
    @Trans
    private String locationString;
    private String placeType;
    private Long viewCount;
    private LocationImageDto image;
    private Boolean isInSchedule;
}

