package org.evernet.vertex.repository;

import org.evernet.vertex.model.Config;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends MongoRepository<Config, String> {

    Config findByKey(String key);
}
