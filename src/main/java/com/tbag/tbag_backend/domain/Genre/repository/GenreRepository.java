package com.tbag.tbag_backend.domain.Genre.repository;

import com.tbag.tbag_backend.domain.Genre.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query("SELECT g FROM Genre g JOIN Translation t ON t.translationId.key = CONCAT('genres_name_', g.id) " +
            "WHERE t.value LIKE %:genreName% ")
    Optional<Genre> findByName(String genreName);
}
