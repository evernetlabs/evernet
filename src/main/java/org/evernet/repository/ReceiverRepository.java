package org.evernet.repository;

import org.evernet.model.Receiver;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, String> {

    boolean existsByNodeIdentifierAndActorAddress(String nodeIdentifier, String actorAddress);

    Receiver findByNodeIdentifierAndActorAddress(String nodeIdentifier, String actorAddress);

    List<Receiver> findByNodeIdentifier(String nodeIdentifier, Pageable pageable);
}
