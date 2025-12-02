package org.evernet.repository;

import org.evernet.enums.ActionType;
import org.evernet.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, String> {

    boolean existsByNodeIdentifierAndStructureAddressAndEventParentReferenceChainAndEventIdentifierAndActionParentReferenceChainAndActionTypeAndActionIdentifier(String nodeIdentifier, String structureAddress, String eventParentReferenceChain, String eventIdentifier, String actionParentReferenceChain, ActionType actionType, String actionIdentifier);

    List<Connection> findByNodeIdentifierAndStructureAddress(String nodeIdentifier, String structureAddress);

    Connection findByNodeIdentifierAndStructureAddressAndEventParentReferenceChainAndEventIdentifierAndActionParentReferenceChainAndActionTypeAndActionIdentifier(String nodeIdentifier, String structureAddress, String eventParentReferenceChain, String eventIdentifier, String actionParentReferenceChain, ActionType actionType, String actionIdentifier);
}
