package com.tbag.tbag_backend.domain.travel.controller;

import com.google.maps.errors.ApiException;
import com.tbag.tbag_backend.domain.travel.dto.TravelRequestDto;
import com.tbag.tbag_backend.domain.travel.dto.TravelWaypointDto;
import com.tbag.tbag_backend.domain.travel.entity.TravelRequest;
import com.tbag.tbag_backend.domain.travel.service.TravelService;
import com.tbag.tbag_backend.domain.travel.dto.TravelRouteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;

    @GetMapping("")
    public List<TravelRequest> getTravelRequests(Principal principal) {
        return travelService.getTravelRequests(principal);
    }

    @PostMapping("/request")
    public TravelRequest createTravelRequest(@RequestBody TravelRequestDto travelRequestDto) {
        return travelService.createTravelRequest(
                travelRequestDto.getUserId(),
                travelRequestDto.getName(),
                travelRequestDto.getStartDate(),
                travelRequestDto.getEndDate()
        );
    }

    @GetMapping("/request/{id}")
    public TravelRouteResponse getTravelRequestById(@PathVariable Long id) {
        return travelService.getTravelRequestById(id);
    }

    @DeleteMapping("/request/{id}")
    public void deleteTravelRequest(@PathVariable Long id) {
        travelService.deleteTravelRequest(id);
    }

    @PostMapping("/waypoint")
    public void addWaypoint(@RequestBody TravelWaypointDto travelWaypointDto) {
        travelService.addWaypoint(
                travelWaypointDto.getTravelRequestId(),
                travelWaypointDto.getLocationId()
        );
    }

    @DeleteMapping("/waypoint/{id}")
    public void deleteTravelWaypoint(@PathVariable Long id) {
        travelService.deleteTravelWaypoint(id);
    }

    @GetMapping("/optimize/{travelRequestId}")
    public TravelRouteResponse optimizeRoute(@PathVariable Long travelRequestId) throws IOException, ExecutionException, InterruptedException, ApiException {
        return travelService.optimizeRoute(travelRequestId);
    }
}

