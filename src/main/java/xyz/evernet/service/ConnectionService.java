package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import xyz.evernet.exception.ClientException;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.Connection;
import xyz.evernet.repository.ConnectionRepository;
import xyz.evernet.request.ConnectionCreationRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    private final ConnectionListService connectionListService;

    private final MongoTemplate mongoTemplate;

    public Connection create(String nodeIdentifier, String username, ConnectionCreationRequest request) {
        if (connectionRepository.existsByNodeIdentifierAndUsernameAndConnectionUserAddress(nodeIdentifier, username, request.getConnectionUserAddress())) {
            throw new ClientException(String.format("%s is already a connection", request.getConnectionUserAddress()));
        }

        Connection connection = Connection.builder()
                .nodeIdentifier(nodeIdentifier)
                .username(username)
                .connectionUserAddress(request.getConnectionUserAddress())
                .connectionLists(Collections.emptySet())
                .build();

        return connectionRepository.save(connection);
    }

    public List<Connection> list(String nodeIdentifier, String username, Pageable pageable) {
        return connectionRepository.findByNodeIdentifierAndUsername(nodeIdentifier, username, pageable);
    }

    public Connection get(String nodeIdentifier, String username, String connectionUserAddress) {
        Connection connection = connectionRepository.findByNodeIdentifierAndUsernameAndConnectionUserAddress(
                nodeIdentifier, username, connectionUserAddress
        );

        if (connection == null) {
            throw new NotFoundException(String.format("%s is not a connection", connectionUserAddress));
        }

        return connection;
    }

    public Connection delete(String nodeIdentifier, String username, String connectionUserAddress) {
        Connection connection = get(nodeIdentifier, username, connectionUserAddress);
        connectionRepository.delete(connection);
        return connection;
    }

    public Connection addToList(String nodeIdentifier, String username, String connectionUserAddress, String connectionListName) {
        if (!connectionListService.exists(nodeIdentifier, username, connectionListName)) {
            throw new NotFoundException(String.format("Connection list %s not found", connectionListName));
        }

        Connection connection = mongoTemplate.findAndModify(
                Query.query(
                        Criteria.where("nodeIdentifier").is(nodeIdentifier)
                                .and("username").is(username)
                                .and("connectionUserAddress").is(connectionUserAddress)
                ),
                new Update()
                        .addToSet("connectionLists", connectionListName)
                        .set("updatedAt", Instant.now()),
                Connection.class
        );

        if (connection == null) {
            throw new NotFoundException(String.format("%s is not a connection", connectionUserAddress));
        }

        return connection;
    }

    public Connection removeFromList(String nodeIdentifier, String username, String connectionUserAddress, String connectionListName) {
        Connection connection = mongoTemplate.findAndModify(
                Query.query(
                        Criteria.where("nodeIdentifier").is(nodeIdentifier)
                                .and("username").is(username)
                                .and("connectionUserAddress").is(connectionUserAddress)
                ),
                new Update()
                        .pull("connectionLists", connectionListName)
                        .set("updatedAt", Instant.now()),
                Connection.class
        );

        if (connection == null) {
            throw new NotFoundException(String.format("%s is not a connection", connectionUserAddress));
        }

        return connection;
    }

    public List<Connection> fetchForList(String nodeIdentifier, String username, String connectionListName, Pageable pageable) {
        return connectionRepository.findByNodeIdentifierAndUsernameAndConnectionListsContaining(nodeIdentifier, username, connectionListName, pageable);
    }
}
