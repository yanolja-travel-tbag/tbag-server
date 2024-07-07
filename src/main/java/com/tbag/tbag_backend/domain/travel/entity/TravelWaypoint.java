package com.tbag.tbag_backend.domain.travel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import com.tbag.tbag_backend.domain.travel.component.DistanceMatrix;
import com.tbag.tbag_backend.domain.travel.dto.TravelSegmentResponse;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    public static CompletableFuture<TravelSegmentResponse> toResponseAsync(TravelWaypoint travelWaypoint, DistanceMatrix distanceMatrix) throws IOException {
        if (travelWaypoint == null) {
            return CompletableFuture.completedFuture(null);
        }

        TravelSegmentResponse response = new TravelSegmentResponse();
        response.setWaypointId(travelWaypoint.getId());
        response.setOrder(travelWaypoint.getSequence());

        TravelSegmentResponse.LocationDTO origin = populateLocationDTO(travelWaypoint.getOriginLocation());
        response.setOrigin(origin);

        if (travelWaypoint.getDestLocation() != null) {
            String origins = travelWaypoint.getOriginLocation().getLatitude() + "," + travelWaypoint.getOriginLocation().getLongitude();
            String destinations = travelWaypoint.getDestLocation().getLatitude() + "," + travelWaypoint.getDestLocation().getLongitude();

            TravelSegmentResponse.LocationDTO dest = populateLocationDTO(travelWaypoint.getDestLocation());
            response.setDest(dest);

            return distanceMatrix.getDistanceMatrixAsync(origins, destinations).thenApply(distanceMatrixResponse -> {
                try {
                    JSONObject element = distanceMatrixResponse.getJSONArray("rows").getJSONObject(0)
                            .getJSONArray("elements").getJSONObject(0);
                    response.setDistance(element.getJSONObject("distance").getLong("value"));
                    response.setDuration(element.getJSONObject("duration").getLong("value"));
                    response.setDistanceString(element.getJSONObject("distance").getString("text"));
                    response.setDurationString(element.getJSONObject("duration").getString("text"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            });
        } else {

            return CompletableFuture.completedFuture(response);
        }
    }

    public static CompletableFuture<List<TravelSegmentResponse>> toResponseList(List<TravelWaypoint> travelWaypoints, DistanceMatrix distanceMatrix) throws IOException {
        if (travelWaypoints == null) {
            return CompletableFuture.completedFuture(null);
        }

        List<CompletableFuture<TravelSegmentResponse>> futures = new ArrayList<>();
        for (TravelWaypoint travelWaypoint : travelWaypoints) {
            futures.add(toResponseAsync(travelWaypoint, distanceMatrix));
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allOf.thenApply(v -> {
            List<TravelSegmentResponse> responses = new ArrayList<>();
            for (CompletableFuture<TravelSegmentResponse> future : futures) {
                try {
                    responses.add(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return responses;
        });
    }

    private static TravelSegmentResponse.LocationDTO populateLocationDTO(ContentLocation location) {
        TravelSegmentResponse.LocationDTO locationDTO = new TravelSegmentResponse.LocationDTO();
        if (location != null) {
            locationDTO.setLocationId(location.getId());
            locationDTO.setPlaceName(location.getContentLocationPlaceNameKey());
            locationDTO.setContentMediaType(location.getContent().getMediaType(Locale.US).toUpperCase());
            locationDTO.setLatitude(location.getLatitude());
            locationDTO.setLongitude(location.getLongitude());
            locationDTO.setAddresses(location.getContentLocationLocationStringKey());

            if (location.getLocationImages() != null && !location.getLocationImages().isEmpty()) {
                locationDTO.setImage(location.getLocationImages().get(0).getImageUrl());
            } else {
                locationDTO.setImage(null);
            }
        }
        return locationDTO;
    }

}

