package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.domain.Content.contentArtist.ContentArtist;
import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentArtistRepository extends JpaRepository<ContentArtist, Long> {
    List<ContentArtist> findByContent(Content content);
}
