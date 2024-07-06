package com.tbag.tbag_backend.domain.Location.controller;

import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDetailDto;
import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDto;
import com.tbag.tbag_backend.domain.Location.dto.MapContentLocationDto;
import com.tbag.tbag_backend.domain.Location.dto.MarkerLocationDto;
import com.tbag.tbag_backend.domain.Location.service.ContentLocationService;
import com.tbag.tbag_backend.domain.Location.service.PublicContentLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/content-location")
public class PublicContentLocationController {

    private final PublicContentLocationService publicContentLocationService;
    private final ContentLocationService contentLocationService;

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


    @GetMapping("/search")
    public Page<ContentLocationDetailDto> searchContentLocations(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return contentLocationService.searchContentLocations(keyword, page, size);
    }


    @GetMapping("/{id}/detailed")
    public ContentLocationDetailDto getContentLocationDetailById(@PathVariable Long id) {
        return contentLocationService.getContentLocationById(id);
    }

    @GetMapping("/{id}/recommended")
    public Page<ContentLocationDetailDto> getRecommendedContentLocations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return contentLocationService.getRecommendedContentLocations(id, page, size);
    }

}

