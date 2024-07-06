package com.tbag.tbag_backend.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;

    public void translateFields(Translatable entity) {
        for (TranslatableField field : entity.getTranslatableFields()) {
            String key = field.getTranslationKey();
            List<Translation> translations = translationRepository.findByTranslationIdKey(key);
            String translatedValue = TranslationResolver.getTranslationWithFallback(field.getTranslationId(), translations);
            field.setTranslatedValue(translatedValue);
        }
    }

    public <T extends Translatable> T getTranslatedEntity(T entity) {
        translateFields(entity);
        return entity;
    }

    public String translate(String key, Language language) {
        return translationRepository.findByTranslationId(new TranslationId(key, language))
                .map(Translation::getValue)
                .orElse(key);
    }
}
