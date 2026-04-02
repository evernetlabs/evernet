package xyz.evernet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.User;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByNodeIdentifierAndUsername(String nodeIdentifier, String username);

    User findByNodeIdentifierAndUsername(String nodeIdentifier, String username);

    List<User> findByNodeIdentifier(String nodeIdentifier, Pageable pageable);
}
