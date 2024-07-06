package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.common.Trans;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContentSearchDto {
    private Long contentId;
    @Trans
    private String title;
    private String mediaType;
    private Long viewCount;
    private List<String> genres;
    private List<MemberDto> members;
    private List<String> contentImages;

    @Getter
    @Builder
    public static class MemberDto {
        @Trans
        private String name;
        @Trans
        private String stageName;
        private String profilePath;
    }
}