package com.tbag.tbag_backend.domain.Content.contentArtist;

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
public class ContentArtistId implements Serializable {

    private Long artistId;
    private Long contentId;

}
