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

    @GetMapping("/recommended")
    public List<ContentSimpleDto> getRecommendedContents(@RequestParam(defaultValue = "10") int limit, Principal principal) {
        return contentService.getRecommendedContents(principal, limit);
    }

}
