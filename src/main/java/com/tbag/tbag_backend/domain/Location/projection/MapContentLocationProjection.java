package com.tbag.tbag_backend.domain.Location.projection;

public interface MapContentLocationProjection {
    Long getId();

    String getContentTitle();

    String getContentTitleEng();

    String getContentMediaType();

    Double getLatitude();

    Double getLongitude();
}
