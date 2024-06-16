package com.tbag.tbag_backend.domain.Content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@Table(name = "content")
@NoArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "media_type", nullable = false)
    private String mediaType;

    @Column(name = "title", nullable = false)
    private String title;

}
