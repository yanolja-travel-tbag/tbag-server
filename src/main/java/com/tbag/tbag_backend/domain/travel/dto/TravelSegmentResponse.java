package com.tbag.tbag_backend.domain.travel.dto;

import lombok.Data;

@Data
public class TravelSegmentResponse {
    private long waypointId;
    private int order;
    private LocationDTO origin;
    private long distance;
    private long duration;

    @Data
    public static class LocationDTO {
        private Long locationId;
        private String placeName;
        private String image;
        private Double latitude;
        private Double longitude;
        private String addresses;
    }
}
