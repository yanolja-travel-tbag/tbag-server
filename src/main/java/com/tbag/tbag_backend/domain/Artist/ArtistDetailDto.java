package com.tbag.tbag_backend.domain.Artist;

import lombok.Data;

import java.util.List;

@Data
public class ArtistDetailDto {
    private Long id;
    private String name;
    private String profileImage;
    private List<ArtistMember> member;

}

