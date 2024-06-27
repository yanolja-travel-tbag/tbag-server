package com.tbag.tbag_backend.domain.Location.dto;

import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
public class ContentLocationDetailDto {
    private Long locationId;
    private String placeName;
    private String placeDescription;
    private String businessHours;
    private String holiday;
    private String locationString;
    private String placeType;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Long viewCount;
    private LocationImageDto image;
    private String contentTitle;
    private List<String> contentGenres;
    private List<String> contentImages;
}

