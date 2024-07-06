package com.tbag.tbag_backend.domain.Genre;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@Table(name = "genres")
@NoArgsConstructor
public class Genre {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    public String getGenreName() {
        return "genres_name_" + id;
    }
}
