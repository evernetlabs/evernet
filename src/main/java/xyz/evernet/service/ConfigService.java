package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.Config;
import xyz.evernet.repository.ConfigRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;

    private final MongoTemplate mongoTemplate;

    public Config setIfMissing(String key, String value) {
        Config config = mongoTemplate.findAndModify(Query.query(Criteria.where("key").is(key)),
                new Update()
                        .setOnInsert("key", key)
                        .setOnInsert("value", value)
                        .setOnInsert("createdAt", Instant.now())
                        .setOnInsert("updatedAt", Instant.now()),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Config.class);

        if (config == null) {
            throw new NotFoundException("Config %s not found".formatted(key));
        }

        return config;
    }

    public Config set(String key, String value) {
        Config config = mongoTemplate.findAndModify(Query.query(Criteria.where("key").is(key)),
                new Update()
                        .set("value", value)
                        .setOnInsert("key", key)
                        .setOnInsert("createdAt", Instant.now())
                        .set("updatedAt", Instant.now()),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Config.class);

        if (config == null) {
            throw new NotFoundException("Config %s not found".formatted(key));
        }

        return config;
    }

    public Config get(String key) {
        Config config = configRepository.findByKey(key);

        if (config == null) {
            throw new NotFoundException("Config %s not found".formatted(key));
        }

        return config;
    }

    public String getValue(String key, String defaultValue) {
        Config config = configRepository.findByKey(key);

        if (config == null) {
            return defaultValue;
        }

        return config.getValue();
    }
}
