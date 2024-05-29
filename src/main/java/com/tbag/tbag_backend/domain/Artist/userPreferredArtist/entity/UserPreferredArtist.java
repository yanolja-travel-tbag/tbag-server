package com.tbag.tbag_backend.domain.Artist.userPreferredArtist.entity;

import com.tbag.tbag_backend.domain.Artist.Artist;
import com.tbag.tbag_backend.domain.User.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "user_preferred_artist")
@NoArgsConstructor
public class UserPreferredArtist {

    @EmbeddedId
    private UserPreferredArtistId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Builder
    public UserPreferredArtist(UserPreferredArtistId id, User user, Artist artist) {
        this.id = id;
        this.user = user;
        this.artist = artist;
    }
}
