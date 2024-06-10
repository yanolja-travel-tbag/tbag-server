package com.tbag.tbag_backend.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LocalizedNameDto {

    private String eng;
    private String kor;
}
