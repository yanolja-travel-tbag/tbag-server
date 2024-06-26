package com.tbag.tbag_backend.common;

import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.Locale;

@Getter
public enum Language {
    en(1, Locale.ENGLISH),
    ko(2, Locale.KOREAN);

    private final Integer code;
    private final Locale locale;

    Language(Integer code, Locale locale) {
        this.code = code;
        this.locale = locale;
    }

    public static final Locale[] fallbackLocales = {
            Locale.ENGLISH,
            Locale.KOREAN
    };

    public static Language ofCode(Integer code) {
        return Arrays.stream(Language.values())
                .filter(language -> language.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "no such language code"));
    }

    public static Language ofLocale() {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return ofLocale(currentLocale);
    }

    public static Language ofLocale(Locale locale) {
        return Arrays.stream(Language.values())
                .filter(language -> language.getLocale().getLanguage().equalsIgnoreCase(locale.getLanguage()))
                .findAny()
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "no such locale"));
    }
}
