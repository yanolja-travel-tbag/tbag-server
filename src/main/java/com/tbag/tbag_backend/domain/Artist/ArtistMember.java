package com.tbag.tbag_backend.domain.Artist;

import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.common.Translatable;
import com.tbag.tbag_backend.common.TranslatableField;
import com.tbag.tbag_backend.common.TranslationId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "idol_members")
@NoArgsConstructor
public class ArtistMember implements Translatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    private String name;

    @Column(name = "profile_image", nullable = true)
    private String profileImage;

    @Override
    @JsonIgnore
    public List<TranslatableField> getTranslatableFields() {
        List<TranslatableField> fields = new ArrayList<>();
        fields.add(new SimpleTranslatableField(name, "artist_member_name_" + id));
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


