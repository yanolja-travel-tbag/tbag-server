package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.common.Trans;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentSimpleDto {
    private Long contentId;
    @Trans
    private String contentTitle;
    private Long contentViewCount;
    private String contentImage;
}