package com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreferredGenresRequest {
    private List<PreferredGenreRequest> preferredGenres;
}
