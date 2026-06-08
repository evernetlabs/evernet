package xyz.evernet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.RemoteStructure;

@Repository
public interface RemoteStructureRepository extends MongoRepository<RemoteStructure, String> {

    RemoteStructure findByAddress(String address);
}
