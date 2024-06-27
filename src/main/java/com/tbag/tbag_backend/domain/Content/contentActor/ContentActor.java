package com.tbag.tbag_backend.domain.Content.contentActor;

import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.common.Translatable;
import com.tbag.tbag_backend.common.TranslatableField;
import com.tbag.tbag_backend.common.TranslationId;
import com.tbag.tbag_backend.domain.Actor.Actor;
import com.tbag.tbag_backend.domain.Content.Content;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "content_actor")
@NoArgsConstructor
public class ContentActor implements Translatable {

    @EmbeddedId
    private ContentActorId id;

    @Column(name = "character")
    private String character;

    @Column(name = "credit_id")
    private String creditId;

    @Column(name = "order")
    private Integer order;

    @MapsId("contentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @MapsId("actorId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private Actor actor;

    @Transient
    private String translatedCharacter;

    @Override
    public List<TranslatableField> getTranslatableFields() {
        return List.of(new TranslatableField() {
            @Override
            public String getTranslationKey() {
                return "content_actor_character_" + id.getContentId() + "_" + id.getActorId();
            }

            @Override
            public TranslationId getTranslationId() {
                return new TranslationId(getTranslationKey(), Language.ofLocale());
            }

            @Override
            public void setTranslatedValue(String translatedValue) {
                translatedCharacter = translatedValue;
            }
        });
    }
}

