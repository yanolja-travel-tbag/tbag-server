package com.tbag.tbag_backend.domain.Artist;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Table(name = "idol_members")
@NoArgsConstructor
public class ArtistMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Transient
    private LocalizedNameDto name;

    @JsonIgnore
    @Column(name = "name_eng", nullable = true)
    private String nameEng;

    @JsonIgnore
    @Column(name = "name", nullable = false)
    private String nameKor;

    @Column(name = "profile_image", nullable = true)
    private String profileImage;

    @PostLoad
    private void postLoad() {
        this.name = LocalizedNameDto.builder()
                .eng(this.nameEng)
                .kor(this.nameKor)
                .build();
    }
}

