package com.tbag.tbag_backend.domain.Location.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.domain.Content.Content;
import com.tbag.tbag_backend.domain.Location.PlaceType;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImage;
import com.tbag.tbag_backend.domain.travel.entity.TravelWaypoint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Entity
@Getter
@Table(name = "content_location")
@NoArgsConstructor
public class ContentLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "break_time", columnDefinition = "VARCHAR(255)")
    private String breakTime;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "phone_number", columnDefinition = "VARCHAR(255)")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "place_type", columnDefinition = "VARCHAR(255)")
    private PlaceType placeType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @JsonIgnore
    @OneToMany(mappedBy = "contentLocation", fetch = FetchType.LAZY)
    private List<LocationImage> locationImages;

    @JsonIgnore
    @OneToMany(mappedBy = "originLocation", fetch = FetchType.LAZY)
    private List<TravelWaypoint> travelWaypoints;

    public void updateViewCount() {
        this.viewCount++;
    }

    public boolean isInSchedule(Integer userId) {
        if (userId == null) {
            return false;
        }
        return travelWaypoints.stream()
                .anyMatch(waypoint -> waypoint.getTravelRequest().getUser().getId().equals(userId));
    }

    public String getContentLocationPlaceNameKey() {
        return "content_location_place_name_" + id;
    }

    public String getContentLocationBusinessHoursKey() {
        return "content_location_business_hours_" + id;
    }

    public String getContentLocationHolidayKey() {
        return "content_location_holiday_" + id;
    }

    public String getContentLocationLocationStringKey() {
        return "content_location_location_string_" + id;
    }

    public String getContentLocationPlaceDescriptionKey() {
        return "content_location_place_description_" + id;
    }

    public String getPlaceType() {
        Locale locale = Language.ofLocale().getLocale();
        return placeType.getName(locale);
    }
}
