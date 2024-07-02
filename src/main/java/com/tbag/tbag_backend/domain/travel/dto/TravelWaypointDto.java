package com.tbag.tbag_backend.domain.travel.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TravelWaypointDto {
    private Long travelRequestId;
    private Long locationId;
}
