package xyz.evernet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.Node;

import java.util.List;

@Repository
public interface NodeRepository extends MongoRepository<Node, String> {

    boolean existsByIdentifier(String identifier);

    List<Node> findByOpenIsTrue(Pageable pageable);

    Node findByIdentifier(String identifier);
}
