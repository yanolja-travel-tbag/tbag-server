package com.tbag.tbag_backend.domain.Genre.service;

import com.tbag.tbag_backend.domain.Genre.GenreDto;
import com.tbag.tbag_backend.domain.Genre.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    public List<GenreDto> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(GenreDto::toGenreDto)
                .collect(Collectors.toList());
    }
}
