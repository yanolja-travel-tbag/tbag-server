package com.tbag.tbag_backend.domain.Artist;

import com.tbag.tbag_backend.common.Trans;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ArtistSearchDto {
    private Long contentId;
    @Trans
    private String artistName;
    private String profileImage;
    private ArtistMemberDto member;
    private long viewCount;
    private String mediaType;

    @Data
    @AllArgsConstructor
    public static class ArtistMemberDto {
        private Long id;
        @Trans
        private String name;
        private String profileImage;
    }
}

