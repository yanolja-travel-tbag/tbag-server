package com.tbag.tbag_backend.domain.Artist.userPreferredArtist.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PreferredArtistRequest {
    private List<Long> artistIds;
}
