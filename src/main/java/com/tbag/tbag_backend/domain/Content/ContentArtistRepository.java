package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.domain.Content.contentArtist.ContentArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentArtistRepository extends JpaRepository<ContentArtist, Long> {
    ContentArtist findOneByContentId(Long contentId);
}
