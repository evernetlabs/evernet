package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.evernet.exception.ClientException;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.ConnectionList;
import xyz.evernet.repository.ConnectionListRepository;
import xyz.evernet.request.ConnectionListCreationRequest;
import xyz.evernet.request.ConnectionListUpdateRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionListService {

    private final ConnectionListRepository connectionListRepository;

    public ConnectionList create(String nodeIdentifier, ConnectionListCreationRequest request, String username) {
        if (connectionListRepository.existsByNodeIdentifierAndUsernameAndName(nodeIdentifier, username, request.getName())) {
            throw new ClientException(String.format("Connection list %s already exists", request.getName()));
        }

        ConnectionList connectionList = ConnectionList.builder()
                .nodeIdentifier(nodeIdentifier)
                .username(username)
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return connectionListRepository.save(connectionList);
    }

    public List<ConnectionList> list(String nodeIdentifier, String username, Pageable pageable) {
        return connectionListRepository.findByNodeIdentifierAndUsername(nodeIdentifier, username, pageable);
    }

    public ConnectionList get(String nodeIdentifier, String username, String name) {
        ConnectionList connectionList = connectionListRepository.findByNodeIdentifierAndUsernameAndName(nodeIdentifier, username, name);

        if (connectionList == null) {
            throw new NotFoundException(String.format("Connection list %s not found", name));
        }

        return connectionList;
    }

    public ConnectionList update(String nodeIdentifier, String username, String name, ConnectionListUpdateRequest request) {
        ConnectionList connectionList = get(nodeIdentifier, username, name);
        connectionList.setDescription(request.getDescription());
        return connectionListRepository.save(connectionList);
    }

    public ConnectionList delete(String nodeIdentifeir, String username, String name) {
        ConnectionList connectionList = get(nodeIdentifeir, username, name);
        connectionListRepository.delete(connectionList);
        return connectionList;
    }

    public Boolean exists(String nodeIdentifier, String username, String name) {
        return connectionListRepository.existsByNodeIdentifierAndUsernameAndName(nodeIdentifier, username, name);
    }
}
