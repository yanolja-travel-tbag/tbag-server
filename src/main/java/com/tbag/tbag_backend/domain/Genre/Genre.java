package com.tbag.tbag_backend.domain.Genre;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.common.Translatable;
import com.tbag.tbag_backend.common.TranslatableField;
import com.tbag.tbag_backend.common.TranslationId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "genres")
@NoArgsConstructor
public class Genre implements Translatable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    @Override
    @JsonIgnore
    public List<TranslatableField> getTranslatableFields() {
        List<TranslatableField> fields = new ArrayList<>();
        fields.add(new SimpleTranslatableField(name, "genre_name_" + id));
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
