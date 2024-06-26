package com.tbag.tbag_backend.domain.Artist;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import lombok.Data;

import java.util.List;

@Data
public class ArtistDetailDto {
    private Long id;
    private LocalizedNameDto name;
    private String profileImage;
    private List<ArtistMember> member;

}

