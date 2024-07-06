package com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto;

import com.tbag.tbag_backend.common.Trans;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserPreferredGenreDto {

    private String mediaType;
    private Long genreId;
    @Trans
    private String genreName;

}
