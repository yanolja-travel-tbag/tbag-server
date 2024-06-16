package com.tbag.tbag_backend.domain.Content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "content_details")
@NoArgsConstructor
public class ContentDetails {

    @Id
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @Column(name = "ref_id", nullable = false)
    private Long refId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "original_title")
    private String originalTitle;

    @Column(name = "overview")
    private String overview;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "genre_ids", columnDefinition = "JSON")
    private String genreIds;

    @Column(name = "original_language", length = 10)
    private String originalLanguage;

    @Column(name = "popularity")
    private Float popularity;

    @Column(name = "vote_count")
    private Integer voteCount;

    @Column(name = "vote_average")
    private Float voteAverage;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "backdrop_path")
    private String backdropPath;

    @Column(name = "videos")
    private String videos;

}
