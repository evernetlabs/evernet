package org.evernet.repository;

import org.evernet.model.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, String> {

    boolean existsByNodeIdentifierAndStructureAddressAndIdentifier(String nodeIdentifier, String structureAddress, String identifier);

    List<State> findByNodeIdentifierAndStructureAddress(String nodeIdentifier, String structureAddress);

    State findByIdentifierAndStructureAddressAndNodeIdentifier(String identifier, String structureAddress, String nodeIdentifier);
}
