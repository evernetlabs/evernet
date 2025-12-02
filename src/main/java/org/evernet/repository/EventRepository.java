package org.evernet.repository;

import org.evernet.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    boolean existsByNodeIdentifierAndStructureAddressAndIdentifier(String nodeIdentifier, String structureAddress, String identifier);

    List<Event> findByNodeIdentifierAndStructureAddress(String nodeIdentifier, String structureAddress);

    Event findByIdentifierAndStructureAddressAndNodeIdentifier(String identifier, String structureAddress, String nodeIdentifier);

    Boolean existsByIdentifierAndStructureAddressAndNodeIdentifier(String identifier, String structureAddress, String nodeIdentifier);
}
