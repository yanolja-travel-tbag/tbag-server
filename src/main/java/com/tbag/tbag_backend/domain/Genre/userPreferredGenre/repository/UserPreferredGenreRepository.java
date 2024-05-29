package com.tbag.tbag_backend.domain.Genre.userPreferredGenre.repository;


import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity.UserPreferredGenre;
import com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity.UserPreferredGenreId;
import com.tbag.tbag_backend.domain.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPreferredGenreRepository extends JpaRepository<UserPreferredGenre, UserPreferredGenreId> {

    List<UserPreferredGenre> findByUser(User user);

}