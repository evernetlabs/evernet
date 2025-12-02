package org.evernet.repository;

import org.evernet.enums.ActionType;
import org.evernet.model.TransitionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransitionActionRepository extends JpaRepository<TransitionAction, String> {

    boolean existsByNodeIdentifierAndStructureAddressAndTransitionIdentifierAndActionParentReferenceChainAndActionTypeAndActionIdentifier(String nodeIdentifier, String structureAddress, String transitionIdentifier, String actionParentReferenceChain, ActionType actionType, String actionIdentifier);

    List<TransitionAction> findByNodeIdentifierAndStructureAddressAndTransitionIdentifier(String nodeIdentifier, String structureAddress, String transitionIdentifier);

    TransitionAction findByNodeIdentifierAndStructureAddressAndTransitionIdentifierAndActionParentReferenceChainAndActionTypeAndActionIdentifier(String nodeIdentifier, String structureAddress, String transitionIdentifier, String actionParentReferenceChain, ActionType actionType, String actionIdentifier);
}
