package com.tbag.tbag_backend.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "translation", indexes = {
        @Index(name = "idx_translation_key", columnList = "key")})
public class Translation {
    @EmbeddedId
    private TranslationId translationId;

    @Lob
    @Column(nullable = false)
    private String value;

    public Language getLanguage() {
        return translationId.getLanguage();
    }

    public String getKey() {
        return translationId.getKey();
    }
}