package com.tbag.tbag_backend.domain.Content.contentActor;

import com.tbag.tbag_backend.domain.Actor.entity.Actor;
import com.tbag.tbag_backend.domain.Content.Content;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@Table(name = "content_actor")
@NoArgsConstructor
public class ContentActor {

    @EmbeddedId
    private ContentActorId id;

    @MapsId("contentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @MapsId("actorId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private Actor actor;

    public String getContentActorCharacterKey() {
        return "content_actor_character_" + id.getContentId()+ "_" + id.getActorId();
    }

}

