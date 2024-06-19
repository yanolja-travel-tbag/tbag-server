package com.tbag.tbag_backend.domain.Location.repository;

import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import com.tbag.tbag_backend.domain.Location.projection.MapContentLocationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentLocationRepository extends JpaRepository<ContentLocation, Long> {

    List<ContentLocation> findTop5ByContentMediaTypeOrderByViewCountDesc(String mediaType);

    List<ContentLocation> findTop5ByContentMediaTypeOrderByCreatedAtDesc(String mediaType);

    @Query("SELECT cl.id as id, c.title as contentTitle, c.titleEng as contentTitleEng, cl.content.mediaType as contentMediaType, cl.latitude as latitude, cl.longitude as longitude " +
            "FROM ContentLocation cl JOIN cl.content c WHERE (:mediaType = 'all' OR c.mediaType = :mediaType)")
    List<MapContentLocationProjection> findByMediaType(String mediaType);

    @Query("SELECT cl FROM ContentLocation cl")
    List<ContentLocation> findAll();
}
