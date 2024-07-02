package com.tbag.tbag_backend.domain.travel.repository;

import com.tbag.tbag_backend.domain.travel.entity.TravelRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelRequestRepository extends JpaRepository<TravelRequest, Long> {
}
