package org.evernet.repository;

import org.evernet.model.ActorMessagingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorMessagingConfigRepository extends JpaRepository<ActorMessagingConfig, Long> {

    ActorMessagingConfig findByNodeIdentifierAndActorIdentifierAndConfigTypeAndNodeAddress(String nodeIdentifier, String actorIdentifier, ActorMessagingConfig.ConfigType configType, String nodeAddress);

    List<ActorMessagingConfig> findByNodeIdentifierAndActorIdentifier(String nodeIdentifier, String actorIdentifier);
}
