package com.tbag.tbag_backend.domain.Location.locationImage;

import com.tbag.tbag_backend.domain.Location.entity.ContentLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@Table(name = "location_images")
@NoArgsConstructor
public class LocationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private ContentLocation contentLocation;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "sizeheight")
    private Integer sizeHeight;

    @Column(name = "sizewidth")
    private Integer sizeWidth;
}

