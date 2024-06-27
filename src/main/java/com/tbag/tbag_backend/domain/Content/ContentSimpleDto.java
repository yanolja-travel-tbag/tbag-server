package com.tbag.tbag_backend.domain.Content;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentSimpleDto {
    private Long contentId;
    private String contentTitle;
    private Long contentViewCount;
    private String contentImage;
}