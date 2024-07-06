package com.tbag.tbag_backend.domain.travel.repository;

import com.tbag.tbag_backend.domain.travel.entity.TravelWaypoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelWaypointRepository extends JpaRepository<TravelWaypoint, Long> {
    List<TravelWaypoint> findByTravelRequestIdOrderBySequenceAsc(Long travelRequestId);
}
