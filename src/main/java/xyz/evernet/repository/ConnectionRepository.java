package xyz.evernet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.Connection;

import java.util.List;

@Repository
public interface ConnectionRepository extends MongoRepository<Connection, String> {

    boolean existsByNodeIdentifierAndUsernameAndConnectionUserAddress(String nodeIdentifier, String username, String connectionUserAddress);

    List<Connection> findByNodeIdentifierAndUsername(String nodeIdentifier, String username, Pageable pageable);

    Connection findByNodeIdentifierAndUsernameAndConnectionUserAddress(String nodeIdentifier, String username, String connectionUserAddress);

    List<Connection> findByNodeIdentifierAndUsernameAndConnectionListsContaining(String nodeIdentifier, String username, String connectionLists, Pageable pageable);
}
