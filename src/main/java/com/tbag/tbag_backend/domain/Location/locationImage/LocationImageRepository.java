package com.tbag.tbag_backend.domain.Location.locationImage;

import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationImageRepository extends JpaRepository<LocationImage, Long> {
    LocationImage findFirstByContentLocationOrderByIdAsc(ContentLocation contentLocation);
}
