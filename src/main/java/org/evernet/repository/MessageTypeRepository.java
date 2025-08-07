package org.evernet.repository;

import org.evernet.model.MessageType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageTypeRepository extends JpaRepository<MessageType, String> {

    boolean existsByNodeIdentifierAndAddress(String nodeIdentifier, String address);

    List<MessageType> findByNodeIdentifier(String nodeIdentifier, Pageable pageable);

    MessageType findByNodeIdentifierAndAddress(String nodeIdentifier, String address);
}
