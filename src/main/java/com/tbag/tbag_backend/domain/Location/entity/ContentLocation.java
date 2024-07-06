package com.tbag.tbag_backend.domain.Location.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.common.Translatable;
import com.tbag.tbag_backend.common.TranslatableField;
import com.tbag.tbag_backend.common.TranslationId;
import com.tbag.tbag_backend.domain.Content.Content;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImage;
import com.tbag.tbag_backend.domain.travel.entity.TravelWaypoint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Table(name = "content_location")
@NoArgsConstructor
public class ContentLocation implements Translatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "place_name", columnDefinition = "VARCHAR(255)", nullable = false)
    private String placeName;
    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "place_type", columnDefinition = "VARCHAR(255)", nullable = false)
    private String placeType;

    @Column(name = "place_description", columnDefinition = "TEXT", nullable = false)
    private String placeDescription;

    @Column(name = "business_hours", columnDefinition = "VARCHAR(255)")
    private String businessHours;

    @Column(name = "break_time", columnDefinition = "VARCHAR(255)")
    private String breakTime;

    @Column(name = "holiday", columnDefinition = "VARCHAR(255)")
    private String holiday;
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "phone_number", columnDefinition = "VARCHAR(255)")
    private String phoneNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "location_string", columnDefinition = "VARCHAR(255)")
    private String locationString;

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

    @Override
    @JsonIgnore
    public List<TranslatableField> getTranslatableFields() {
        List<TranslatableField> fields = new ArrayList<>();
        fields.add(new SimpleTranslatableField(placeName, "content_location_place_name_" + id));
        fields.add(new SimpleTranslatableField(placeType, "content_location_place_type_" + id));
        fields.add(new SimpleTranslatableField(placeDescription, "content_location_place_description_" + id));
        fields.add(new SimpleTranslatableField(businessHours, "content_location_business_hours_" + id));
        fields.add(new SimpleTranslatableField(holiday, "content_location_holiday_" + id));
        fields.add(new SimpleTranslatableField(locationString, "content_location_location_string_" + id));
        return fields;
    }

    private static class SimpleTranslatableField implements TranslatableField {
        private String value;
        private final String key;

        SimpleTranslatableField(String value, String key) {
            this.value = value;
            this.key = key;
        }

        @Override
        public String getTranslationKey() {
            return key;
        }

        @Override
        public TranslationId getTranslationId() {
            return new TranslationId(key, Language.ofLocale());
        }

        @Override
        public void setTranslatedValue(String translatedValue) {
            this.value = translatedValue;
        }

        public String getValue() {
            return value;
        }
    }
}
