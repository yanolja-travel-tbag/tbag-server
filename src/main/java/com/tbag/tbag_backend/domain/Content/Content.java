package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.common.*;
import com.tbag.tbag_backend.domain.Content.contentActor.ContentActor;
import com.tbag.tbag_backend.domain.Content.contentArtist.ContentArtist;
import com.tbag.tbag_backend.domain.Content.contentGenre.ContentGenre;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "content")
@Getter
@Setter
@NoArgsConstructor
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long viewCount;

    private String mediaType;
    private String title;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    private List<ContentGenre> contentGenres;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    private List<ContentArtist> contentArtists;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    private List<ContentActor> contentActors;

    @OneToOne(mappedBy = "content", fetch = FetchType.LAZY)
    private ContentDetails contentDetails;

    public String getContentTitleKey() {
        return "content_title_" + id;
    }

}

