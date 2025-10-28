package org.evernet.repository;

import org.evernet.model.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, String> {
    boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);

    Actor findByNodeIdentifierAndIdentifier(String nodeIdentifier, String identifier);
}
