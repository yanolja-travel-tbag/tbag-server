package com.tbag.tbag_backend.domain.Genre.userPreferredGenre.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserPreferredGenreId implements Serializable {

    private Integer userId;
    private String mediaType;
    private Long genreId;


    public UserPreferredGenreId(Integer userId, String mediaType, Long genreId) {
        this.userId = userId;
        this.mediaType = mediaType;
        this.genreId = genreId;
    }
}
