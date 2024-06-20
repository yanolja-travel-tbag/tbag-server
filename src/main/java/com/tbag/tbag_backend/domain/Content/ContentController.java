package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.domain.Actor.ContentSearchDto;
import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/{contentId}")
    public ContentSearchDto getContentById(@PathVariable Long contentId) {
        return contentService.getContentById(contentId);
    }

    @GetMapping("/{id}/related-locations")
    public Page<ContentLocationDetailDto> getRelatedLocations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return contentService.getRelatedLocations(id, page, size);
    }
}
