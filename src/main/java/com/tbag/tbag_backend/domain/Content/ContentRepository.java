package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.domain.Artist.Artist;
import com.tbag.tbag_backend.domain.Genre.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {

    @Query("SELECT c FROM Content c JOIN Translation t ON t.translationId.key = CONCAT('content_title_', c.id) " +
            "WHERE t.value LIKE %:title% AND c.mediaType <> :mediaType")
    Page<Content> findByTitleContainingAndMediaTypeNot(@Param("title") String title, @Param("mediaType") MediaType mediaType, Pageable pageable);

    @Query("SELECT c FROM Content c JOIN c.contentGenres cg WHERE c.mediaType = :mediaType AND cg.genre = :genre ORDER BY c.viewCount DESC")
    Page<Content> findByMediaTypeAndGenreOrderByViewCountDesc(
            @Param("mediaType") MediaType mediaType,
            @Param("genre") Genre genre,
            Pageable pageable
    );

    Page<Content> findByMediaTypeOrderByViewCountDesc(MediaType mediaType, Pageable pageable);

    List<Content> findTop5ByMediaTypeOrderByViewCountDesc(MediaType mediaType);

    @Query("SELECT c FROM Content c JOIN c.contentGenres cg JOIN c.contentArtists ca " +
            "WHERE cg.genre IN :genres OR ca.artist IN :artists " +
            "ORDER BY c.viewCount DESC")
    List<Content> findTopNByGenresOrArtistsOrderByViewCountDesc(@Param("genres") List<Genre> genres,
                                                                @Param("artists") List<Artist> artists,
                                                                Pageable pageable);

    @Query("SELECT c FROM Content c " +
            "ORDER BY c.viewCount DESC")
    List<Content> findAllOrderByViewCountDesc(Pageable pageable);
}