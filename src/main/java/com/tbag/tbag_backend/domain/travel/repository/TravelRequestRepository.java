package com.tbag.tbag_backend.domain.travel.repository;

import com.tbag.tbag_backend.domain.travel.entity.TravelRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelRequestRepository extends JpaRepository<TravelRequest, Long> {
    List<TravelRequest> findByUserId(Integer userId);
}
