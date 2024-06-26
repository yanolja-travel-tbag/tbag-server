package com.tbag.tbag_backend.common;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TranslationResolver {
    public static String getTranslationWithFallback(TranslationId translationId, List<Translation> translations) {
        Optional<Translation> translationOptional = translations.stream()
                .filter(translation -> translation.getTranslationId().equals(translationId))
                .findAny();
        if (translationOptional.isEmpty()) {
            for (Locale fallbackLocale : Language.fallbackLocales) {
                TranslationId fallbackId = new TranslationId(translationId.getKey(), Language.ofLocale(fallbackLocale));
                translationOptional = translations.stream()
                        .filter(translation -> translation.getTranslationId().equals(fallbackId))
                        .findAny();
                if (translationOptional.isPresent()) {
                    break;
                }
            }
        }
        return translationOptional.map(Translation::getValue).orElse(null);
    }
}
