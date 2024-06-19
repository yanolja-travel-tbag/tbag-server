package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.domain.Content.contentGenre.ContentGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentGenreRepository extends JpaRepository<ContentGenre, Long> {

    List<ContentGenre> findByContentId(Long contentId);

}
