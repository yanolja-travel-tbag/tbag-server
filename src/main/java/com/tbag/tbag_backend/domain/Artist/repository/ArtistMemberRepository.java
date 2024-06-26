package com.tbag.tbag_backend.domain.Artist.repository;

import com.tbag.tbag_backend.domain.Artist.ArtistMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtistMemberRepository extends JpaRepository<ArtistMember, Long> {
    @Query("SELECT DISTINCT am FROM ArtistMember am " +
            "WHERE LOWER(am.nameEng) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(am.nameKor) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ArtistMember> searchMembersByKeyword(@Param("keyword") String keyword, Pageable pageable);}
