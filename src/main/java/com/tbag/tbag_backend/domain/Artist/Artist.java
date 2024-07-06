package com.tbag.tbag_backend.domain.Artist;

import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.common.Translatable;
import com.tbag.tbag_backend.common.TranslatableField;
import com.tbag.tbag_backend.common.TranslationId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import java.util.ArrayList;

@Entity
@Getter
@Table(name = "artist")
@NoArgsConstructor
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @Column(name = "profile_image", nullable = true)
    private String profileImage;

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
    private List<ArtistMember> artistMembers;

    public String getArtistNameKey() {
        return "artist_name_" + id;
    }
}
