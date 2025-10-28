package org.evernet.repository;

import org.evernet.model.Inheritance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InheritanceRepository extends JpaRepository<Inheritance, String> {

    Boolean existsByNodeIdentifierAndStructureAddressAndInheritedStructureAddress(String nodeIdentifier, String structureAddress, String inheritedStructureAddress);
}
