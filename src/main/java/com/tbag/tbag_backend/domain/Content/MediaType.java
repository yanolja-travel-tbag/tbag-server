package com.tbag.tbag_backend.domain.Content;

import java.util.Locale;
import java.util.Map;

public enum MediaType {
    DRAMA(Map.of(Locale.ENGLISH, "Drama", Locale.KOREAN, "드라마")),
    MOVIE(Map.of(Locale.ENGLISH, "Movie", Locale.KOREAN, "영화")),
    ARTIST(Map.of(Locale.ENGLISH, "Artist", Locale.KOREAN, "아티스트"));

    private final Map<Locale, String> names;

    MediaType(Map<Locale, String> names) {
        this.names = names;
    }

    public String getName(Locale locale) {
        return names.getOrDefault(locale, names.get(Locale.ENGLISH));
    }
}
