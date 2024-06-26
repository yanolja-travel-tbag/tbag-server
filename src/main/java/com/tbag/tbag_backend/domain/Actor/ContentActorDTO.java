package com.tbag.tbag_backend.domain.Actor;

import lombok.Data;

@Data
public class ContentActorDTO {
    private Long contentId;
    private String title;
    private String character;
    private String actorName;
    private String posterPath;
    private Long viewCount;
    private String mediaType;


    public ContentActorDTO(Long contentId, String title, String character, String actorName, String posterPath, Long viewCount, String mediaType) {
        this.contentId = contentId;
        this.title = title;
        this.character = character;
        this.actorName = actorName;
        this.posterPath = posterPath;
        this.viewCount = viewCount;
        this.mediaType = mediaType;
    }

}

