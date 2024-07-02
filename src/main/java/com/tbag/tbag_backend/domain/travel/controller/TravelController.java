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
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;

    @PostMapping("/request")
    public TravelRequest createTravelRequest(@RequestBody TravelRequestDto travelRequestDto) {
        return travelService.createTravelRequest(
                travelRequestDto.getUserId(),
                travelRequestDto.getName(),
                travelRequestDto.getStartDate(),
                travelRequestDto.getEndDate()
        );
    }
    @PostMapping("/waypoint")
    public void addWaypoint(@RequestBody TravelWaypointDto travelWaypointDto) {
        travelService.addWaypoint(
                travelWaypointDto.getTravelRequestId(),
                travelWaypointDto.getLocationId()
        );
    }

    @GetMapping("/optimize/{travelRequestId}")
    public TravelRouteResponse optimizeRoute(@PathVariable Long travelRequestId) throws IOException, ExecutionException, InterruptedException, ApiException {
        return travelService.optimizeRoute(travelRequestId);
    }
}

