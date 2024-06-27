package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.common.Translatable;
import com.tbag.tbag_backend.common.TranslatableField;
import com.tbag.tbag_backend.common.TranslationId;
import com.tbag.tbag_backend.domain.Content.contentActor.ContentActor;
import com.tbag.tbag_backend.domain.Content.contentArtist.ContentArtist;
import com.tbag.tbag_backend.domain.Content.contentGenre.ContentGenre;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "content")
@Getter
@Setter
@NoArgsConstructor
public class Content implements Translatable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mediaType;
    private Long viewCount;
    private String title;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    private List<ContentGenre> contentGenres;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    private List<ContentArtist> contentArtists;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    private List<ContentActor> contentActors;

    @OneToOne(mappedBy = "content", fetch = FetchType.LAZY)
    private ContentDetails contentDetails;

    @Override
    public List<TranslatableField> getTranslatableFields() {
        return List.of(new TranslatableField() {
            @Override
            public String getTranslationKey() {
                return "content_title_" + id;
            }

            @Override
            public TranslationId getTranslationId() {
                return new TranslationId(getTranslationKey(), Language.ofLocale());
            }

            @Override
            public void setTranslatedValue(String translatedValue) {
                title = translatedValue;
            }
        });
    }
}

