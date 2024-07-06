package com.tbag.tbag_backend.domain.Artist.repository;

import com.tbag.tbag_backend.domain.Artist.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query("SELECT DISTINCT a FROM Artist a LEFT JOIN a.artistMembers am " +
            "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(am.artist) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(am.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Artist> searchArtistsByKeyword(@Param("keyword") String keyword, Pageable pageable);



}
