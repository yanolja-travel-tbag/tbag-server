package com.tbag.tbag_backend.domain.Actor;

import com.tbag.tbag_backend.domain.Content.contentActor.ContentActor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentActorRepository extends JpaRepository<ContentActor, Long> {

    Page<ContentActor> findByActor_NameContainingOrActor_OriginalNameContaining(String character, String characterEng, Pageable pageable);

}
