package org.evernet.repository;

import org.evernet.model.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, String> {

    boolean existsByIdentifierAndFromStructureAddressAndNodeIdentifier(String identifier, String fromStructureAddress, String nodeIdentifier);

    List<Relationship> findByFromStructureAddressAndNodeIdentifier(String fromStructureAddress, String nodeIdentifier);

    Relationship findByIdentifierAndFromStructureAddressAndNodeIdentifier(String identifier, String fromStructureAddress, String nodeIdentifier);
}
