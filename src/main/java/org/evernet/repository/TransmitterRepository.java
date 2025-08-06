package org.evernet.repository;

import org.evernet.model.Transmitter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransmitterRepository extends JpaRepository<Transmitter, String> {

    boolean existsByNodeIdentifierAndActorAddress(String nodeIdentifier, String actorAddress);

    List<Transmitter> findByNodeIdentifier(String nodeIdentifier, Pageable pageable);

    Transmitter findByNodeIdentifierAndActorAddress(String nodeIdentifier, String actorAddress);
}
