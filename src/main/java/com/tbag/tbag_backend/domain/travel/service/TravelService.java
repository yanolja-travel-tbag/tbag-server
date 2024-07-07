package com.tbag.tbag_backend.domain.travel.service;

import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import com.tbag.tbag_backend.domain.Location.repository.ContentLocationRepository;
import com.tbag.tbag_backend.domain.User.entity.User;
import com.tbag.tbag_backend.domain.User.repository.UserRepository;
import com.tbag.tbag_backend.domain.travel.component.DistanceMatrix;
import com.tbag.tbag_backend.domain.travel.dto.TravelRequestDto;
import com.tbag.tbag_backend.domain.travel.dto.TravelRouteResponse;
import com.tbag.tbag_backend.domain.travel.dto.TravelSegmentResponse;
import com.tbag.tbag_backend.domain.travel.dto.TravelWaypointDto;
import com.tbag.tbag_backend.domain.travel.entity.TravelRequest;
import com.tbag.tbag_backend.domain.travel.entity.TravelWaypoint;
import com.tbag.tbag_backend.domain.travel.repository.TravelRequestRepository;
import com.tbag.tbag_backend.domain.travel.repository.TravelWaypointRepository;
import com.tbag.tbag_backend.domain.travel.util.DistanceFormatter;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final TravelRequestRepository travelRequestRepository;
    private final TravelWaypointRepository travelWaypointRepository;
    private final UserRepository userRepository;
    private final ContentLocationRepository contentLocationRepository;
    private final DistanceMatrix distanceMatrix;

    @Transactional
    public TravelRequest createTravelRequest(TravelRequestDto travelRequestDto) {
        User user = findUserById(travelRequestDto.getUserId());

        TravelRequest travelRequest = TravelRequest.builder()
                .user(user)
                .name(travelRequestDto.getName())
                .startDate(travelRequestDto.getStartDate())
                .endDate(travelRequestDto.getEndDate())
                .build();
        return travelRequestRepository.save(travelRequest);
    }

    public TravelRouteResponse getTravelRequestById(Long travelRequestId) throws IOException, ExecutionException, InterruptedException {
        List<TravelWaypoint> travelWaypoints = travelWaypointRepository.findByTravelRequestIdOrderBySequenceAsc(travelRequestId);
        CompletableFuture<List<TravelSegmentResponse>> futureResponses = TravelWaypoint.toResponseList(travelWaypoints, distanceMatrix);

        List<TravelSegmentResponse> segmentResponses = futureResponses.get();

        long totalDistance = calculateTotalDistance(segmentResponses);
        long totalDuration = calculateTotalDuration(segmentResponses);

        return buildTravelRouteResponse(segmentResponses, totalDistance, totalDuration);
    }

    @Transactional
    public void addWaypoint(TravelWaypointDto travelWaypointDto) {
        TravelRequest travelRequest = findTravelRequestById(travelWaypointDto.getTravelRequestId());
        ContentLocation location = findContentLocationById(travelWaypointDto.getLocationId());
        List<TravelWaypoint> travelWaypoints = travelWaypointRepository.findByTravelRequestIdOrderBySequenceAsc(travelWaypointDto.getTravelRequestId());

        validateWaypoint(travelWaypoints, travelWaypointDto.getLocationId());

        TravelWaypoint lastWaypoint = travelWaypoints.stream().max(Comparator.comparing(TravelWaypoint::getSequence)).orElse(null);

        if (lastWaypoint != null) {
            lastWaypoint.setDestLocation(location);
            travelWaypointRepository.save(lastWaypoint);
        }

        TravelWaypoint newWaypoint = TravelWaypoint.builder()
                .travelRequest(travelRequest)
                .originLocation(location)
                .sequence(travelWaypoints.size() + 1)
                .build();

        travelWaypointRepository.save(newWaypoint);
    }

    public void optimizeRoute(Long travelRequestId) throws IOException, ExecutionException, InterruptedException {
        List<TravelWaypoint> waypoints = resetWaypoints(travelRequestId);
        String[] locations = extractLocations(waypoints);
        List<Long> waypointIds = extractWaypointIds(waypoints);

        distanceMatrix.buildTravelSegments(locations, waypointIds);
    }

    private List<TravelWaypoint> resetWaypoints(Long travelRequestId) {
        List<TravelWaypoint> waypoints = travelWaypointRepository.findByTravelRequestIdOrderBySequenceAsc(travelRequestId);

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

    private User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found"));
    }

    private TravelRequest findTravelRequestById(Long travelRequestId) {
        return travelRequestRepository.findById(travelRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Invalid travel request ID"));
    }

    private ContentLocation findContentLocationById(Long locationId) {
        return contentLocationRepository.findById(locationId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Location not found"));
    }

    private void validateWaypoint(List<TravelWaypoint> travelWaypoints, Long locationId) {
        if (travelWaypoints.stream().anyMatch(waypoint -> waypoint.getOriginLocation().getId().equals(locationId))) {
            throw new CustomException(ErrorCode.DUPLICATED_DATA, "The location is already present in the travel request.");
        }
    }

    private long calculateTotalDistance(List<TravelSegmentResponse> segmentResponses) {
        return segmentResponses.stream()
                .mapToLong(segment -> Optional.ofNullable(segment.getDistance()).orElse(0L))
                .sum();
    }

    private long calculateTotalDuration(List<TravelSegmentResponse> segmentResponses) {
        return segmentResponses.stream()
                .mapToLong(segment -> Optional.ofNullable(segment.getDuration()).orElse(0L))
                .sum();
    }

    private TravelRouteResponse buildTravelRouteResponse(List<TravelSegmentResponse> segmentResponses, long totalDistance, long totalDuration) {
        TravelRouteResponse travelRouteResponse = new TravelRouteResponse();
        travelRouteResponse.setSegments(segmentResponses);
        travelRouteResponse.setTotalDistance(totalDistance);
        travelRouteResponse.setTotalDuration(totalDuration);
        travelRouteResponse.setTotalDistanceString(DistanceFormatter.formatDistance(totalDistance));
        travelRouteResponse.setTotalDurationString(DistanceFormatter.formatDuration(totalDuration));

        return travelRouteResponse;
    }

    private String[] extractLocations(List<TravelWaypoint> waypoints) {
        return waypoints.stream()
                .map(waypoint -> waypoint.getOriginLocation().getLatitude() + "," + waypoint.getOriginLocation().getLongitude())
                .toArray(String[]::new);
    }

    private List<Long> extractWaypointIds(List<TravelWaypoint> waypoints) {
        return waypoints.stream()
                .map(TravelWaypoint::getId)
                .collect(Collectors.toList());
    }
}