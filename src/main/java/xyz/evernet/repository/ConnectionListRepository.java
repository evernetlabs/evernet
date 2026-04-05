package xyz.evernet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.ConnectionList;

import java.util.List;

@Repository
public interface ConnectionListRepository extends MongoRepository<ConnectionList, String> {

    boolean existsByNodeIdentifierAndUsernameAndName(String nodeIdentifier, String username, String name);

    List<ConnectionList> findByNodeIdentifierAndUsername(String nodeIdentifier, String username, Pageable pageable);

    ConnectionList findByNodeIdentifierAndUsernameAndName(String nodeIdentifier, String username, String name);
}
