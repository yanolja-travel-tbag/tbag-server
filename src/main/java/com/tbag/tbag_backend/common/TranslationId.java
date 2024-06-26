package com.tbag.tbag_backend.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class TranslationId implements Serializable {
    private String key;

//    @Convert(converter = LanguageConverter.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

    public TranslationId(String key, Language language) {
        this.key = key;
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationId that = (TranslationId) o;
        return Objects.equals(key, that.key) && language == that.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, language);
    }
}