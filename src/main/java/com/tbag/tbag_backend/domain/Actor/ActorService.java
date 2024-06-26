package com.tbag.tbag_backend.domain.Actor;

import com.tbag.tbag_backend.domain.Content.contentActor.ContentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.tbag.tbag_backend.common.LocalizedNameDto.getLocalizedMediaType;
import static com.tbag.tbag_backend.common.LocalizedNameDto.mapToLocalizedNameDto;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ContentActorRepository contentActorRepository;
    @Value("${tmdb.base-image-url}")
    private String imageBaseUrl;

    public Page<ContentActorDTO> searchActorsByKeyword(String keyword, Pageable pageable) {
        Page<ContentActor> contentActors = contentActorRepository.findByActor_NameContainingOrActor_OriginalNameContaining(keyword, keyword, pageable);
        return contentActors.map(this::convertToDTO);
    }

    private ContentActorDTO convertToDTO(ContentActor contentActor) {
        return new ContentActorDTO(
                contentActor.getContent().getId(),
                mapToLocalizedNameDto(contentActor.getContent().getTitle(), contentActor.getContent().getTitleEng()),
                mapToLocalizedNameDto(contentActor.getCharacter(), contentActor.getCharacterEng()),
                mapToLocalizedNameDto(contentActor.getActor().getName(),contentActor.getActor().getOriginalName()),
                imageBaseUrl + contentActor.getContent().getContentDetails().getPosterPath(),
                contentActor.getContent().getViewCount(),
                getLocalizedMediaType(contentActor.getContent().getMediaType())
        );
    }
}
