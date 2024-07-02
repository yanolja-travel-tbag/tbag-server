package com.tbag.tbag_backend.domain.travel.dto;
import lombok.Data;

import java.util.List;

@Data
public class TravelRouteResponse {
    private List<TravelSegmentResponse> segments;
    private int totalDistance;
    private int totalDuration;
}