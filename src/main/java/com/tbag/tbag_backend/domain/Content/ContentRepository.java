package com.tbag.tbag_backend.domain.Content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    Page<Content> findByTitleContainingAndMediaTypeNot(String title, String mediaType, Pageable pageable);
}
