package com.tbag.tbag_backend.domain.Actor;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import lombok.Data;

@Data
public class ContentActorDTO {
    private Long contentId;
    private LocalizedNameDto title;
    private LocalizedNameDto character;
    private LocalizedNameDto actorName;
    private String posterPath;
    private Long viewCount;
    private LocalizedNameDto mediaType;


    public ContentActorDTO(Long contentId, LocalizedNameDto title, LocalizedNameDto character, LocalizedNameDto actorName, String posterPath, Long viewCount, LocalizedNameDto mediaType) {
        this.contentId = contentId;
        this.title = title;
        this.character = character;
        this.actorName = actorName;
        this.posterPath = posterPath;
        this.viewCount = viewCount;
        this.mediaType = mediaType;
    }

}

