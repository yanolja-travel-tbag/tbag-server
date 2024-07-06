package com.tbag.tbag_backend.domain.Artist.repository;

import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.domain.Artist.ArtistMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtistMemberRepository extends JpaRepository<ArtistMember, Long> {
    @Query("SELECT DISTINCT am FROM ArtistMember am " +
            "LEFT JOIN Translation t ON t.translationId.key = CONCAT('idol_members_name_', am.id) " +
            "WHERE LOWER(t.value) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ArtistMember> searchMembersByKeyword(@Param("keyword") String keyword,
                                              Pageable pageable);
}