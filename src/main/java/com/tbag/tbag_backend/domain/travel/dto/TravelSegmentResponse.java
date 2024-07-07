package com.tbag.tbag_backend.domain.travel.dto;

import com.tbag.tbag_backend.common.Trans;
import lombok.Data;

@Data
public class TravelSegmentResponse {
    private long waypointId;
    private int order;
    private LocationDTO origin;
    private LocationDTO dest;
    private Long distance;
    private Long duration;
    private String distanceString;
    private String durationString;

    @Data
    public static class LocationDTO {
        private Long locationId;
        @Trans
        private String placeName;
        @Trans
        private String contentMediaType;
        private String image;
        private Double latitude;
        private Double longitude;
        @Trans
        private String addresses;
    }
}
