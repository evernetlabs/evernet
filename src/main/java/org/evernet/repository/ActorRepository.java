package org.evernet.repository;

import org.evernet.model.Actor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorRepository extends JpaRepository<Actor, String> {
    boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);

    Actor findByNodeIdentifierAndIdentifier(String nodeIdentifier, String identifier);

    List<Actor> findByNodeIdentifier(String nodeIdentifier, Pageable pageable);
}
