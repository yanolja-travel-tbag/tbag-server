package com.tbag.tbag_backend.domain.Actor;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContentSearchDto {
    private Long id;
    private LocalizedNameDto title;
    private Long viewCount;
    private List<LocalizedNameDto> genres;
    private List<ActorDto> actors;
    private List<String> contentImages;

    @Getter
    @Builder
    public static class ActorDto {
        private LocalizedNameDto name;
        private LocalizedNameDto character;
        private String profilePath;
    }
}