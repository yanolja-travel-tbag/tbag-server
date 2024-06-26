package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.common.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContentSearchDto {
    private Long id;
    private String title;
    private Long viewCount;
    private List<String> genres;
    private List<ActorDto> actors;
    private List<String> contentImages;

    @Getter
    @Builder
    public static class ActorDto {
        private String name;
        private String character;
        private String profilePath;
    }
}