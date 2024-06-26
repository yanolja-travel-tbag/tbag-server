package com.tbag.tbag_backend.domain.Actor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/actor")
public class PublicActorController {

    private final ActorService actorService;


    @GetMapping("/search")
    public ResponseEntity<Page<ContentActorDTO>> searchCelebs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ContentActorDTO> results = actorService.searchActorsByKeyword(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(results);
    }

}
