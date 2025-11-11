package org.evernet.repository;

import org.evernet.model.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionRepository extends JpaRepository<Function, String> {

    boolean existsByNodeIdentifierAndStructureAddressAndIdentifier(String nodeIdentifier, String structureAddress, String identifier);

    List<Function> findByNodeIdentifierAndStructureAddress(String nodeIdentifier, String structureAddress);

    Function findByIdentifierAndStructureAddressAndNodeIdentifier(String identifier, String structureAddress, String nodeIdentifier);
}
