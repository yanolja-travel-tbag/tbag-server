package com.tbag.tbag_backend.domain.Content.contentActor;

import com.tbag.tbag_backend.common.Trans;
import lombok.Data;

@Data
public class ContentActorDto {
    private Long contentId;
    @Trans
    private String title;
    @Trans
    private String character;
    @Trans
    private String actorName;
    private String posterPath;
    private Long viewCount;
    private String mediaType;


    public ContentActorDto(Long contentId, String title, String character, String actorName, String posterPath, Long viewCount, String mediaType) {
        this.contentId = contentId;
        this.title = title;
        this.character = character;
        this.actorName = actorName;
        this.posterPath = posterPath;
        this.viewCount = viewCount;
        this.mediaType = mediaType;
    }

}

