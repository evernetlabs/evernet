package xyz.evernet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

}
