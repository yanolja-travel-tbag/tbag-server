package com.tbag.tbag_backend.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TranslationRepository extends JpaRepository<Translation, TranslationId> {
    List<Translation> findByTranslationIdKey(String key);

    Optional<Translation> findByTranslationId(TranslationId translationId);

}
