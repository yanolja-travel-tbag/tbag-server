package com.tbag.tbag_backend.domain.Location.entity;

import com.tbag.tbag_backend.domain.Content.Content;
import com.tbag.tbag_backend.domain.Location.locationImage.LocationImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "content_location")
@NoArgsConstructor
public class ContentLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "place_name", columnDefinition = "VARCHAR(255)", nullable = false)
    private String placeName;

    @Column(name = "place_name_eng", columnDefinition = "VARCHAR(255)", nullable = false)
    private String placeNameEng;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "place_type", columnDefinition = "VARCHAR(255)", nullable = false)
    private String placeType;

    @Column(name = "place_type_eng", columnDefinition = "VARCHAR(255)", nullable = false)
    private String placeTypeEng;

    @Column(name = "place_description", columnDefinition = "TEXT", nullable = false)
    private String placeDescription;

    @Column(name = "place_description_eng", columnDefinition = "TEXT", nullable = false)
    private String placeDescriptionEng;

    @Column(name = "business_hours", columnDefinition = "VARCHAR(255)")
    private String businessHours;

    @Column(name = "business_hours_eng", columnDefinition = "VARCHAR(255)")
    private String businessHoursEng;

    @Column(name = "break_time", columnDefinition = "VARCHAR(255)")
    private String breakTime;

    @Column(name = "holiday", columnDefinition = "VARCHAR(255)")
    private String holiday;

    @Column(name = "holiday_eng", columnDefinition = "VARCHAR(255)")
    private String holidayEng;

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

    @Column(name = "location_string_eng", columnDefinition = "VARCHAR(255)")
    private String locationStringEng;

    @OneToMany(mappedBy = "contentLocation", fetch = FetchType.LAZY)
    private List<LocationImage> locationImages;

    public void updateViewCount() {
        this.viewCount++;
    }
}

