package com.tbag.tbag_backend.domain.Location.dto;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapContentLocationDto {
    private Long id;
    private LocalizedNameDto contentTitle;
    private String type;
    private Double latitude;
    private Double longitude;
}