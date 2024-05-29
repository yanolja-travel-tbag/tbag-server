package com.tbag.tbag_backend.domain.Artist.repository;

import com.tbag.tbag_backend.domain.Artist.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
