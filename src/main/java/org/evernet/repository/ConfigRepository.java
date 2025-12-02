package org.evernet.repository;

import org.evernet.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ConfigRepository extends JpaRepository<Config, String> {

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO config (id, key, value, created_at, updated_at)
            VALUES (:id, :key, :value, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON CONFLICT(key) DO UPDATE SET
                value = excluded.value,
                updated_at = CURRENT_TIMESTAMP
            """,
            nativeQuery = true
    )
    void upsert(
            @Param("id") String id,
            @Param("key") String key,
            @Param("value") String value
    );

    Config findByKey(String key);

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO config (id, key, value, created_at, updated_at)
            VALUES (:id, :key, :value, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON CONFLICT(key) DO NOTHING
            """, nativeQuery = true)
    void insertIfNotExists(
            @Param("id") String id,
            @Param("key") String key,
            @Param("value") String value
    );
}
