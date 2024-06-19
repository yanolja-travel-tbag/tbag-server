package com.tbag.tbag_backend.domain.Location.controller;

import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDto;
import com.tbag.tbag_backend.domain.Location.dto.MapContentLocationDto;
import com.tbag.tbag_backend.domain.Location.dto.MarkerLocationDto;
import com.tbag.tbag_backend.domain.Location.service.PublicContentLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/content-location")
public class PublicContentLocationController {

    private final PublicContentLocationService publicContentLocationService;

    @GetMapping("/top-viewed")
    public List<ContentLocationDto> getTop5ByViewCount(@RequestParam String mediaType) {
        return publicContentLocationService.getTop5ByViewCount(mediaType);
    }

    @GetMapping("/latest")
    public List<ContentLocationDto> getTop5ByCreatedAt(@RequestParam String mediaType) {
        return publicContentLocationService.getTop5ByCreatedAt(mediaType);
    }

    @PostMapping("/increment-view/{id}")
    public void incrementViewCount(@PathVariable Long id) {
        publicContentLocationService.incrementViewCount(id);
    }

    @GetMapping
    public List<MapContentLocationDto> getAllContentLocations(@RequestParam(required = false, defaultValue = "all") String mediaType) {
        return publicContentLocationService.getContentLocations(mediaType);
    }

    @GetMapping("/{id}")
    public MarkerLocationDto getContentLocationById(@PathVariable Long id) {
        return publicContentLocationService.getContentLocationById(id);
    }

}

