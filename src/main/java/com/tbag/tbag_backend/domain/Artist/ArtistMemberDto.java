package com.tbag.tbag_backend.domain.Artist;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ArtistMemberDto {

    private Long id;
    private String name;
    private String profileImage;

    public ArtistMemberDto(Long id, String name, String profileImage) {
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
    }
}
