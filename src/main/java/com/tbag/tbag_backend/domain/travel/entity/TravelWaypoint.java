package com.tbag.tbag_backend.domain.travel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import com.tbag.tbag_backend.domain.travel.dto.TravelSegmentResponse;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "travel_waypoint")
public class TravelWaypoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "travel_request_id", nullable = false)
    private TravelRequest travelRequest;
    @ManyToOne
    @JoinColumn(name = "origin_location_id", nullable = false)
    private ContentLocation originLocation;

    @ManyToOne
    @JoinColumn(name = "dest_location_id", nullable = true)
    private ContentLocation destLocation;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "distance", nullable = true)
    private Long distance;

    @Column(name = "duration", nullable = true)
    private Long duration;

    @Builder
    public TravelWaypoint(TravelRequest travelRequest, ContentLocation originLocation, ContentLocation destLocation, Integer sequence,
                          Long distance, Long duration) {
        this.travelRequest = travelRequest;
        this.originLocation = originLocation;
        this.destLocation = destLocation;
        this.sequence = sequence;
        this.distance = distance;
        this.duration = duration;
    }

    public static TravelSegmentResponse toResponse(TravelWaypoint travelWaypoint) {
        if (travelWaypoint == null) {
            return null;
        }

        TravelSegmentResponse response = new TravelSegmentResponse();
        response.setWaypointId(travelWaypoint.getId());
        response.setOrder(travelWaypoint.getSequence());

        TravelSegmentResponse.LocationDTO origin = new TravelSegmentResponse.LocationDTO();
        if (travelWaypoint.getOriginLocation() != null) {
            origin.setLocationId(travelWaypoint.getOriginLocation().getId());
            origin.setPlaceName(travelWaypoint.getOriginLocation().getPlaceName());
            origin.setLatitude(travelWaypoint.getOriginLocation().getLatitude());
            origin.setLongitude(travelWaypoint.getOriginLocation().getLongitude());
            origin.setAddresses(travelWaypoint.getOriginLocation().getLocationString());

            if (travelWaypoint.getOriginLocation().getLocationImages() != null &&
                    !travelWaypoint.getOriginLocation().getLocationImages().isEmpty()) {
                origin.setImage(travelWaypoint.getOriginLocation().getLocationImages().get(0).getImageUrl());
            } else {
                origin.setImage(null);
            }
        }
        response.setOrigin(origin);
        response.setDistance(travelWaypoint.getDistance());
        response.setDuration(travelWaypoint.getDuration());

        return response;
    }

    public static List<TravelSegmentResponse> toResponseList(List<TravelWaypoint> travelWaypoints) {
        if (travelWaypoints == null) {
            return null;
        }

        List<TravelSegmentResponse> responses = new ArrayList<>();
        for (TravelWaypoint travelWaypoint : travelWaypoints) {
            responses.add(toResponse(travelWaypoint));
        }

        return responses;
    }

}

