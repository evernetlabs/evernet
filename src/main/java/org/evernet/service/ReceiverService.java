package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotAllowedException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Node;
import org.evernet.model.Receiver;
import org.evernet.repository.ReceiverRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiverService {

    private final ReceiverRepository receiverRepository;

    private final NodeService nodeService;

    public Receiver create(String nodeIdentifier, String actorAddress, String creator) {
        Node node = nodeService.get(nodeIdentifier);

        if (!StringUtils.hasText(creator)) {
            if (!node.getOpen()) {
                throw new NotAllowedException();
            }
        }

        if (receiverRepository.existsByNodeIdentifierAndActorAddress(nodeIdentifier, actorAddress)) {
            throw new ClientException(String.format("Receiver is already registered for actor %s on node %s", actorAddress, nodeIdentifier));
        }

        Receiver receiver = Receiver.builder()
                .nodeIdentifier(nodeIdentifier)
                .actorAddress(actorAddress)
                .creator(creator)
                .build();

        return receiverRepository.save(receiver);
    }

    public List<Receiver> list(String nodeIdentifier, Pageable pageable) {
        return receiverRepository.findByNodeIdentifier(nodeIdentifier, pageable);
    }

    public Receiver get(String nodeIdentifier, String actorAddress) {
        Receiver receiver = receiverRepository.findByNodeIdentifierAndActorAddress(nodeIdentifier, actorAddress);

        if (receiver == null) {
            throw new NotFoundException(String.format("Receiver not found for actor %s on node %s", actorAddress, nodeIdentifier));
        }

        return receiver;
    }

    public Receiver delete(String nodeIdentifier, String actorAddress) {
        Receiver receiver = get(nodeIdentifier, actorAddress);
        receiverRepository.delete(receiver);
        return receiver;
    }
}
