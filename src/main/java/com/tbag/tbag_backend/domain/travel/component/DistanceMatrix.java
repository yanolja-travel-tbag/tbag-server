package com.tbag.tbag_backend.domain.travel.component;

import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import com.tbag.tbag_backend.domain.Location.repository.ContentLocationRepository;
import com.tbag.tbag_backend.domain.travel.dto.TravelRouteResponse;
import com.tbag.tbag_backend.domain.travel.dto.TravelSegmentResponse;
import com.tbag.tbag_backend.domain.travel.entity.TravelWaypoint;
import com.tbag.tbag_backend.domain.travel.repository.TravelWaypointRepository;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@EnableAsync
@RequiredArgsConstructor
public class DistanceMatrix {

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.api.distance-matrix-url}")
    private String baseUrl;
    private final OkHttpClient client = new OkHttpClient();
    private final TravelWaypointRepository waypointRepository;
    private final ContentLocationRepository locationRepository;

    @Async
    public CompletableFuture<JSONObject> getDistanceMatrixAsync(String origins, String destinations) throws IOException {
        String url = baseUrl + "origins=" + origins + "&destinations=" + destinations + "&key=" + apiKey;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return CompletableFuture.completedFuture(new JSONObject(response.body().string()));
        }
    }

    private void populateAdjacencyMatrix(int[][] adjacencyMatrix, JSONObject[][] distanceResponses, int n) throws ExecutionException, InterruptedException {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    JSONObject elements = getDistanceElements(distanceResponses[i][j]);
                    adjacencyMatrix[i][j] = elements.getJSONObject("distance").getInt("value");
                }
            }
        }
    }

    private JSONObject getDistanceElements(JSONObject response) {
        JSONArray rows = response.getJSONArray("rows");
        return rows.getJSONObject(0).getJSONArray("elements").getJSONObject(0);
    }

    private TravelSegmentResponse createTravelSegment(int order, JSONObject response, Long originWaypointId, Long destWaypointId) {
        TravelSegmentResponse segment = new TravelSegmentResponse();

        TravelSegmentResponse.LocationDTO origin = createLocationDTO(originWaypointId, response, true);

        segment.setOrigin(origin);

        JSONObject elements = getDistanceElements(response);
        TravelSegmentResponse.DistanceDTO distance = new TravelSegmentResponse.DistanceDTO();
        distance.setText(elements.getJSONObject("distance").getString("text"));
        distance.setValue(elements.getJSONObject("distance").getInt("value"));
        segment.setDistance(distance);

        TravelSegmentResponse.DurationDTO duration = new TravelSegmentResponse.DurationDTO();
        duration.setText(elements.getJSONObject("duration").getString("text"));
        duration.setValue(elements.getJSONObject("duration").getInt("value"));
        segment.setDuration(duration);

        segment.setOrder(order);

        return segment;
    }

    private TravelSegmentResponse.LocationDTO createLocationDTO(Long waypointId, JSONObject response, boolean isOrigin) {
        TravelWaypoint waypoint = waypointRepository.findById(waypointId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Waypoint not found"));

        TravelSegmentResponse.LocationDTO location = new TravelSegmentResponse.LocationDTO();
        ContentLocation contentLocation = waypoint.getOriginLocation();

        location.setLocationId(contentLocation.getId());
        location.setLatitude(contentLocation.getLatitude());
        location.setLongitude(contentLocation.getLongitude());
        location.setPlaceName(contentLocation.getPlaceName());
        location.setImage(contentLocation.getLocationImages().stream().findFirst().get().getImageUrl());

        if (isOrigin) {
            location.setAddresses(response.getJSONArray("origin_addresses").getString(0));
        } else {
            location.setAddresses(response.getJSONArray("destination_addresses").getString(0));
        }

        return location;
    }

    public TravelRouteResponse buildTravelSegments(String[] locations, List<Long> waypointIds) throws IOException, ExecutionException, InterruptedException {
        int n = locations.length;
        int[][] adjacencyMatrix = new int[n][n];
        List<CompletableFuture<JSONObject>> futures = new ArrayList<>();
        JSONObject[][] distanceResponses = new JSONObject[n][n]; // 응답을 저장할 2차원 배열

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    CompletableFuture<JSONObject> future = getDistanceMatrixAsync(locations[i], locations[j]);
                    futures.add(future);
                    final int originIndex = i;
                    final int destinationIndex = j;
                    future.thenAccept(response -> distanceResponses[originIndex][destinationIndex] = response);
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        populateAdjacencyMatrix(adjacencyMatrix, distanceResponses, n);

        int[] bestRoute = new TSPSolver().findBestRoute(adjacencyMatrix);

        List<TravelSegmentResponse> segments = new ArrayList<>();
        int totalDistance = 0;
        int totalDuration = 0;

        for (int i = 0; i < bestRoute.length; i++) {
            int originIndex = bestRoute[i];
            int destinationIndex = bestRoute[(i + 1) % bestRoute.length];

            Long originWaypointId = waypointIds.get(originIndex);
            Long destWaypointId = waypointIds.get(destinationIndex);

            JSONObject response = distanceResponses[originIndex][destinationIndex];
            TravelSegmentResponse segment = createTravelSegment(i + 1, response, originWaypointId, destWaypointId);

            segments.add(segment);
            updateWaypoint(originWaypointId, destWaypointId, segment, i);

            totalDistance += segment.getDistance().getValue();
            totalDuration += segment.getDuration().getValue();
        }

        TravelRouteResponse result = new TravelRouteResponse();
        result.setSegments(segments);
        result.setTotalDistance(totalDistance);
        result.setTotalDuration(totalDuration);

        return result;
    }

    private void updateWaypoint(Long originWaypointId, Long destWaypointId, TravelSegmentResponse segment, int sequence) {
        TravelWaypoint originWaypoint = waypointRepository.findById(originWaypointId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Origin Waypoint not found"));
        originWaypoint.setSequence(sequence);
        originWaypoint.setDestLocation(waypointRepository.findById(destWaypointId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "Destination Waypoint not found"))
                .getOriginLocation());
        originWaypoint.setDistance(segment.getDistance().getValue());
        originWaypoint.setDuration(segment.getDuration().getValue());

        waypointRepository.save(originWaypoint);
    }
}
