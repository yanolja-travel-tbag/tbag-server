package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/content")
public class PublicContentController {

    private final ContentService contentService;

    @GetMapping("/search")
    public Page<ContentSearchDto> searchContent(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return contentService.searchContent(keyword, PageRequest.of(page, size));
    }

    @PutMapping("/{id}/viewCount")
    public void updateViewCount(@PathVariable Long id) {
        contentService.updateViewCount(id);
    }

    @GetMapping("/filter")
    public Page<ContentSimpleDto> getFilteredContent(
            @RequestParam(required = true) String mediaType,
            @RequestParam(required = false) Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        return contentService.getFilteredContent(mediaType, genreId, PageRequest.of(page, size));
    }

    @GetMapping("/top-viewed")
    public List<ContentSimpleDto> getTop5ByViewCount(@RequestParam String mediaType) {
        return contentService.getTop5ByViewCount(mediaType);
    }

    @GetMapping("/recommended")
    public List<ContentSimpleDto> getRecommendedContents(@RequestParam(defaultValue = "10") int limit) {
        return contentService.getRecommendedContents(limit);
    }


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
}
