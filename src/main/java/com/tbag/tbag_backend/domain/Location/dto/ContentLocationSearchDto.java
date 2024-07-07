package com.tbag.tbag_backend.domain.Location.dto;

import com.tbag.tbag_backend.common.Trans;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ContentLocationSearchDto {
    private Long locationId;
    @Trans
    private String placeName;
    private String placeType;
    private String mediaType;
    private Long viewCount;
    private LocationImageDto image;
    @Trans
    private String contentTitle;
    private Boolean isInSchedule;

}

