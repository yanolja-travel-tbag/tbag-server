package com.tbag.tbag_backend.domain.travel.dto;

import lombok.Data;

@Data
public class TravelSegmentResponse {
    private int order;
    private LocationDTO origin;
//    private LocationDTO destination;
    private DistanceDTO distance;
    private DurationDTO duration;

    @Data
    public static class LocationDTO {
        private Long locationId;
        private String placeName;
        private String image;
        private Double latitude;
        private Double longitude;
        private String addresses;
    }

    @Data
    public static class DistanceDTO {
        private String text;
        private long value;
    }

    @Data
    public static class DurationDTO {
        private String text;
        private long value;
    }
}
