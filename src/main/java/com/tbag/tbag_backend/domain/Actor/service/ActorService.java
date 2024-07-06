package com.tbag.tbag_backend.domain.Actor.service;

import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.common.TranslationService;
import com.tbag.tbag_backend.domain.Content.contentActor.ContentActorRepository;
import com.tbag.tbag_backend.domain.Content.contentActor.ContentActorDto;
import com.tbag.tbag_backend.domain.Actor.entity.Actor;
import com.tbag.tbag_backend.domain.Content.Content;
import com.tbag.tbag_backend.domain.Content.ContentDetails;
import com.tbag.tbag_backend.domain.Content.contentActor.ContentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class ActorService {

    private final ContentActorRepository contentActorRepository;
    private final TranslationService translationService;
    @Value("${tmdb.base-image-url}")
    private String imageBaseUrl;

    public Page<ContentActorDto> searchActorsByKeyword(String keyword, Pageable pageable) {
        Page<ContentActor> contentActors = contentActorRepository.findByTranslatedActorName(keyword, Language.ofLocale(), pageable);
        return contentActors.map(this::convertToDTO);
    }

    private ContentActorDto convertToDTO(ContentActor contentActor) {
        Content content = contentActor.getContent();
        Actor actor = contentActor.getActor();
        ContentDetails contentDetails = content.getContentDetails();
        return new ContentActorDto(
                content.getId(),
                contentActor.getContent().getContentTitleKey(),
                contentActor.getCharacter(),
                actor.getName(),
                imageBaseUrl + contentDetails.getPosterPath(),
                content.getViewCount(),
                content.getMediaType()
        );
    }
}
