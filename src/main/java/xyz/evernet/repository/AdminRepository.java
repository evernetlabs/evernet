package xyz.evernet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.Admin;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {

    Admin findByUsername(String username);

    boolean existsByUsername(String username);
}
