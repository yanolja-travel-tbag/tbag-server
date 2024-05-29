package com.tbag.tbag_backend.domain.Content.contentGenre;

import com.tbag.tbag_backend.domain.Content.ContentDetails;
import com.tbag.tbag_backend.domain.Genre.Genre;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@Table(name = "content_genre")
@NoArgsConstructor
public class ContentGenre {

    @EmbeddedId
    private ContentGenreId id;

    @MapsId("contentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private ContentDetails contentDetails;

    @MapsId("genreId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

}
