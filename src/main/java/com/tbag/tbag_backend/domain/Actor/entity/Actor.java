package com.tbag.tbag_backend.domain.Actor.entity;

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

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "profile_path", columnDefinition = "TEXT")
    private String profilePath;

    public String getActorNameKey() {
        return "actor_name_" + id;
    }
}

