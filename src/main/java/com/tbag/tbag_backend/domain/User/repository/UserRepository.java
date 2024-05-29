package com.tbag.tbag_backend.domain.User.repository;

import com.tbag.tbag_backend.domain.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
//    Optional<User> findByIdAndActivatedIsTrue(Integer id);
    Optional<User> findOneByIdAndIsActivatedIsTrue(Integer userId);
    Optional<User> findBySocialIdAndIsActivatedIsTrue(Long socialId);
}

