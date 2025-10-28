package org.evernet.repository;

import org.evernet.model.Structure;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StructureRepository extends JpaRepository<Structure, String> {

    Boolean existsByNodeIdentifierAndAddress(String nodeIdentifier, String address);

    List<Structure> findByNodeIdentifier(String nodeIdentifier, Pageable pageable);

    Structure findByAddressAndNodeIdentifier(String address, String nodeIdentifier);
}
