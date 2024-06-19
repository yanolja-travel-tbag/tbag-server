package com.tbag.tbag_backend.domain.Content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContentDetailRepository extends JpaRepository<ContentDetails, Long> {


}
