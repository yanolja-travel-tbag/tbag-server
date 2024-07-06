package com.tbag.tbag_backend.domain.Genre;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.common.Translatable;
import com.tbag.tbag_backend.common.TranslatableField;
import com.tbag.tbag_backend.common.TranslationId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
