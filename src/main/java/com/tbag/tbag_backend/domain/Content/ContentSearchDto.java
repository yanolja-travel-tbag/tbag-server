package com.tbag.tbag_backend.domain.Content;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContentSearchDto {
    private Long contentId;
    private String title;
    private Long viewCount;
    private List<String> genres;
    private List<MemberDto> members;
    private List<String> contentImages;

    @Getter
    @Builder
    public static class MemberDto {
        private String name;
        private String stageName;
        private String profilePath;
    }
}