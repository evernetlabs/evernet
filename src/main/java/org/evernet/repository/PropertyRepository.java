package org.evernet.repository;

import org.evernet.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {

    boolean existsByNodeIdentifierAndStructureAddressAndIdentifier(String nodeIdentifier, String structureAddress, String identifier);

    List<Property> findByNodeIdentifierAndStructureAddress(String nodeIdentifier, String structureAddress);

    Property findByIdentifierAndStructureAddressAndNodeIdentifier(String identifier, String structureAddress, String nodeIdentifier);
}
