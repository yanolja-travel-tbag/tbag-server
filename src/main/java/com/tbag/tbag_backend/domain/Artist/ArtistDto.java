package com.tbag.tbag_backend.domain.Artist;

import com.tbag.tbag_backend.common.Trans;
import lombok.Data;

@Data
public class ArtistDto {
    private Long id;
    @Trans
    private String name;

    public static ArtistDto toArtistDto(Artist artist) {
        ArtistDto dto = new ArtistDto();
        dto.setId(artist.getId());
        dto.setName(artist.getArtistNameKey());
        return dto;
    }
}

