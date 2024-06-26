package com.tbag.tbag_backend.domain.Location.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapContentLocationDto {
    private Long id;
    private String contentTitle;
    private String type;
    private Double latitude;
    private Double longitude;
}