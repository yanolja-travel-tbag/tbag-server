package com.tbag.tbag_backend.domain.Content;

import com.tbag.tbag_backend.domain.Actor.ContentSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
