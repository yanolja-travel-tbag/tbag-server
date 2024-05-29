package com.tbag.tbag_backend.domain.Artist.userPreferredArtist.dto;

import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.dto.PreferredGenreRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRegistrationRequest {

    private List<PreferredGenreRequest> preferredGenres;
    private List<PreferredArtistRequest> preferredArtists;

}
