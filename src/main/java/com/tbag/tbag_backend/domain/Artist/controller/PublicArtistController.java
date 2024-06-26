package com.tbag.tbag_backend.domain.Artist.controller;

import com.tbag.tbag_backend.domain.Artist.ArtistSearchDto;
import com.tbag.tbag_backend.domain.Artist.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/artist")
public class PublicArtistController {

    private final ArtistService artistService;


    @GetMapping("/search")
    public ResponseEntity<Page<ArtistSearchDto>> searchCelebs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ArtistSearchDto> results = artistService.searchArtistsByKeyword(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(results);
    }

}
