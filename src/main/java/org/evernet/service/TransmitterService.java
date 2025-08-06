package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotAllowedException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Node;
import org.evernet.model.Transmitter;
import org.evernet.repository.TransmitterRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransmitterService {

    private final TransmitterRepository transmitterRepository;

    private final NodeService nodeService;

    public Transmitter create(String nodeIdentifier, String actorAddress, String creator) {
        Node node = nodeService.get(nodeIdentifier);

        if (!StringUtils.hasText(creator)) {
            if (!node.getOpen()) {
                throw new NotAllowedException();
            }
        }

        if (transmitterRepository.existsByNodeIdentifierAndActorAddress(nodeIdentifier, actorAddress)) {
            throw new ClientException(String.format("Transmitter is already registered for actor %s on node %s", actorAddress, nodeIdentifier));
        }

        Transmitter transmitter = Transmitter.builder()
                .nodeIdentifier(nodeIdentifier)
                .actorAddress(actorAddress)
                .creator(creator)
                .build();

        return transmitterRepository.save(transmitter);
    }

    public List<Transmitter> list(String nodeIdentifier, Pageable pageable) {
        return transmitterRepository.findByNodeIdentifier(nodeIdentifier, pageable);
    }

    public Transmitter get(String nodeIdentifier, String actorAddress) {
        Transmitter transmitter = transmitterRepository.findByNodeIdentifierAndActorAddress(nodeIdentifier, actorAddress);

        if (transmitter == null) {
            throw new NotFoundException(String.format("Transmitter not found for actor %s on node %s", actorAddress, nodeIdentifier));
        }

        return transmitter;
    }

    public Transmitter delete(String nodeIdentifier, String actorAddress) {
        Transmitter transmitter = get(nodeIdentifier, actorAddress);
        transmitterRepository.delete(transmitter);
        return transmitter;
    }
}
