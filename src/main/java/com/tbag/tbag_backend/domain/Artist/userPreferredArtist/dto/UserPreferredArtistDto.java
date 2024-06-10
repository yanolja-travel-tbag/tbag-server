package com.tbag.tbag_backend.domain.Artist.userPreferredArtist.dto;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserPreferredArtistDto {

    private Long artistId;
    private LocalizedNameDto artistName;

}
