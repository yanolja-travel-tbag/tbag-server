package com.tbag.tbag_backend.domain.travel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import lombok.*;

import javax.persistence.*;

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


}

