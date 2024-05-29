package com.tbag.tbag_backend.domain.Artist.userPreferredArtist.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserPreferredArtistId implements Serializable {

    private Integer userId;
    private Long artistId;

    public UserPreferredArtistId(Integer userId, Long artistId) {
        this.userId = userId;
        this.artistId = artistId;
    }
}
