package com.tbag.tbag_backend.domain.travel.util;;

import com.tbag.tbag_backend.common.Language;

import java.util.Locale;

public class DistanceFormatter {

    public static String formatDistance(long distanceInMeters) {
        Locale locale = Language.ofLocale().getLocale();
        if (distanceInMeters >= 1000) {
            return String.format(locale, "%.2f km", distanceInMeters / 1000.0);
        } else {
            return String.format(locale, locale.getLanguage().equals(new Locale("ko").getLanguage()) ? "%d m" : "%d meters", distanceInMeters);
        }
    }

    public static String formatDuration(long durationInSeconds) {
        Locale locale = Language.ofLocale().getLocale();
        if (durationInSeconds >= 3600) {
            return String.format(locale, locale.getLanguage().equals(new Locale("ko").getLanguage()) ? "%.2f 시간" : "%.2f hours", durationInSeconds / 3600.0);
        } else if (durationInSeconds >= 60) {
            return String.format(locale, locale.getLanguage().equals(new Locale("ko").getLanguage()) ? "%d 분" : "%d minutes", durationInSeconds / 60);
        } else {
            return String.format(locale, locale.getLanguage().equals(new Locale("ko").getLanguage()) ? "%d 초" : "%d seconds", durationInSeconds);
        }
    }
}