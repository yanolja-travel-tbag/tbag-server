package com.tbag.tbag_backend.domain.Artist;

import lombok.Data;

@Data
public class ArtistSearchDto {
    private Long contentId;
    private String artistName;
    private String profileImage;
    private ArtistMember member;
    private long viewCount;
    private String mediaType;

}

