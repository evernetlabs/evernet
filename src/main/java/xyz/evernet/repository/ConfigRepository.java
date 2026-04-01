package xyz.evernet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.model.Config;

@Repository
public interface ConfigRepository extends MongoRepository<Config, String> {
    Config findByKey(String key);
}
