package com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PreferredGenreRequest {

    private String mediaType;
    private List<Long> genreIds;

}
