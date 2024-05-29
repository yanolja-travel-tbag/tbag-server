package com.tbag.tbag_backend.domain.Artist.userPreferredArtist.repository;

import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.entity.UserPreferredArtist;
import com.tbag.tbag_backend.domain.Artist.userPreferredArtist.entity.UserPreferredArtistId;
import com.tbag.tbag_backend.domain.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPreferredArtistRepository extends JpaRepository<UserPreferredArtist, UserPreferredArtistId> {

    List<UserPreferredArtist> findByUser(User user);

}
