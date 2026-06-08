package xyz.evernet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.Structure;

@Repository
public interface StructureRepository extends MongoRepository<Structure, String> {

    boolean existsByIdentifier(String identifier);

    Structure findByIdentifier(String identifier);
}
