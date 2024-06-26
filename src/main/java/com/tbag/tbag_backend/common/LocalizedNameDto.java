package com.tbag.tbag_backend.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LocalizedNameDto {

    private String eng;
    private String kor;

    public static LocalizedNameDto mapToLocalizedNameDto(String eng, String kor) {
        return LocalizedNameDto.builder()
                .eng(eng)
                .kor(kor)
                .build();
    }

    public static LocalizedNameDto getLocalizedMediaType(String mediaType) {
        String kor;
        switch (mediaType.toLowerCase()) {
            case "drama":
                kor = "드라마";
                break;
            case "artist":
                kor = "아티스트";
                break;
            case "movie":
                kor = "영화";
                break;
            default:
                kor = mediaType;
        }

        return LocalizedNameDto.builder()
                .kor(kor)
                .eng(mediaType)
                .build();
    }

}
