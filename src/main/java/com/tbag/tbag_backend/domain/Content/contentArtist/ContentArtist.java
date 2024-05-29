package com.tbag.tbag_backend.domain.Content.contentArtist;

import com.tbag.tbag_backend.domain.Artist.Artist;
import com.tbag.tbag_backend.domain.Content.Content;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@Table(name = "content_artist")
@NoArgsConstructor
public class ContentArtist {

    @EmbeddedId
    private ContentArtistId id;

    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @MapsId("contentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

}
