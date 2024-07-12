package com.tbag.tbag_backend.common;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;

public class GeoHelper {

    public static Point parseSQLStringToPoint(String wkt) {
        try {
            WKTReader reader = new WKTReader();
            return (Point) reader.read(wkt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse WKT", e);
        }
    }
}
