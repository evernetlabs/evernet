package org.evernet.repository;

import jakarta.validation.constraints.NotBlank;
import org.evernet.model.Inheritance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InheritanceRepository extends JpaRepository<Inheritance, String> {

    Boolean existsByNodeIdentifierAndStructureAddressAndInheritedStructureAddress(String nodeIdentifier, String structureAddress, String inheritedStructureAddress);

    List<Inheritance> findByNodeIdentifierAndStructureAddress(String nodeIdentifier, String structureAddress);

    Inheritance findByNodeIdentifierAndStructureAddressAndInheritedStructureAddress(String nodeIdentifier, String structureAddress,  String inheritedStructureAddress);
}
