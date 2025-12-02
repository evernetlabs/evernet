package org.evernet.repository;

import org.evernet.model.Node;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, String> {

    boolean existsByIdentifier(String identifier);

    Node findByIdentifier(String identifier);

    List<Node> findByOpenIsTrue(Pageable pageable);
}
