package com.tbag.tbag_backend.domain.Artist;

import com.tbag.tbag_backend.common.LocalizedNameDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Getter
@Table(name = "artist")
@NoArgsConstructor
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Transient
    private LocalizedNameDto name;

    @JsonIgnore
    @Column(name = "name_eng", nullable = false)
    private String nameEng;

    @JsonIgnore
    @Column(name = "name_kor", nullable = false)
    private String nameKor;

    @JsonIgnore
    @Column(name = "image", nullable = true)
    private String image;

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
    private List<ArtistMember> artistMembers;

    @PostLoad
    private void postLoad() {
        this.name = LocalizedNameDto.builder()
                .eng(this.nameEng)
                .kor(this.nameKor)
                .build();
    }
}
