package com.tbag.tbag_backend.domain.travel.dto;
import lombok.Data;

import java.util.List;

@Data
public class TravelRouteResponse {
    private List<TravelSegmentResponse> segments;
    private long totalDistance;
    private long totalDuration;
    private String totalDistanceString;
    private String totalDurationString;
}