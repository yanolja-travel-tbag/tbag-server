package com.tbag.tbag_backend.domain.Genre;

import com.tbag.tbag_backend.common.Trans;
import lombok.Data;

@Data
public class GenreDto {
    private Long id;
    @Trans
    private String name;

    public static GenreDto toGenreDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getGenreName());
        return dto;
    }
}
