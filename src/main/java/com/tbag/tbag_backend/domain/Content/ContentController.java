package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recent") String sort) {
        return contentService.getRelatedLocations(id, page, size, sort);
    }

    @GetMapping("/recommended")
    public List<ContentSimpleDto> getRecommendedContents(@RequestParam(defaultValue = "10") int limit, Principal principal) {
        return contentService.getRecommendedContents(principal, limit);
    }

}
