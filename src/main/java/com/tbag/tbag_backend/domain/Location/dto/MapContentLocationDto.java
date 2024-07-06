package com.tbag.tbag_backend.domain.Location.dto;

import com.tbag.tbag_backend.common.Trans;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapContentLocationDto {
    private Long locationId;
    private String contentTitle;
    private String contentMediaType;
    private Double latitude;
    private Double longitude;
}