package com.tbag.tbag_backend.domain.Content.contentActor;

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
public class ContentActorId implements Serializable {

    private Long contentId;
    private Long actorId;

}
