package com.tbag.tbag_backend.domain.Artist.repository;

import com.tbag.tbag_backend.domain.Artist.ArtistMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistMemberRepository extends JpaRepository<ArtistMember, Long> {
}
