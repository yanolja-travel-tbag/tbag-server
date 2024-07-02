package com.tbag.tbag_backend.domain.travel.service;

import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import com.tbag.tbag_backend.domain.Location.repository.ContentLocationRepository;
import com.tbag.tbag_backend.domain.User.entity.User;
import com.tbag.tbag_backend.domain.User.repository.UserRepository;
import com.tbag.tbag_backend.domain.travel.component.DistanceMatrix;
import com.tbag.tbag_backend.domain.travel.dto.TravelRouteResponse;
import com.tbag.tbag_backend.domain.travel.dto.TravelSegmentResponse;
import com.tbag.tbag_backend.domain.travel.entity.TravelRequest;
import com.tbag.tbag_backend.domain.travel.entity.TravelWaypoint;
import com.tbag.tbag_backend.domain.travel.repository.TravelRequestRepository;
import com.tbag.tbag_backend.domain.travel.repository.TravelWaypointRepository;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final TravelRequestRepository travelRequestRepository;
    private final TravelWaypointRepository travelWaypointRepository;
    private final UserRepository userRepository;
    private final ContentLocationRepository contentLocationRepository;
    private final DistanceMatrix distanceMatrix;

    @Transactional
    public TravelRequest createTravelRequest(Integer userId, String name, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found"));

        TravelRequest travelRequest = TravelRequest.builder()
                .user(user)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        return travelRequestRepository.save(travelRequest);
    }

    public TravelRouteResponse getTravelRequestById(Long travelRequestId) {
        List<TravelWaypoint> travelWaypoints = travelWaypointRepository.findByTravelRequestId(travelRequestId);
        List<TravelSegmentResponse> segmentResponses = TravelWaypoint.toResponseList(travelWaypoints);

        long totalDistance = segmentResponses.stream()
                .mapToLong(TravelSegmentResponse::getDistance)
                .sum();
        long totalDuration = segmentResponses.stream()
                .mapToLong(TravelSegmentResponse::getDuration)
                .sum();

        TravelRouteResponse travelRouteResponse = new TravelRouteResponse();
        travelRouteResponse.setSegments(segmentResponses);
        travelRouteResponse.setTotalDistance(totalDistance);
        travelRouteResponse.setTotalDuration(totalDuration);

        return travelRouteResponse;
    }

    @Transactional
    public void addWaypoint(Long travelRequestId, Long locationId) {
        TravelRequest travelRequest = travelRequestRepository.findById(travelRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Invalid travel request ID"));
        ContentLocation location = contentLocationRepository.findById(locationId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Location not found"));
        List<TravelWaypoint> travelWaypoints = travelWaypointRepository.findByTravelRequestId(travelRequestId);

        boolean locationExists = travelWaypoints.stream()
                .anyMatch(waypoint -> waypoint.getOriginLocation().getId().equals(locationId));
        if (locationExists) {
            throw new CustomException(ErrorCode.DUPLICATED_DATA, "The location is already present in the travel request.");
        }

        TravelWaypoint newWaypoint = TravelWaypoint.builder()
                .travelRequest(travelRequest)
                .originLocation(location)
                .sequence(travelWaypoints.size() + 1)
                .build();

        travelWaypointRepository.save(newWaypoint);
    }

    public TravelRouteResponse optimizeRoute(Long travelRequestId) throws IOException, ExecutionException, InterruptedException {
        List<TravelWaypoint> waypoints = resetWaypoints(travelRequestId);
        String[] locations = new String[waypoints.size()];
        List<Long> waypointIds = new ArrayList<>();

        for (int i = 0; i < waypoints.size(); i++) {
            ContentLocation location = waypoints.get(i).getOriginLocation();
            locations[i] = location.getLatitude() + "," + location.getLongitude();
            waypointIds.add(waypoints.get(i).getId());
        }

        return distanceMatrix.buildTravelSegments(locations, waypointIds);
    }

    private List<TravelWaypoint> resetWaypoints(Long travelRequestId) {
        List<TravelWaypoint> waypoints = travelWaypointRepository.findByTravelRequestId(travelRequestId);

        waypoints.forEach(waypoint -> {
            waypoint.setSequence(0);
            waypoint.setDestLocation(null);
            waypoint.setDistance(0L);
            waypoint.setDuration(0L);
        });

        travelWaypointRepository.saveAll(waypoints);
        return waypoints;
    }

    public void deleteTravelWaypoint(Long id) {
        travelWaypointRepository.deleteById(id);
    }

    public List<TravelRequest> getTravelRequests(Principal principal) {
        Integer userId = Integer.parseInt(principal.getName());

        return travelRequestRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteTravelRequest(Long id) {
        if (!travelRequestRepository.existsById(id)) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Travel request not found");
        }
        travelRequestRepository.deleteById(id);
    }

}