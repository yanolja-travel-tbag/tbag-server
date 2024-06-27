package com.tbag.tbag_backend.domain.Location.repository;

import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import com.tbag.tbag_backend.domain.Location.projection.MapContentLocationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentLocationRepository extends JpaRepository<ContentLocation, Long>, JpaSpecificationExecutor<ContentLocation> {

    List<ContentLocation> findTop5ByContentMediaTypeOrderByViewCountDesc(String mediaType);

    List<ContentLocation> findTop5ByContentMediaTypeOrderByCreatedAtDesc(String mediaType);

    @Query("SELECT cl.id as id, c.title as contentTitle, c.title as contentTitleEng, cl.content.mediaType as contentMediaType, cl.latitude as latitude, cl.longitude as longitude " +
            "FROM ContentLocation cl JOIN cl.content c WHERE (:mediaType = 'all' OR c.mediaType = :mediaType)")
    List<MapContentLocationProjection> findByMediaType(String mediaType);

    @Query("SELECT cl FROM ContentLocation cl")
    List<ContentLocation> findAll();


    @Query("SELECT cl FROM ContentLocation cl WHERE cl.content.id = :contentId AND cl.id != :currentId " +
            "ORDER BY (CASE WHEN cl.placeType = :placeType THEN 0 ELSE 1 END), cl.viewCount DESC")
    Page<ContentLocation> findRecommendedLocations(@Param("contentId") Long contentId, @Param("placeType") String placeType, @Param("currentId") Long currentId, Pageable pageable);

    Page<ContentLocation> findByContentId(Long contentId, Pageable pageable);

    @Query("SELECT cl FROM ContentLocation cl WHERE " +
            "LOWER(cl.placeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(cl.locationString) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(cl.placeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(cl.locationString) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(cl.placeType) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ContentLocation> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
