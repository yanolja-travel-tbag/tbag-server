package com.tbag.tbag_backend.domain.Location.dto;

import com.tbag.tbag_backend.common.Trans;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
public class ContentLocationDetailDto {
    private Long locationId;
    @Trans
    private String placeName;
    @Trans
    private String placeDescription;
    @Trans
    private String businessHours;
    @Trans
    private String holiday;
    @Trans
    private String locationString;
    private String placeType;
    private String mediaType;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Long viewCount;
    private LocationImageDto image;
    @Trans
    private String contentTitle;
    private List<String> contentGenres;
    private List<String> contentImages;
    private Boolean isInSchedule;

}

