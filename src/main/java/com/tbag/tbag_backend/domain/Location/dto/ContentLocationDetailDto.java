package com.tbag.tbag_backend.domain.Location.dto;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImageDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
public class ContentLocationDetailDto {
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
    private LocalizedNameDto contentTitle;
    private List<LocalizedNameDto> contentGenres;
    private List<String> contentImages;
}

