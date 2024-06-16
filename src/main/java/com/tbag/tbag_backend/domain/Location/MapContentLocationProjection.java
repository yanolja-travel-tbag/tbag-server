package com.tbag.tbag_backend.domain.Location;

public interface MapContentLocationProjection {
    Long getId();

    String getContentTitle();

    String getContentMediaType();

    Double getLatitude();

    Double getLongitude();
}
