package com.tbag.tbag_backend.domain.Actor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@Table(name = "actor")
@NoArgsConstructor
public class Actor {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "profile_path", columnDefinition = "TEXT")
    private String profilePath;

}

