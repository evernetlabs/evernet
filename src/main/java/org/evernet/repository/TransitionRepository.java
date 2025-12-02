package org.evernet.repository;

import org.evernet.model.Transition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransitionRepository extends JpaRepository<Transition, String> {

    boolean existsByNodeIdentifierAndStructureAddressAndIdentifier(String nodeIdentifier, String structureAddress, String identifier);

    boolean existsByNodeIdentifierAndStructureAddressAndSourceStateIdentifierAndEventParentReferenceChainAndEventIdentifier(String nodeIdentifier, String structureAddress, String sourceStateIdentifier, String eventParentReferenceChain, String eventIdentifier);

    List<Transition> findByNodeIdentifierAndStructureAddress(String nodeIdentifier, String structureAddress);

    Transition findByIdentifierAndStructureAddressAndNodeIdentifier(String identifier, String structureAddress, String nodeIdentifier);
}
