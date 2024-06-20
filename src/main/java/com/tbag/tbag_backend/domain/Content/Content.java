package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.domain.Content.contentActor.ContentActor;
import com.tbag.tbag_backend.domain.Content.contentGenre.ContentGenre;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "content")
@NoArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "media_type", nullable = false)
    private String mediaType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "title_eng", nullable = false)
    private String titleEng;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    private List<ContentGenre> contentGenres;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    private List<ContentActor> contentActors;

    @OneToOne(mappedBy = "content", fetch = FetchType.LAZY)
    private ContentDetails contentDetails;
}

