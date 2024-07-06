package com.tbag.tbag_backend.domain.Location;
import java.util.Locale;
import java.util.Map;

public enum PlaceType {
    CAFE(Map.of(Locale.ENGLISH, "Cafe", Locale.KOREAN, "카페")),
    PLAYGROUND(Map.of(Locale.ENGLISH, "Playground", Locale.KOREAN, "즐길거리")),
    RESTAURANT(Map.of(Locale.ENGLISH, "Restaurant", Locale.KOREAN, "음식점")),
    STAY(Map.of(Locale.ENGLISH, "Stay", Locale.KOREAN, "숙소")),
    STATION(Map.of(Locale.ENGLISH, "Station", Locale.KOREAN, "기차역")),
    STORE(Map.of(Locale.ENGLISH, "Store", Locale.KOREAN, "상점"));

    private final Map<Locale, String> names;

    PlaceType(Map<Locale, String> names) {
        this.names = names;
    }

    public String getName(Locale locale) {
        return names.getOrDefault(locale, names.get(Locale.ENGLISH));
    }
}
