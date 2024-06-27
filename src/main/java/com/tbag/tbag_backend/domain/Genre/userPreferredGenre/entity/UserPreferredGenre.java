package com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity;

import com.tbag.tbag_backend.domain.Genre.Genre;
import com.tbag.tbag_backend.domain.User.entity.User;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "user_preferred_genre")
@NoArgsConstructor
@ToString
public class UserPreferredGenre {

    @EmbeddedId
    private UserPreferredGenreId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("genreId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Builder
    public UserPreferredGenre(UserPreferredGenreId id, User user, Genre genre) {
        this.id = id;
        this.user = user;
        this.genre = genre;
    }
}
