package xyz.evernet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.Structure;

import java.util.Collection;
import java.util.List;

@Repository
public interface StructureRepository extends MongoRepository<Structure, String> {

    boolean existsByNodeIdentifierAndAddress(String nodeIdentifier, String address);

    List<Structure> findByNodeIdentifier(String nodeIdentifier, Pageable pageable);

    Structure findByNodeIdentifierAndAddress(String nodeIdentifier, String address);

    List<Structure> findByNodeIdentifierAndAddressIn(String nodeIdentifier, Collection<String> addresses);
}
