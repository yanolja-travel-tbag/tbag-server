package com.tbag.tbag_backend.domain.Artist;

import com.tbag.tbag_backend.common.Trans;
import lombok.Data;

@Data
public class ArtistSearchDto {
    private Long contentId;
    @Trans
    private String artistName;
    private String profileImage;
    private ArtistMember member;
    private long viewCount;
    private String mediaType;
}

