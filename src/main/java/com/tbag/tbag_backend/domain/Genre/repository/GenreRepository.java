package com.tbag.tbag_backend.domain.Genre.repository;

import com.tbag.tbag_backend.domain.Genre.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
