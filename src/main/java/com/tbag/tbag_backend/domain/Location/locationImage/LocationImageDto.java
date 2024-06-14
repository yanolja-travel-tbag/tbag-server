package com.tbag.tbag_backend.domain.Location.locationImage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LocationImageDto {
    private String imageUrl;
    private String thumbnailUrl;
    private Integer sizeHeight;
    private Integer sizeWidth;
}