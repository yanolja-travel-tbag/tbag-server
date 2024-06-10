package com.tbag.tbag_backend.domain.Genre;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.common.LocalizedNameDto;
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

    @Transient
    private LocalizedNameDto name;

    @JsonIgnore
    @Column(name = "name_eng", nullable = false)
    private String nameEng;

    @JsonIgnore
    @Column(name = "name_kor", nullable = false)
    private String nameKor;

    @PostLoad
    private void postLoad() {
        this.name = LocalizedNameDto.builder()
                .eng(this.nameEng)
                .kor(this.nameKor)
                .build();
    }
}