package com.tbag.tbag_backend.domain.Artist;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import lombok.Data;

@Data
public class ArtistSearchDto {
    private Long id;
    private LocalizedNameDto name;
    private String profileImage;
    private ArtistMember member;

}

