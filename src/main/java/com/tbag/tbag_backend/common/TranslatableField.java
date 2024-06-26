package com.tbag.tbag_backend.common;

public interface TranslatableField {
    String getTranslationKey();
    TranslationId getTranslationId();
    void setTranslatedValue(String translatedValue);
}
