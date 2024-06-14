package com.tbag.tbag_backend.domain.Location;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/content-location")
public class PublicContentLocationController {

    private final ContentLocationService contentLocationService;

    @GetMapping("/top-viewed")
    public List<ContentLocationDto> getTop5ByViewCount(@RequestParam String mediaType) {
        return contentLocationService.getTop5ByViewCount(mediaType);
    }

    @GetMapping("/latest")
    public List<ContentLocationDto> getTop5ByCreatedAt(@RequestParam String mediaType) {
        return contentLocationService.getTop5ByCreatedAt(mediaType);
    }

    @PostMapping("/increment-view/{id}")
    public void incrementViewCount(@PathVariable Long id) {
        contentLocationService.incrementViewCount(id);
    }
}

