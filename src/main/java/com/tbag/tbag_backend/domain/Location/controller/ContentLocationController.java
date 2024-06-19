package com.tbag.tbag_backend.domain.Location.controller;

import com.tbag.tbag_backend.domain.Location.dto.ContentLocationDetailDto;
import com.tbag.tbag_backend.domain.Location.service.ContentLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/content-location")
public class ContentLocationController {

    private final ContentLocationService contentLocationService;

    @GetMapping("/{id}")
    public ContentLocationDetailDto getContentLocationById(@PathVariable Long id) {
        return contentLocationService.getContentLocationById(id);
    }

}

