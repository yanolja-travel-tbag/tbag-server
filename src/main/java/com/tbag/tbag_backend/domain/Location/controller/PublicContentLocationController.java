package com.tbag.tbag_backend.domain.Location.controller;

import com.tbag.tbag_backend.domain.Location.dto.*;
import com.tbag.tbag_backend.domain.Location.service.ContentLocationService;
import com.tbag.tbag_backend.domain.Location.service.PublicContentLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/content-location")
public class PublicContentLocationController {

    private final PublicContentLocationService publicContentLocationService;
    private final ContentLocationService contentLocationService;

    @GetMapping("/top-viewed")
    public List<ContentLocationDto> getTop5ByViewCount(@RequestParam String mediaType, Principal principal) {
        Integer userId = (principal != null) ? Integer.parseInt(principal.getName()) : null;
        return publicContentLocationService.getTop5ByViewCount(mediaType, userId);
    }

    @GetMapping("/latest")
    public List<ContentLocationDto> getTop5ByCreatedAt(@RequestParam String mediaType, Principal principal) {
        Integer userId = (principal != null) ? Integer.parseInt(principal.getName()) : null;
        return publicContentLocationService.getTop5ByCreatedAt(mediaType, userId);
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

    @GetMapping("/{id}/detailed")
    public ContentLocationDetailedDto getContentLocationDetailById(@PathVariable Long id, Principal principal) {
        Integer userId = (principal != null) ? Integer.parseInt(principal.getName()) : null;
        return contentLocationService.getContentLocationById(id, userId);
    }

    @GetMapping("/search")
    public Page<ContentLocationSearchDto> searchContentLocations(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return contentLocationService.searchContentLocations(keyword, page, size);
    }

    @GetMapping("/{id}/recommended")
    public Page<ContentLocationSearchDto> getRecommendedContentLocations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return contentLocationService.getRecommendedContentLocations(id, page, size);
    }

}

