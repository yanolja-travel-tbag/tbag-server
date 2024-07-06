package com.tbag.tbag_backend.domain.Content.contentActor;

import com.tbag.tbag_backend.common.Language;
import com.tbag.tbag_backend.domain.Content.contentActor.ContentActor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentActorRepository extends JpaRepository<ContentActor, Long> {

    @Query("SELECT ca FROM ContentActor ca JOIN Translation t ON t.translationId.key = CONCAT('actor_name_', ca.actor.id) " +
            "WHERE t.value LIKE %:character%")
    Page<ContentActor> findByTranslatedActorName(@Param("character") String character,
                                                 Pageable pageable);
}
