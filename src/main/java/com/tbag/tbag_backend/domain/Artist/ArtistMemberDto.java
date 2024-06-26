package com.tbag.tbag_backend.domain.Artist;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ArtistMemberDto {

    private Long id;
    private LocalizedNameDto name;
    private String profileImage;

    public ArtistMemberDto(Long id, LocalizedNameDto name, String profileImage) {
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
    }
}
