package com.tbag.tbag_backend.domain.Artist;

import lombok.Data;

@Data
public class ArtistSearchDto {
    private Long id;
    private String name;
    private String profileImage;
    private ArtistMember member;

}

