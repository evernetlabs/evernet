package org.evernet.vertex.repository;

import org.evernet.vertex.model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {

    Admin findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);
}
