package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.common.Translatable;
import com.tbag.tbag_backend.common.TranslatableField;
import com.tbag.tbag_backend.common.TranslationId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "content_details")
@NoArgsConstructor
public class ContentDetails implements Translatable {

    @Id
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @Column(name = "ref_id", nullable = false)
    private Long refId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "overview")
    private String overview;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "genre_ids", columnDefinition = "JSON")
    private String genreIds;

    @Column(name = "original_language", length = 10)
    private String originalLanguage;

    @Column(name = "popularity")
    private Float popularity;

    @Column(name = "vote_count")
    private Integer voteCount;

    @Column(name = "vote_average")
    private Float voteAverage;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "backdrop_path")
    private String backdropPath;

    @Column(name = "videos")
    private String videos;

    @Override
    public List<TranslatableField> getTranslatableFields() {
        List<TranslatableField> fields = new ArrayList<>();
        fields.add(new SimpleTranslatableField(title, "content_title_" + contentId));
        fields.add(new SimpleTranslatableField(overview, "content_overview_" + contentId));
        return fields;
    }

    private static class SimpleTranslatableField implements TranslatableField {
        private String value;
        private final String key;

        SimpleTranslatableField(String value, String key) {
            this.value = value;
            this.key = key;
        }

        @Override
        public String getTranslationKey() {
            return key;
        }

        @Override
        public TranslationId getTranslationId() {
            return new TranslationId(key, Language.ofLocale());
        }

        @Override
        public void setTranslatedValue(String translatedValue) {
            this.value = translatedValue;
        }

        public String getValue() {
            return value;
        }
    }
}

