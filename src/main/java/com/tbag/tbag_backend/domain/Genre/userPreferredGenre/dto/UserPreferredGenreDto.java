package com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserPreferredGenreDto {

    private String mediaType;
    private Long genreId;
    private String  genreName;

}
