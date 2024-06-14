package com.tbag.tbag_backend.domain.Location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentLocationRepository extends JpaRepository<ContentLocation, Long> {

    List<ContentLocation> findTop5ByContentMediaTypeOrderByViewCountDesc(String mediaType);

    List<ContentLocation> findTop5ByContentMediaTypeOrderByCreatedAtDesc(String mediaType);
}
