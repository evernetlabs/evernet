package xyz.evernet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.Node;

@Repository
public interface NodeRepository extends MongoRepository<Node, String> {

    Node findByAddress(String address);

    void deleteByAddress(String address);
}
